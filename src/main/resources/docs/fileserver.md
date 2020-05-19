# YoZo-Docs永中软件云服务在线文档


<a name="overview"></a>
## 概览

### 版本信息
*版本* : 0.1


### URI scheme
*域名* : localhost:9001  
*基础路径* : /


### 标签

* 下载Controller : Download Controller
* 文档Controller : Source File Controller
* 文档工具Controller : Util Controller




<a name="paths"></a>
## 资源

<a name="3bf94c34f54fdf719a2ab0891eaaa32c"></a>
### 下载Controller
Download Controller


<a name="localdownloadusingget"></a>
#### local下载接口
```
GET /api/file/download/{downloadId}/**
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**downloadId**  <br>*必填*|downloadId|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[ResponseEntity](#responseentity)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/api/file/download/string/**
```


##### HTTP响应示例

###### 响应 200
```json
{
  "body" : "object",
  "statusCode" : "string",
  "statusCodeValue" : 0
}
```


<a name="getdownloadurlusingget"></a>
#### 获取下载链接
```
GET /api/file/downloadUrl
```


##### 参数

|类型|名称|类型|
|---|---|---|
|**Query**|**appName**  <br>*可选*|string|
|**Query**|**fileInfos[0].fileName**  <br>*可选*|string|
|**Query**|**fileInfos[0].fileRefId**  <br>*可选*|integer (int64)|
|**Query**|**fileName**  <br>*可选*|string|
|**Query**|**timeOut**  <br>*可选*|integer (int64)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[ResponseEntity](#responseentity)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/api/file/downloadUrl
```


##### HTTP响应示例

###### 响应 200
```json
{
  "body" : "object",
  "statusCode" : "string",
  "statusCodeValue" : 0
}
```


<a name="downloadtoserverusingget"></a>
#### 下载文件到服务器制定目录
```
GET /api/file/serverDownload
```


##### 参数

|类型|名称|类型|
|---|---|---|
|**Query**|**appName**  <br>*可选*|string|
|**Query**|**fileInfos[0].fileName**  <br>*可选*|string|
|**Query**|**fileInfos[0].fileRefId**  <br>*可选*|integer (int64)|
|**Query**|**storageDir**  <br>*可选*|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[ResponseEntity](#responseentity)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/api/file/serverDownload
```


##### HTTP响应示例

###### 响应 200
```json
{
  "body" : "object",
  "statusCode" : "string",
  "statusCodeValue" : 0
}
```


<a name="b28bbf5409c542f23b3345d3d0257ff4"></a>
### 文档Controller
Source File Controller


<a name="getfilebyuploadusingpost"></a>
#### 真实上传文件
```
POST /api/file/upload
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Query**|**appName**  <br>*可选*||string|
|**Query**|**fileMd5**  <br>*可选*||string|
|**Query**|**taskId**  <br>*可选*||string|
|**Query**|**userMetadata**  <br>*可选*||string|
|**FormData**|**file**  <br>*必填*|file|file|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[ResponseEntity](#responseentity)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `multipart/form-data`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/api/file/upload
```


###### 请求 formData
```json
"file"
```


##### HTTP响应示例

###### 响应 200
```json
{
  "body" : "object",
  "statusCode" : "string",
  "statusCodeValue" : 0
}
```


<a name="getfilebysecuploadusingget"></a>
#### 判断是否可以秒传
```
GET /api/file/upload
```


##### 参数

|类型|名称|类型|
|---|---|---|
|**Query**|**appName**  <br>*可选*|string|
|**Query**|**fileMd5**  <br>*可选*|string|
|**Query**|**taskId**  <br>*可选*|string|
|**Query**|**userMetadata**  <br>*可选*|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[ResponseEntity](#responseentity)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/api/file/upload
```


##### HTTP响应示例

###### 响应 200
```json
{
  "body" : "object",
  "statusCode" : "string",
  "statusCodeValue" : 0
}
```


<a name="20e46a6227863fb60a3c28dc7e4384c5"></a>
### 文档工具Controller
Util Controller


<a name="getfilebyuploadusingpost_1"></a>
#### 获取文件md5值
```
POST /api/util/fileMd5
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**FormData**|**file**  <br>*必填*|file|file|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[ResponseEntity](#responseentity)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `multipart/form-data`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/api/util/fileMd5
```


###### 请求 formData
```json
"file"
```


##### HTTP响应示例

###### 响应 200
```json
{
  "body" : "object",
  "statusCode" : "string",
  "statusCodeValue" : 0
}
```




<a name="definitions"></a>
## 定义

<a name="responseentity"></a>
### ResponseEntity

|名称|说明|类型|
|---|---|---|
|**body**  <br>*可选*|**样例** : `"object"`|object|
|**statusCode**  <br>*可选*|**样例** : `"string"`|enum (100 CONTINUE, 101 SWITCHING_PROTOCOLS, 102 PROCESSING, 103 CHECKPOINT, 200 OK, 201 CREATED, 202 ACCEPTED, 203 NON_AUTHORITATIVE_INFORMATION, 204 NO_CONTENT, 205 RESET_CONTENT, 206 PARTIAL_CONTENT, 207 MULTI_STATUS, 208 ALREADY_REPORTED, 226 IM_USED, 300 MULTIPLE_CHOICES, 301 MOVED_PERMANENTLY, 302 FOUND, 302 MOVED_TEMPORARILY, 303 SEE_OTHER, 304 NOT_MODIFIED, 305 USE_PROXY, 307 TEMPORARY_REDIRECT, 308 PERMANENT_REDIRECT, 400 BAD_REQUEST, 401 UNAUTHORIZED, 402 PAYMENT_REQUIRED, 403 FORBIDDEN, 404 NOT_FOUND, 405 METHOD_NOT_ALLOWED, 406 NOT_ACCEPTABLE, 407 PROXY_AUTHENTICATION_REQUIRED, 408 REQUEST_TIMEOUT, 409 CONFLICT, 410 GONE, 411 LENGTH_REQUIRED, 412 PRECONDITION_FAILED, 413 PAYLOAD_TOO_LARGE, 413 REQUEST_ENTITY_TOO_LARGE, 414 URI_TOO_LONG, 414 REQUEST_URI_TOO_LONG, 415 UNSUPPORTED_MEDIA_TYPE, 416 REQUESTED_RANGE_NOT_SATISFIABLE, 417 EXPECTATION_FAILED, 418 I_AM_A_TEAPOT, 419 INSUFFICIENT_SPACE_ON_RESOURCE, 420 METHOD_FAILURE, 421 DESTINATION_LOCKED, 422 UNPROCESSABLE_ENTITY, 423 LOCKED, 424 FAILED_DEPENDENCY, 426 UPGRADE_REQUIRED, 428 PRECONDITION_REQUIRED, 429 TOO_MANY_REQUESTS, 431 REQUEST_HEADER_FIELDS_TOO_LARGE, 451 UNAVAILABLE_FOR_LEGAL_REASONS, 500 INTERNAL_SERVER_ERROR, 501 NOT_IMPLEMENTED, 502 BAD_GATEWAY, 503 SERVICE_UNAVAILABLE, 504 GATEWAY_TIMEOUT, 505 HTTP_VERSION_NOT_SUPPORTED, 506 VARIANT_ALSO_NEGOTIATES, 507 INSUFFICIENT_STORAGE, 508 LOOP_DETECTED, 509 BANDWIDTH_LIMIT_EXCEEDED, 510 NOT_EXTENDED, 511 NETWORK_AUTHENTICATION_REQUIRED)|
|**statusCodeValue**  <br>*可选*|**样例** : `0`|integer (int32)|





