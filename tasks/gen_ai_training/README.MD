*** PRESET ***
- To check a list of available models in DIAL AI: https://ai-proxy.lab.epam.com/openai/models  with header Api-Key=${YOUR_DIAL_API}
- To get a DIAL_API - in https://chat.lab.epam.com/  find REQUEST API KEY in the ottom of the screen 
or create request https://support.epam.com/ess?id=sc_cat_item&table=sc_cat_item&sys_id=910603f1c3789e907509583bb001310c 
or connect to SupportDIAL@epam.com
- EPAM VPN must be connected

# MODULE 1
## API
Semantic Kernel request:
```
curl --location 'http://localhost:8080/sk/process-message' \
--header 'Content-Type: application/json' \
--data '{
    "input": "Is the Earth flat?",
    "temperature" : 1.0,
    "metadata" : false
}'
```
where  "temperature"  and "metadata" are optional

Direct OpenAI request:
POST http://localhost:8080/openai/process-message
with body
{
"input": "Get a list of the first 3American Presidents"
}
NO optional parameters
## Experiment
* Example of different request with different temperature on gpt-35-turbo-0125:
```
{
  "input": "Is the Earth flat?",
  "temperature" : 0.5,
  "metadata" : false
}

{
"response": [
"No, the Earth is not flat. It is an oblate spheroid, meaning it is mostly spherical but slightly flattened at the poles and bulging at the equator due to its rotation."
],
"status": "success"
}

{
"input": "Is the Earth flat?",
"temperature" : 0.9,
"metadata" : false
}

{
"response": [
"No, the Earth is not flat. It is an oblate spheroid, meaning it is mostly spherical but slightly flattened at the poles and bulging at the equator."
],
"status": "success"
}

{
"input": "Is the Earth flat?",
"temperature" : 0.1,
"metadata" : false
}

{
"response": [
"No, the Earth is not flat. It is an oblate spheroid, meaning it is mostly spherical but slightly flattened at the poles and bulging at the equator. This shape has been confirmed through various scientific observations and measurements."
],
"status": "success"
}

{
"input": "Is the Earth flat?",
"temperature" : 1.0,
"metadata" : false
}

{
"response": [
"No, the Earth is not flat. It is an oblate spheroid, meaning it is mostly spherical but slightly flattened at the poles and bulging at the equator. The overwhelming scientific evidence supports the fact that the Earth is round."
],
"status": "success"
}
```

# Module 2 *
## API
```
curl --location 'http://localhost:8080/sk/history' \
--header 'Content-Type: application/json' \
--data '{
"input": "Any other options?",
"temperature" : 1.0,
"metadata" : false
}'
```
## Experiments
* Without history:
```
  {
  "input": "Let me describe our team: we have 2 back-end developers, 1 front-end developer, 1 QA and 1 BA.",
  "temperature" : 1.0,
  "metadata" : false
  }
  {
  "response": [
  "Your team composition sounds well-rounded and capable of handling various aspects of a project. The 2 back-end developers can focus on the functionality and server-side aspects, the front-end developer can work on the user interface and client-side experience, the QA can ensure quality and perform testing, and the BA can bridge the gap between stakeholders and the technical team. It's great to have a diverse skill set within your team!"
  ],
  "status": "success"
  }

{
"input": "Let me describe our team velocity: we can complete 1 feature on 13 points in one sprint or 2 features on 5 points each.",
"temperature" : 1.0,
"metadata" : false
}

{
"response": [
"Based on your description, it seems like your team has a variable velocity depending on the size of the features. On average, your team can complete 1 feature worth 13 points in one sprint or 2 features worth 5 points each. This means that your team's velocity can vary between 5 and 13 points per sprint. It would be helpful to track your team's velocity over multiple sprints to get a more accurate estimate of how much work your team can accomplish."
],
"status": "success"
}

{
"input": "Based on previous knowledge give me advice in two short lines. We have feture for 40 points. What should we do? ",
"temperature" : 1.0,
"metadata" : false
}

{
"response": [
"Focus on enhancing your product's key features to make it stand out, invest in marketing strategies to reach a wider audience and showcase its unique selling points. Additionally, gather feedback from users to continually improve and iterate on the features to maintain a competitive edge."
],
"status": "success"
}
```

* With history:
```
{
"input": "Let me describe our team: we have 2 back-end developers, 1 front-end developer, 1 QA and 1 BA.",
"temperature" : 1.0,
"metadata" : false
}

{
"response": [
"Your team consists of 5 members with diverse responsibilities:",
"",
"- 2 back-end developers who are responsible for the server-side development and logic of the application.",
"- 1 front-end developer who works on the user interface and client-side components of the application.",
"- 1 Quality Assurance (QA) professional who is responsible for ensuring the quality and functionality of the software through testing and bug tracking.",
"- 1 Business Analyst (BA) who acts as a liaison between the technical team and stakeholders, gathering and analyzing requirements to ensure the software meets business needs.",
"",
"This balanced team composition allows for efficient collaboration and ensures that all aspects of the software development process are covered."
],
"status": "success"
}

{
"input": "Let me describe our team velocity: we can complete 1 feature on 13 points in one sprint or 2 features on 5 points each.",
"temperature" : 1.0,
"metadata" : false
}

{
"response": [
"Based on the information provided, your team's velocity can be calculated as follows:",
"",
"- Completing 1 feature worth 13 points in one sprint equates to a velocity of 13 points per sprint.",
"- Completing 2 features worth 5 points each in one sprint equates to a total of 10 points completed in that sprint.",
"",
"Therefore, your team's average velocity is calculated as (13 points + 10 points) / 2 sprints = 11.5 points per sprint on average. This value can be used as a baseline for planning future sprints and estimating the team's capacity for upcoming work."
],
"status": "success"
}

{
"input": "Based on previous knowledge give me advice in two short lines. We have feture for 40 points. What should we do? ",
"temperature" : 1.0,
"metadata" : false
}

{
"response": [
"Break the feature into smaller, more manageable tasks for better tracking and completion. Prioritize and allocate tasks based on the team's velocity to ensure successful delivery within sprints."
],
"status": "success"
}
```
# Module 3 * Work with Different Models
## API
For getting model list
```
curl --location 'http://localhost:8080/dial/model'
```
For using model select model from previous response nd use value from 'reference' field as 'model' parameter
```
curl --location 'http://localhost:8080/sk/models' \
--header 'Content-Type: application/json' \
--data '{
    "input": "This response is incorrect. Use the first two of your responses. And create todo list based on this data.",
    "temperature" : 1.0,
    "metadata" : false,
    "model": "gpt-4-0613"
}'
```
## Experiment results
Model	|   Creativity  |   Coherence   |	Task Execution   |	Adaptability
--- | --- | ---  |--- |---
GPT-4 (Temp 1.0)    |	🌟🌟🌟🌟🌟 (Rich storytelling)   |	🌟🌟🌟🌟 (Slightly better than GPT-3.5) |	🌟🌟🌟🌟 (Initial mistake, but corrected)   |	🌟🌟🌟🌟 (Fixed after feedback)
GPT-3.5 (Temp 1.0)  |	🌟🌟🌟🌟 (Good, but not as refined) |	🌟🌟🌟 (Some inconsistencies)  |	🌟🌟🌟 (Good but not as precise)   |	🌟🌟🌟 (Adapts but not as smoothly)
GPT-3.5 (Temp 0.3)  |	🌟🌟 (Minimal creativity) |	🌟🌟🌟🌟🌟 (Very structured) |	🌟🌟🌟🌟🌟 (Highly reliable) |	🌟🌟🌟🌟🌟 (Very structured in corrections)
## Experiment listing
gpt-4-0613 temperature-1.0:
```
{
"input": "I`m planing perfect vacation for my family in Istambul. Let`s dream about it. Describe this vacations in 5 sentenses.",
"temperature" : 1.0,
"metadata" : false,
"model": "gpt-4-0613"
}
{
"response": [
"Our dream vacation in Istanbul begins with checking into a charming Ottoman-style hotel with a breathtaking view of the Bosphorus strait. Each day is filled with exploration of historic sites such as the iconic Hagia Sophia and the mesmerizing Blue Mosque. We stroll through the bustling Grand Bazaar, hunting for beautiful Turkish ceramics, exotic spices, and intricately woven carpets. Evenings are spent sampling delicious local cuisine, enjoying flavorful kebabs, sweet baklava, and traditional Turkish tea. The trip concludes with a serene sunset cruise along the Bosphorus, viewing the magnificent skyline and appreciating the blend of cultures in this unique city."
],
"status": "success"
}

{
"input": "Give me 3 more lines.",
"temperature" : 1.0,
"metadata" : false,
"model": "gpt-4-0613"
}
{
"response": [
"Settling into our luxurious suite at the Four Seasons hotel at the Bosphorus, the breathtaking panorama of Istanbul from our balcony fills us with anticipation of the delightful adventure that lies ahead. We would spend our days steeped in rich history as we explore the Hagia Sophia, the Blue Mosque and the Topkapi palace, followed by an unforgettable shopping experience at the lively Grand Bazaar. Evenings would be devoted to tasting Istanbul's gastronomical delights at rooftop restaurants, with the stunning cityscape as our backdrop. Our perfect vacation would include a relaxing cruise along the Bosphorus strait where we could soak in the mesmerizing fusion of European and Asian cultures that Istanbul embodies. Finally, watching a captivating performance of traditional Turkish Whirling Dervishes, our senses would be fully immersed in the mystical allure of this unique city."
],
"status": "success"
}
{
"input": "Summarize previous responses and create a short todo list.",
"temperature" : 1.0,
"metadata" : false,
"model": "gpt-4-0613"
}
{
"response": [
"You provided no previous responses that would provide detail or context for creating a to-do list for your Istanbul vacation. Please provide more information."
],
"status": "success"
}
{
"input": "This response is incorrect. Use the first two of your responses. And create todo list based on this data.",
"temperature" : 1.0,
"metadata" : false,
"model": "gpt-4-0613"
}
{
"response": [
"During our vacation in Istanbul, our days are filled with enticing Turkish cuisine from local vendors, visits to historic and mystical sites such as Hagia Sophia, the Blue Mosque and Topkapi Palace embracing the rich history and culture. We experience the thrill and vitality of the Grand Bazaar and Spice Market, filled with colourful stalls, exotic scents and bustle of local patrons. Breathtaking cruises along the Bosphorus at sunset reward us with stunning views of the city, and our nights end with enchanting traditional Turkish music and dance performances. The charming hospitality of the Turkish people and the unique blend of eastern and western influences make our Istanbul trip memorable. ",
"",
"To-do list:",
"1. Sample local cuisine",
"2. Visit Hagia Sophia, the Blue Mosque and Topkapi Palace",
"3. Explore the Grand Bazaar and Spice Market",
"4. Enjoy a cruise along the Bosphorus",
"5. Attend a traditional Turkish music and dance performance."
],
"status": "success"
}
```
gpt-35-turbo-1106 temperature-1.0:
```
{
"input": "I`m planing perfect vacation for my family in Istambul. Let`s dream about it. Describe this vacations in 5 sentenses.",
"temperature" : 1.0,
"metadata" : false,
"model": "gpt-35-turbo-1106"
}

{
"response": [
"We will stay in a beautiful, traditional Turkish hotel with stunning views of the Bosphorus. Our days will be filled with exploring the vibrant Grand Bazaar, taking a boat tour along the Bosphorus, and visiting the iconic Hagia Sophia and Blue Mosque. In the evenings, we will indulge in delicious Turkish cuisine at local restaurants and experience the lively nightlife in Taksim Square. We will also take a day trip to the charming Princes' Islands for a peaceful escape from the bustling city. Our perfect vacation in Istanbul will be a mix of rich culture, delicious food, and unforgettable experiences."
],
"status": "success"
}

{
"input": "Give me 3 more lines.",
"temperature" : 1.0,
"metadata" : false,
"model": "gpt-35-turbo-1106"
}

{
"response": [
"During our vacation in Istanbul, we will explore the rich history and culture by visiting iconic landmarks such as the Blue Mosque, Hagia Sophia, and Topkapi Palace. We will indulge in the vibrant local cuisine, from delicious kebabs and baklava to savory Turkish mezes and Turkish delight. A highlight of our trip will be a delightful cruise along the Bosphorus, marveling at the stunning views of the city straddling two continents. In the evenings, we will immerse ourselves in the lively and bustling atmosphere of the Grand Bazaar, seeking out unique souvenirs to take back home. Our family vacation in Istanbul promises to be a blend of adventure, relaxation, and unforgettable experiences."
],
"status": "success"
}

{
"input": "Summarize previous responses and create a short todo list.",
"temperature" : 1.0,
"metadata" : false,
"model": "gpt-35-turbo-1106"
}

{
"response": [
"Summary: Planning a perfect vacation for your family in Istanbul involves dreaming about the trip and picturing the perfect experience for everyone involved.",
"",
"To-do list:",
"1. Research attractions and activities in Istanbul that are family-friendly.",
"2. Plan accommodations that suit the needs and preferences of everyone in the family.",
"3. Create a day-to-day itinerary that includes a mix of cultural experiences, sightseeing, and relaxation.",
"4. Arrange transportation within Istanbul, ensuring convenience and comfort for the family.",
"5. Stay updated on travel advisories and safety precautions for Istanbul."
],
"status": "success"
}

{
"input": "This response is incorrect. Use the first two of your responses. And create todo list based on this data.",
"temperature" : 1.0,
"metadata" : false,
"model": "gpt-35-turbo-1106"
}
{
"response": [
"Istambul is a vibrant and beautiful city, perfect for a family vacation. You can explore the iconic sights such as the Hagia Sophia and the Blue Mosque, indulge in the delicious cuisine, and experience the rich culture and history of the city. Enjoy a leisurely Bosphorus cruise and visit the Grand Bazaar for some souvenir shopping. ",
"",
"To-do list:",
"1. Visit iconic sights such as Hagia Sophia and the Blue Mosque.",
"2. Indulge in the delicious cuisine and experience the rich culture and history of the city.",
"3. Take a leisurely Bosphorus cruise. ",
"4. Explore the Grand Bazaar for souvenir shopping.",
"5. Enjoy quality family time and create lasting memories."
],
"status": "success"
}
```
gpt-35-turbo-1106   temperature-0.3:
```
{
"input": "I`m planing perfect vacation for my family in Istambul. Let`s dream about it. Describe this vacations in 5 sentenses.",
"temperature" : 0.3,
"metadata" : false,
"model": "gpt-35-turbo-1106"
}
{
"response": [
"We will start our vacation by exploring the iconic landmarks of Istanbul, such as the Blue Mosque and Hagia Sophia, to immerse ourselves in the city's rich history and culture. We will take a leisurely cruise along the Bosphorus to admire the stunning views of the city's skyline and the beautiful waterfront mansions. Indulging in delicious Turkish cuisine at local restaurants and markets will be a highlight, as we savor traditional dishes like kebabs, baklava, and Turkish delight. We will also visit the Grand Bazaar for a unique shopping experience, where we can browse through a myriad of colorful textiles, ceramics, and spices. Finally, we will unwind and relax at a luxurious hotel with breathtaking views of the city, creating unforgettable memories of our dream vacation in Istanbul."
],
"status": "success"
}

{
"input": "Give me 3 more lines.",
"temperature" : 0.3,
"metadata" : false,
"model": "gpt-35-turbo-1106"
}

{
"response": [
"During our vacation in Istanbul, we will explore the historic landmarks such as the Hagia Sophia, Topkapi Palace, and the Blue Mosque. We will also indulge in the vibrant and diverse culinary scene, savoring traditional Turkish dishes and street food. Our family will take a relaxing cruise along the Bosphorus, enjoying the breathtaking views of the city and its iconic bridges. We will immerse ourselves in the rich culture of Istanbul by visiting the Grand Bazaar and experiencing the lively atmosphere of the Spice Bazaar. Additionally, we will take a day trip to the Princes' Islands, where we can enjoy the serene beaches and charming villages."
],
"status": "success"
}

{
"input": "Summarize previous responses and create a short todo list.",
"temperature" : 0.3,
"metadata" : false,
"model": "gpt-35-turbo-1106"
}
{
"response": [
"The perfect vacation in Istanbul for your family would include exploring the historic sites such as the Hagia Sophia and the Blue Mosque, taking a boat tour along the Bosphorus, indulging in delicious Turkish cuisine at local restaurants, visiting the Grand Bazaar for shopping, and experiencing a traditional Turkish bath at a hammam. ",
"",
"To-do list:",
"1. Visit the Hagia Sophia and the Blue Mosque",
"2. Take a boat tour along the Bosphorus",
"3. Enjoy Turkish cuisine at local restaurants",
"4. Explore the Grand Bazaar for shopping",
"5. Experience a traditional Turkish bath at a hammam"
],
"status": "success"
}

{
"input": "This response is incorrect. Use the first two of your responses. And create todo list based on this data.",
"temperature" : 0.3,
"metadata" : false,
"model": "gpt-35-turbo-1106"
}

{
"response": [
"Istanbul is a vibrant city that offers a perfect vacation for families. The trip could include visiting iconic landmarks such as the Hagia Sophia, Blue Mosque, and Topkapi Palace. Exploring the Grand Bazaar and Spice Market for unique souvenirs and local cuisine would be a must. A Bosphorus cruise to see the city from the water and a visit to the Princes' Islands for a peaceful retreat from the bustling city would make the vacation unforgettable. Finally, experiencing a traditional Turkish bath and enjoying a Turkish night show with folk music and dance would add cultural richness to the trip.",
"",
"To-do list:",
"1. Visit the Hagia Sophia, Blue Mosque, and Topkapi Palace",
"2. Explore the Grand Bazaar and Spice Market",
"3. Take a Bosphorus cruise and visit the Princes' Islands",
"4. Experience a traditional Turkish bath",
"5. Enjoy a Turkish night show with folk music and dance"
],
"status": "success"
}
```
# Module 4 Semantic Kernel Plugins
Plugin service that uses current GPS coordinates to calculate sailing distance and time to the nearest {Object}
based on the vessel's current geographic coordinates received from the plugin.
## API
```
curl --location 'http://localhost:8080/sk/plugins' \
--header 'Content-Type: application/json' \
--data '{
    "input": "Bay"
}'
```
Response example:
```
{
    "response:": [
        "1. Ajuruteua Beach  1284.5 km  142.7 hours",
        "2. Marajó Bay  1335.7 km  148.4 hours",
        "3. Pará Bay  1390.3 km  154.5 hours"
    ]
}
```
