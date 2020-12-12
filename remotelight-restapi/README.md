# RemoteLight REST API

default port: `8080`  
default context path: `/`

## URLs
scheme: `<ip-address>:<port>/<context_path>/[url]`  
sample: `localhost:8080/[url]`

## `/outputs`
**Description:** A list of all output devices  
**Query parameters**:
| Parameter | Value | Description | Sample
| --------- | ----- | ----------- | ------
| `type` | `arduino`, `remotelightserver`, `artnet`, `e131`, `virtualoutput`, `chain`, `multioutput` | Filters the list of output devices | multiple params possible: `?type=e131&type=chain`
| `active` | | Show only active/connected output devices | `?active`

## `/output/:id`
**Description:** Get the output device with the given id