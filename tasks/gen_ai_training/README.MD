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
