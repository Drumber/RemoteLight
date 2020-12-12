# RemoteLight REST API

default port: `8080`  
default context path: `/`

## URLs
scheme: `<ip-address>:<port>/<context_path>/[url]`  
sample: `localhost:8080/[url]`

## `/outputs` GET
**Description:** A list of all output devices  
**Query parameters**:
| Parameter | Value | Description | Sample
| --------- | ----- | ----------- | ------
| `type` | `arduino`, `remotelightserver`, `artnet`, `e131`, `virtualoutput`, `chain`, `multioutput` | Filters the list of output devices | multiple params possible: `?type=e131&type=chain`
| `active` | | Show only active/connected output devices | `?active`

## `/outputs/get/:id` GET
**Description:** Get the output device with the given id

## `/outputs/active` GET | PUT
**Description:** Active output device  
**Data:**  
- Set active output device: `{"active_output": "<device id>"}`  
- Deactivate output: `{"active_output": null}`

**Sample request:** 
```bash
curl -X PUT -H 'Content-Type: application/json' -d '{"active_output":"My arduino"}' http://localhost:8080/outputs/active
{"active_output": "My arduino"}
```
