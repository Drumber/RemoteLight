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
| `type` | `arduino`, `remotelightserver`, `artnet`, `e131`, `virtualoutput`, `chain`, `multioutput` | Filters the list of output devices | `type=arduino` or `type=e131&type=chain`
| `id` | valid output id (case sensitive) | Get the output device with the given id | `id=My+arduino`
| `active` | | Show only active/connected output devices | `active`

## `/outputs/active` GET | PUT
**Description:** Active output device  
**Request Data:**  
- Set active output device: `{"active_output": "<device id>"}`  
- Deactivate output: `{"active_output": null}`

**Sample request:** 
```bash
curl -X PUT -H 'Content-Type: application/json' -d '{"active_output":"My arduino"}' http://localhost:8080/outputs/active
{"active_output": "My arduino"}
```

## `/effects` GET
**Description:** All available effects  
**Query parameters:**
| Parameter | Value | Description | Sample
| --------- | ----- | ----------- | ------
| `type` | `animations`, `scenes`, `music` | Show only the specified effect type | `type=animations` or `type=animations&type=scenes`
| `name` | name of an effect (case sensitive) | Show only effects with the specified name | `name=Rainbow` or `name=Rainbow&name=Scanner&name=...`

## `/effects/:type` GET
**Description:** All effects for the specified type  
**Value:** `animations`, `scenes`, `music`  
**Query parameters:** [see `/effects`](#effects-get)

## `/effects/:type/active` GET | PUT
**Description:** Active effect of the specified effect type  
**`:type` Parameter:** `animations`, `scenes` or `music`  
**Request Data:**  
- Set active effect: `{"active_effect": "<effect name>"}`  
- Disable effect: `{"active_effect": null}`

**Sample request:**
```bash
curl -X PUT -H 'Content-Type: application/json' -d '{"active_effect": {"name":"scanner"}}' http://localhost:8080/effects/animations/active
or
curl -X PUT -H 'Content-Type: application/json' -d '{"active_effect": "scanner"}' http://localhost:8080/effects/animations/active
```
**Sample output:**
```bash
{"active_effect": {
    "name": "Scanner",
    "options": [
      "animation.scanner.randomcolor",
      "animation.scanner.color",
      "animation.scanner.mode"
    ]}}
```

## TO DO
- [ ] get and set setting values
- [ ] set color for all pixels and individual pixels
- [ ] get general information (version, etc)
- [ ] toggle lua scripts
- [ ] (edit device data)
