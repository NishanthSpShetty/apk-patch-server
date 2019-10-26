## patch-server
_Project born from hackathon_

server to store and generate patch files for apk files using bsdiff

----

bsdiff/bspatch is a library for generating delta patch files and applying patch files.

This library has two components

`bsdiff` generates the delta file or patch file for the given old file and new file, the generated file then later can be used to update old binaries to new one.

`bspatch` is to patch the old files using the patch/delta file generated by `bsdiff`. 

---
`patch-server` uses these library to provide the patch file for the new apk releases, also the server will act as APK repo.

## how it works ?
 * Upload apk file with meta data such as app name, version via rest endpoints
 * server will store the apk file and make an entry in the system
    and performs following task
    1. if uploaded app name is non existing, server will create new entry in the system and ack client request.
    2. if app is already exist in the system, system will make and entry for apk update, and keeps the new apk in repo, ack the user and trigger the event for patch generator.
    patch generator will query the system for all the releases prior to the current updates and starts generating delta/patch file for all the apk versions.
* When user requests for the update via REST endpointd, system will check the requested APK installed version and laest version in the repo, if present respond with patch file details if not repsond with proper message.
* if the server responds with the patch details, download the file from the server and using `bspatch` update the apk and reinstall.


---
The server can be used as apk repo for the app which are not hosted in store or app which manages the its own update and upgrades.

## Requirements
    * Java 1.8 or above
    * maven build tool
    * database of your choice (RDBMS, application uses PostgreSQL)

### Setup
* application.properties 

  provide the apk store(upload-base-dir) directory and patch store directory (patch-base-dir) in application.properties file.

* PostgreSQL/Any of your choice but update the specific driver and connection urls in properties file

    Application uses postgresql as database to store application meta data and file storage to store the apk files
        1. create the database of your choice and update application.properties file.
            create the following tables in database

## Tables Schema


### **file_table**
  `contains infromation on uploaded files`

| Column | Type | Nullable | Default |
| ------ | ---- | -------- | ------- |
| id| integer (serial)|    | 
|name | character varying(50) | 

### **application**
`contains information on application`

| Column | Type | Nullable | Default |
| ------ | ---- | -------- | ------- |
| id| integer (serial)|    | || 
| name | character varying(50) |   No |  |
| category | character varying(25) | No| |

### **apk**
`contains apk metadata of the application for all releases`

| Column | Type | Nullable | Default |
| ------ | ---- | -------- | ------- |
| id| integer (serial)|    | 
|app_id (references application[id])| integer|
|version| integer| 
|version_display_name | character varying(50)|
|release_date         | timestamp without time zone |
|apk_file_name        | character varying(50)       |

### **app_patch**
`apk patch/delta information of all apk versions`


| Column | Type | Nullable | Default |
| ------ | ---- | -------- | ------- |
| id| integer (serial)|    | 
|app_id          | integer|
|from_apk_id [references apk(id)]| integer|
|to_apk_id [references apk(id)]| integer|
|patch_file_name | character varying(50) | 






## To run
```
mvn install && java -jar target/genesis-server-0.0.1-SNAPSHOT.jar
```

NOTE : if you are using java 9 and above, the above command will throw ClassNotFoundException while looking for jaxb jars, as it is moved to J2EE, to run you can use the given jaxb jar with this project and run as follows
```
mvn clean install && java -cp jaxb-api-2.2.jar -jar target/genesis-server-0.0.1-SNAPSHOT.jar 
```

   go to your favorite browser and open `http://{host_ip}:8080/`. If you see repo screen, you are all set.


## endpoints
* upload APK endpoints
    
```
    POST /rest/v1/apk/app/{appName}/upload/file
    
    where
        {appName} is applicatio name, which is unique for all apk releases.
    
    Request Body
        upload file as multipart data,
    
    
    Response Body 
        status : 200
        file_id  #returns unique upload file id
```

* update apk meta endpoints
```
    POST /rest/v1/apk/app/{appName}/upload/meta/{file_id}
    
    Where
        {appName} is applicatio name, which is unique for all apk releases.
        {file_id} file id returned by file upload endpoint.

    Request Body
        {
            
            "name":__APP NAME__,
	         "versionId":__APP_VERSION__,
             "versionDisplayName":__DISPLAY_VERSION_STRING__,
	         "releaseDate":__APP_RELEASE_DATE__,
             "category:"__APP_CATEGORY__, ##games, music etc
        }

    Response Body 
        on succuss
            status : 200 
            {"message":"ok"}

        on failure
            500 : server internal error
            {error msg}
```

* Query for update
```
    GET /rest/v1/apk/app/{appName}/update/{version}
    
    Where
            {appName} is applicatio name
            {version}  current installed APK version 

    Request Body 
            NONE

    Response Body
            {
                "name":__APP NAME__,
	            "version:__APP_VERSION__,
	            "versionDisplayName":__DISPLAY_VERSION_STRING__,
	            "releaseDate":__APP_RELEASE_DATE__,
	            "patchId":__APP_PATCH_ID__,
                "patchFile":__PATCH_FILE_NAME,
            }
```


* download patch endpoint
```
    GET /rest/v1/apk/app/{appName}/patch/{patchId}
    
    Where
            {appName} is applicatio name
            {patchId}  patch id returned by the  update query api

    
    Request Body 
            NONE
        
    Response
            patch file
```


----
*NOTE : version is unique number generated for application, not a display string like 1.2.3*

----

## Patch application using bspatch
    server only contains the delta generation file, to use the application patcher refer another repo, a android application using the bspatch is found in repo ${COMING_SOON}




---
with the small changes to the  server or in uploading meta data, this can be used for any type of files, the server works on binary level.


