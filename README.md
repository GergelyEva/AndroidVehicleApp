<h3 align="center">Vehicle App</h3>
<p align="center">This app was designed based on the Android Lab project requirements.</p>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Contents</summary>
  <ol>
    <li><a href="#Description">Description</a></li>
    <li><a href="#MainActivity">MainActivity</a></li>
    <li><a href="#MapActivity">MapActivity</a></li>
    <li><a href="#GeopointItem">GeopointItem</a></li>
    <li><a href="#Database Helpers">Database Helpers</a></li>
  </ol>
</details>

### Description

This app was designed based on the Android Lab project requirements: <br>
-car diagnostics<br>
-request assistance <br>
-show current location on map <br>
-create database of nearby hotels, restaurants, car services <br>
-send SOS message to emergency contact, containg the location, weather condition, breakdown reason.<br>

<p align="right">(<a href="#readme-top">Back to Top</a>)</p>

### MainActivity

Manages the main functionality of the app, such as location tracking, weather fetching, sending SOS messages, and displaying breakdown information. MainActivity contains the following buttons and actions: <br>
- sos: Sends an SOS SMS message to a hardcoded emergency contact. The message contains the location (longitude and latitude), city, weather condition, and breakdown reason.<br>
- map: When the user clicks the map icon, the map opens. <br>
- diagnostics: When the user clicks on the diagnostics, breakdown reasons will show in a toast. The breakdown reason is stored in an array, and the actual reason is randomized.<br>
- help: When the user clicks on this icon, they require assistance, and a broadcast is sent to the Vehicle App Broadcast. A toast will show with an answer from a known person, something along the lines of "help is on the way."

<p align="right">(<a href="#readme-top">Back to Top</a>)</p>

### MapActivity

Shows a map with points of interest nearby. The selected points of interest are: hotels, restaurants, and car services. It contains icons so the user can check the list of nearby amenities.

<p align="right">(<a href="#readme-top">Back to Top</a>)</p>

### GeopointItem

A class to represent a point on the map with its name, coordinates, and address.

<p align="right">(<a href="#readme-top">Back to Top</a>)</p>

### Database Helpers

DatabaseHelper is a generic class for a generic database. The other DatabaseHelpers (Restaurant, Hotel, CarService) manage the SQLITE database for specific amenities. They have methods to insert and upgrade the specific databases.

<p align="right">(<a href="#readme-top">Back to Top</a>)</p>
