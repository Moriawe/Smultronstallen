# Smultronstallen

-------------------------------

Android development - Group-project (12/11 - 2021)

-------------------------------

This is a project by 3 students on ITHS-Göteborg during the second month of a 2 year education to become appdevelopers. 

Smultronstallen will require the user to login/create an account and get authorized against Firebase Auth. 
The main activity in the app is the google maps where the user can mark out their own 'special places' and share it with their friends 
(or actually everyone who has the app since we never got to the point where you could add friends).

When you create a new place you can chose to share a picture, a short text about it and the address (will be printed out from google).
The app will then print who created this 'Smultronstalle' and when so it can update the list of new places.

-------------------------------

API KEYS
This project needs three keys to work. One for Google services and one for Firebase located in Project/app/ google-services.json AND firebase_admin.json
The third one is res/values/google_maps_api.xml

-------------------------------

My part

- The user should be able to login and logout
- Make a comment about a new place
- Save information for next time (API)
- See who created the place
- Add account
- Push the icon to get more info about the place
- Add a new place
- Make the new place private or public
- Redo a geopoint to an address
- Add an icon on the map for the place
- Remove place from the map
- Change your userprofile
- Do changes on your added place
