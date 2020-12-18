# RemoteLight REST API

default port: `8080`  
Port can be changed in settings (requires a restart of the REST API web server).  
The REST API web server can be enabled or disabled in the settings (enabled by default).

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
**Request:**  
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
**`:type` Parameter:** `animations`, `scenes` or `music`  
**Query parameters:** [see `/effects`](#effects-get)

## `/effects/:type/active` GET | PUT
**Description:** Active effect of the specified effect type  
**`:type` Parameter:** `animations`, `scenes` or `music`  
**Request:**  
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

## `/color` GET | PUT
**Description:** Get and set the color for the whole strip  
**Request:**  
- R,G,B values: `{"red":<0..255>,"green":<0..255>,"blue":<0..255>}`
- RGB integer value: `{"rgb":<RGB values as 4-byte Integer>}`
- HEX value: `{"hex":"<HEX color>"}`

**Example:**
```bash
curl -X PUT -H 'Content-Type: application/json' -d '{"red":255,"green":0,"blue":0}' http://localhost:8080/color
{
  "rgb": -65536,
  "red": 255,
  "green": 0,
  "blue": 0
}

curl -X PUT -H 'Content-Type: application/json' -d '{"rgb":-16776961}' http://localhost:8080/color
{
  "rgb": -16776961,
  "red": 0,
  "green": 0,
  "blue": 255
}


curl -X PUT -H 'Content-Type: application/json' -d '{"hex":"#00FF00"}' http://localhost:8080/color
{
  "rgb": -16711936,
  "red": 0,
  "green": 255,
  "blue": 0
}
```

## `/color/pixel` GET | PUT
**Description:** Get and set the color for each pixel individually  
**Request:**  
A JSON array that contains for each pixel a JSON object. The JSON objects look like described in [`/color`](#color-get--put).  
**Example:**
```bash
curl -X PUT -H 'Content-Type: application/json' -d '[{"hex":"#00FF00"},{"red":255,"green":0,"blue":0},{"rgb":-16777216}, ... ]' http://localhost:8080/color/pixel
[
  {
    "rgb": -16711936,
    "red": 0,
    "green": 255,
    "blue": 0,
    "index": 0
  },
  {
    "rgb": -65536,
    "red": 255,
    "green": 0,
    "blue": 0,
    "index": 1
  },
  {
    "rgb": -16777216,
    "red": 0,
    "green": 0,
    "blue": 0,
    "index": 2
  },
  ...
]

```

## `/settings` GET | PUT
**Description:** Get and edit setting values  
**Query parameters:**  
| Parameter | Value | Description | Sample
| --------- | ----- | ----------- | ------
| `id` | valid setting id (case sensitive) | Get the setting element with the given id | `id=ui.style`

**Request:**  
To edit one or more settings, you must send the entire serialized settings array with the edited values. It is also possible to send only a single JSON object if you only want to edit a single setting.  
**Change only `value` (or `selected` for SettingSelection), `name`, `description` parameters. Changing other parameters can lead to errors.**

**Example:**
```bash
1. GET the setting element
curl -X GET -H 'Content-Type: application/json' http://localhost:8080/settings?id=animations.speed
[
  {
    "SETTING_TYPE": "SettingObject",
    "OBJECT_TYPE": "java.lang.Integer",
    "VALUES": {
      "value": 20,
      "name": "",
      "id": "animations.speed",
      "description": "",
      "category": "Intern"
    }
  }
]

2. PUT the setting element with the updated value (100)
curl -X PUT -H 'Content-Type: application/json' -d '[{"SETTING_TYPE":"SettingObject","OBJECT_TYPE":"java.lang.Integer","VALUES":{"value":100,"name":"","id":"animations.speed","description":"","category":"Intern"}}]' http://localhost:8080/settings
[
  {
    "SETTING_TYPE": "SettingObject",
    "OBJECT_TYPE": "java.lang.Integer",
    "VALUES": {
      "value": 100,
      "name": "",
      "id": "animations.speed",
      "description": "",
      "category": "Intern"
    }
  }
]

Alternatively as a single JSON object:
curl -X PUT -H 'Content-Type: application/json' -d '{"SETTING_TYPE":"SettingObject","OBJECT_TYPE":"java.lang.Integer","VALUES":{"value":100,"name":"","id":"animations.speed","description":"","category":"Intern"}}' http://localhost:8080/settings
```

## TO DO
- [x] get and set setting values
- [x] set color for all pixels and individual pixels
- [x] get general information (version, etc)
- [ ] toggle lua scripts
- [ ] (edit device data)
