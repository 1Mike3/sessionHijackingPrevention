# Session Hijacking Prevention

## About
This repository is created to examine methods for the prevention of session hijacking trough IP-Geolocation, and Browser fingerprinting, as a basis for my bachelor's thesis.

## General Info
### Security Info
+ This Programm logs many infos that should not be logged in a usual setting.
+ The saving of the cookies is done in the frontend in a way that makes it easier to exfiltrate (which is part of the goal of the project). To enchance Security they should be written from the backend and the appropriate flags should be set.
### Configuration
>The Application can be configured as follows:
+ Through config.json
  + located in ressources/config
  + information about the parameters in configinfo
+ Through changing the DNS_NAME Parameter in appMain.js
  + the Attribute is on top of the file marked with //CONFIGURABLE
+ By changing the path from which the config file is loaded in ConfigManager.java
  + Also look out for the CONFIGURABLE comment

## Structure
