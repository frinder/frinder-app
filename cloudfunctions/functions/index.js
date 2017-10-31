// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');

//GeoFire
var GeoFire = require('geofire');

//Firebase
var Firebase = require('firebase');

admin.initializeApp(functions.config().firebase);

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.updateUser = functions.firestore
  .document('users/{userId}')
  .onUpdate(event => {
    // Get an object representing the document
    var newValue = event.data.data();

    // ...or the previous value before this update
    var previousValue = event.data.previous.data();

    var userName = newValue.name;
    var userId = newValue.id;
    var newLat = newValue.location[0];
    var newLon = newValue.location[1];
    var previousLat = previousValue.location[0];
    var previousLon = previousValue.location[1];

    if (newLat == previousLat && newLon == previousLon) {
        console.log('No location change for ' + userName + '(' + userId + ')');
    }
    else {
      //add value to GeoFire

      // Create a Firebase reference where GeoFire will store its information
      //var dataRef = new Firebase("https://projectfrinder.firebaseio.com/users_location");
      var dbRef = admin.database().ref('/users_location');

      // Create a GeoFire index
      var geoFire = new GeoFire(dbRef);

      //var key = event.params.test;
      var location = [newLat, newLon];

      geoFire.set(userId, location).then(() => {
         console.log('GeoFire Update successful for ' + userName + '(' + userId + ')');
      }).catch(error => {
         console.log(error);
      });
    }

    return true;
});

exports.createUser = functions.firestore
  .document('users/{userId}')
  .onCreate(event => {
    // Get an object representing the document
    var value = event.data.data();

    var userName = value.name;
    var userId = value.id;
    var lat = value.location[0];
    var lon = value.location[1];

    //add value to GeoFire

    // Create a Firebase reference where GeoFire will store its information
    var dbRef = admin.database().ref('/users_location');

    // Create a GeoFire index
    var geoFire = new GeoFire(dbRef);

    //var key = event.params.test;
    var location = [lat, lon];

    geoFire.set(userId, location).then(() => {
       console.log('GeoFire Update successful for ' + userName + '(' + userId + ')');
    }).catch(error => {
         console.log(error);
    });

    return true;
});

exports.deleteUser = functions.firestore
  .document('users/{userId}')
  .onDelete(event => {
    // Get an object representing the document
    var value = event.data.previous.data();

    var userName = value.name;
    var userId = value.id;

    //remove value from GeoFire

    // Create a Firebase reference where GeoFire will store its information
    var dbRef = admin.database().ref('/users_location');

    // Create a GeoFire index
    var geoFire = new GeoFire(dbRef);

    geoFire.remove(userId).then(() => {
       console.log('GeoFire Delete successful for ' + userName + '(' + userId + ')');
    }).catch(error => {
         console.log(error);
    });

    return true;
});
