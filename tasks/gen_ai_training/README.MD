*** PRESET ***
- To check a list of available models in DIAL AI: https://ai-proxy.lab.epam.com/openai/models  with header Api-Key=${YOUR_DIAL_API}
- To get a DIAL_API - in https://chat.lab.epam.com/  find REQUEST API KEY in the ottom of the screen 
or create request https://support.epam.com/ess?id=sc_cat_item&table=sc_cat_item&sys_id=910603f1c3789e907509583bb001310c 
or connect to SupportDIAL@epam.com
- EPAM VPN must be connected

* MODULE 1 API *
Semantic Kernel request:
POST http://localhost:8080/sk/process-message  with body 
{
  "input": "Is the Earth flat?",
  "temperature" : 1.0,
  "metadata" : false
}

where  "temperature"  and "metadata" are optional

Direct OpenAI request:
POST http://localhost:8080/openai/process-message
with body
{
"input": "Get a list of the first 3American Presidents"
}
NO optional parameters

* Example of different request with different temperature on gpt-35-turbo-0125:
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
