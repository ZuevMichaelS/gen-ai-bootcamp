package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.model.VectorSearchResponse;
import io.qdrant.client.PointIdFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.SearchPoints;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

/**
 * Service class for processing text into embeddings and interacting with Qdrant for vector storage and retrieval.
 * <p>
 * This service converts text into embeddings using Azure OpenAI and saves these vectors in a Qdrant collection.
 * It also provides functionality to search for similar vectors based on input text.
 */

@Slf4j
@Service
@AllArgsConstructor
public class SimpleVectorActions {
    public static final String DEFAULT_COLLECTION_NAME = "demo_collection";
    private static final String INFO_KEY = "info";
    private final OpenAIAsyncClient openAIAsyncClient;
    private final QdrantClient qdrantClient;

    /**
     * Processes the input text into embeddings, transforms them into vector points,
     * and saves them in the Qdrant collection.
     *
     * @param text the text to be processed into embeddings
     * @throws ExecutionException if the vector saving operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    @SneakyThrows
    public void processAndSaveText(String collectionName, String text){
        var embeddings = getEmbeddings(text);
        var points = new ArrayList<List<Float>>();
        embeddings.forEach(
                embeddingItem -> {
                    var values = new ArrayList<>(embeddingItem.getEmbedding());
                    points.add(values);
                });

        var pointStructs = new ArrayList<PointStruct>();
        points.forEach(point -> {
            var pointStruct = getPointStruct(point, text);
            pointStructs.add(pointStruct);
        });

        saveVector(collectionName, pointStructs);
    }

    /**
     * Searches the Qdrant collection for vectors similar to the input text.
     * <p>
     * The input text is converted to embeddings, and a search is performed based on the vector similarity.
     *
     * @param text the text to search for similar vectors
     * @return a list of scored points representing similar vectors
     * @throws ExecutionException if the search operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public List<VectorSearchResponse> search(String text, String collectionName, int responseLimit) throws ExecutionException, InterruptedException {
        var embeddings = retrieveEmbeddings(text);
        var qe = new ArrayList<Float>();
        embeddings.block().getData().forEach(embeddingItem ->
                qe.addAll(embeddingItem.getEmbedding())
        );
        var searchResult = qdrantClient
                .searchAsync(
                        SearchPoints.newBuilder()
                                .setCollectionName(collectionName)
                                .addAllVector(qe)
                                .setWithPayload(enable(true))
                                .setLimit(responseLimit)
                                .build())
                .get();
        return searchResult.stream().map(scoredPoint ->
             VectorSearchResponse.builder()
                    .uuid(scoredPoint.getId().getUuid())
                    .score(scoredPoint.getScore())
                    .text(scoredPoint.getPayloadMap().get(INFO_KEY).getStringValue())
                    .build())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the embeddings for the given text using Azure OpenAI.
     *
     * @param text the text to be embedded
     * @return a list of {@link EmbeddingItem} representing the embeddings
     */
    public List<EmbeddingItem> getEmbeddings(String text) {
        var embeddings = retrieveEmbeddings(text);
        return embeddings.block().getData();
    }

    /**
     * Creates a new collection in Qdrant with specified vector parameters.
     *
     * @throws ExecutionException if the collection creation operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    @PostConstruct
    public void createCollection() throws ExecutionException, InterruptedException {
        createCollection(DEFAULT_COLLECTION_NAME);
    }

    public void createCollection(String collectionName) throws ExecutionException, InterruptedException {
        var collectionsFuture = qdrantClient.listCollectionsAsync();

        if (!collectionsFuture.isDone()) {
            collectionsFuture.get();
        }

        var collections = collectionsFuture.get();
        if (collections.contains(collectionName)) {
            log.info("Collection already exists: [{}]", collectionName);
        } else {
            var createResult = qdrantClient.createCollectionAsync(
                    collectionName,
                    VectorParams.newBuilder()
                            .setDistance(Collections.Distance.Cosine)
                            .setSize(1536)
                            .build()
            ).get();
            log.info("Collection created: [{}]", createResult.getResult());
        }
    }

    /**
     * Saves the list of point structures (vectors) to the Qdrant collection.
     *
     * @param pointStructs the list of vectors to be saved
     * @throws InterruptedException if the thread is interrupted during execution
     * @throws ExecutionException if the saving operation fails
     */
    private void saveVector(String collectionName, ArrayList<PointStruct> pointStructs) throws InterruptedException, ExecutionException {
        var updateResult = qdrantClient.upsertAsync(collectionName, pointStructs).get();
        log.info(updateResult.getStatus().name());
    }

    /**
     * Constructs a point structure from a list of float values representing a vector.
     *
     * @param point the vector values
     * @return a {@link PointStruct} object containing the vector and associated metadata
     */
    private PointStruct getPointStruct(List<Float> point, String text) {
        return PointStruct.newBuilder()
                .setId(PointIdFactory.id(UUID.randomUUID()))
                .setVectors(vectors(point))
                .putAllPayload(Map.of(INFO_KEY, value(text)))
                .build();
    }

    /**
     * Retrieves the embeddings for the given text asynchronously from Azure OpenAI.
     *
     * @param text the text to be embedded
     * @return a {@link Mono} of {@link Embeddings} representing the embeddings
     */
    private Mono<Embeddings> retrieveEmbeddings(String text) {
        var qembeddingsOptions = new EmbeddingsOptions(List.of(text));
        return openAIAsyncClient.getEmbeddings("text-embedding-ada-002", qembeddingsOptions);
    }
}
