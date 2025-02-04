# Session Hijacking Prevention

## About

### General
This repository is created to examine methods for the prevention of session hijacking trough IP-Geolocation, and browser fingerprinting, as a basis for my bachelor's thesis.<br>
This program is a weberver that hosts the testing website, does some bare bones user management and provides access to a restricted page for authorized users. While a user interacts with the website a security meachism is running in the background to dectect if a session token has been exfiltrated.
### Functionality and Intended Use

#### Usage
+ The webserver is supposed to be run on a server running some version of linux, but due to the portability provided by the jvm it can probably be run on different devices.
+ With `mvn package` the `JavaWebserverSecProject-1.0-SNAPSHOT-jar-with-dependencies.jar` file is created in the target directory, this one can be used to run the application across devices, the other one can be used for testing on device.
+ Example of a shell script to start the packaged application:
``` shell
#!/bin/bash
echo "start server script started!"
sudo java -jar ./target/JavaWebserverSecProject-1.0-SNAPSHOT-jar-with-dependencies.jar 
```
+ The program does not take arguments from the command line, only from the configuration file
+ The program was tested with the JDKs used, it is not certain if the program will still function with different versions.
+ The database can be started with a webserver enabled to configure and debug the database, the credenatials are in the source code, this should be turned off in the configuration file if being deployed.
+ The passwords used for testing are very basic and are not intended to provide security as these are not real user passwords. 
  + Still i will not disclose any passwords used, but a new hash can be generated using the "generatePasswordHash" class in the dev folder and replaced in the database.
  + (The way this application deals with passwords would of course not be good practice for a aplication in which real user data is processed, salting policies etc. are omitted for this proof of concept)
### Notable Changes
+ Switch from JSON user "database" to embeded H2 database for the persistant storage of user data
+ Not handling HTTPS in the program, instead outsourcing this completely to a reverse proxy to decouple it from the source code
>During the course of developing the program some ideas were introduced, some abandoned. 
Some artifacts of the abandoned changes are still left in the source code because they still might provide some value, but i tried to label them as obsolete as well as i could.
  

### Enviroment
>The hard-and software used for the development and deploying of the application
#### Development
+ Windows 11
+ IntelliJ IDEA 2023.3.8
+ SDK: Amazon Corretto version 17.0.10
#### Deployment
+ Raspberry Pi 5 , 8 GB
  + Debian bookworm V_ID_12   
+ openjdk 17.0.13
+ Other
  + nginx 1.22.1
  + ddclient 3.10.0
  + certbot 3.1.0
  + ufw 0.36.2

## Security Information
+ This Programm logs many infos that should not be logged in a production enviroment, client and server side.
+ The saving of the cookies is done in the frontend in a way that makes it easier to exfiltrate (which is part of the goal of the project). To enchance Security they should be written from the backend and the appropriate flags should be set.
  
## Configuration
>The Application can be configured as follows:
+ Through config.json
  + located in ressources/config
  + information about the parameters in configinfo
+ Through changing the DNS_NAME Parameter in appMain.js
  + the Attribute is on top of the file marked with //CONFIGURABLE
+ By changing the path from which the config file is loaded in ConfigManager.java
  + Also look out for the //CONFIGURABLE comment

#### Configuration File 
```json
{
"ON_DEVICE": false ,
*"HTTPS":false,
"ADDRESS":"localhost",
*"ADDRESS_SECURE":"localhost",
"PORT":3000,
*"PORT_SECURE":443,
"PATH_WS_STATIC":"/static",
"DB_WEBSERVER_ENABLED":true,
"PATH_RELATIVE_USER_DB":"./src/main/resources/persistence/userDB.json",
"PATH_RELATIVE_USER_DB_ON_DEVICE":"./target/classes/persistence/userDB.json",
*"PATH_RELATIVE_CERTIFICATE": "src/main/resources/certsTest/sec.test.crt",
*"PATH_RELATIVE_CERTIFICATE_ON_DEVICE": "./target/classes/certsTest/sec.test.crt",
*"PATH_RELATIVE_PRIVATE_KEY": "src/main/resources/certsTest/sec.test.key",
*"PATH_RELATIVE_PRIVATE_KEY_ON_DEVICE": "./target/classes/certsTest/sec.test.key",
"PATH_RELATIVE_USERSPACE_HTML":"./src/main/resources/static/restricted/userSpace.html",
"PATH_RELATIVE_USERSPACE_HTML_ON_DEVICE":"./target/classes/static/restricted/userSpace.html"
}
``` 
+ Due to major changes in the way HTTPS is handled for this project some of the previous parameters of the configuration file have become obsolete, they are now marked with a * prefix. 
+ They should not be changed (Mainly just the HTTPS parameter, the other ones just do not perform a function in the program anymore).
+ This change is due to outsourcing the HTTPS functionality to a reverse proxy (nginx).
+ A file named configinfo is located in the same folder as the configuration, and as the name would suggest contains more inromation related to the parameters.

## Structure
+ The **main source-code files** are located in *src>main>java>proj*
  + *config*
    + Manages classes for loading, validating and managing of the configuration files
  + *core*
    + Includes classes that manage the core functionality of the Webserver like the entery point in the Main class, reqest handelers and the session and user management function
  + *dev*
    + This folder can be mostly ignored by the reader, it contains classes and methods not used during runtime, instead helping me with some tasks during development
  + *entities*
    + Contains classes of which multiple objects are expected to be instanciated during runtime
  + *util*
    + Contains classes which are utilized during runtime but are not related to the core functionality of the program like deserializing JSON 
+ **Fronted, persistance and configuration** files are located in *src>main>resources*
  + *certsTest* 
    + was previously used for the https functionality baked in to the program, but was since abandoned (https now by nginx)
  + *config*
    + contains files related to the configuration of the program, the goal is to allow changing the programs behaviour when running from a jar on the deployment-device without recompiling.
  + *persistence*
    + persistently stored data about the users and their associated session data. The embeded H2 database is used now, some helper scrips to interact with the database are also located here. The json file is outdated and only left for me to quickly looking up the previous structure.
  + *static*
    + contains all the files related to client-side-execution .html,js.css
    + *restricted*
      + special folder containing the restricted ressource and not served like the other frontend files by the default request handler


### Program behaviour
+ On boot the program starts by loading the configuration, should it fail the program prints out a bunch of debug information and shuts down.
  + because this it is crucial that the path of the configuration file is correct, see "Configuration" section
+ Next the UserManagementSystem and the SessionManagementSystem are initialized, should there be a problem with the database for example the program will not start
+ Next Javalin is used to create a webserver based on some of the parameters from the configuration file, should it.
+ Finally the Request handelers are registered, some debug information is printed out and the application is ready to go. 


## Miscellaneous
### Nginx
>Because the HTTPS functionality has been moved out of the program i have provided an example nginx configuration which can be used as a secure reverse proxy.
(This is of course just an example and probably has to be modified for other devices)
``` nginx
# NGINX CONFIG

#user michi;
worker_processes auto;
pid /run/nginx.pid;
error_log /var/log/nginx/error.log;
include /etc/nginx/modules-enabled/*.conf;

events {
      worker_connections 768;
	# multi_accept on;
}


http {
server {
	listen 80;
	server_name sercuritytest-sh.at www.securitytest-sh.at;
	
location /test {
         root /home/michi/Desktop/server/nginx/test/;
         # index test.html;
     }
location / {
	return 301 https://$host$request_uri;
}


}
	# Main Server
server {
 
    server_name securitytest-sh.at www.securitytest-sh.at;
    
    location / {
        #include proxy_params;
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
     }

         listen [::]:443 ssl ipv6only=on; # managed by Certbot
    listen 443 ssl; # managed by Certbot
    ssl_certificate /etc/letsencrypt/live/securitytest-sh.at/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/securitytest-sh.at/privkey.pem; # managed by Certbot
    include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot

}

	##
	# Basic Settings
	##

	sendfile on;
	tcp_nopush on;
	types_hash_max_size 2048;
	# server_tokens off;

	# server_names_hash_bucket_size 64;
	# server_name_in_redirect off;

	include /etc/nginx/mime.types;
	default_type application/octet-stream;

	##
	# SSL Settings
	##

	ssl_protocols TLSv1 TLSv1.1 TLSv1.2 TLSv1.3; # Dropping SSLv3, ref: POODLE
	ssl_prefer_server_ciphers on;

	##
	# Logging Settings
	##

	access_log /var/log/nginx/access.log;

	gzip on;

	##
	# Virtual Host Configs
	##
	include /etc/nginx/conf.d/*.conf;
	include /etc/nginx/sites-enabled/*;

server {
    if ($host = securitytest-sh.at) {
        return 301 https://$host$request_uri;
    } # managed by Certbot


   listen 80;
   listen [::]:80;
 
    server_name securitytest-sh.at www.securitytest-sh.at;
    return 404; # managed by Certbot
}}

```