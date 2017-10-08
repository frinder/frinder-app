# *Frinder*

**Frinder** is an android app that <fill out>.

## User Stories

The following **required** functionality are completed (Sprint 1):

* [ ] User can sign into Frinder using Facebook login
  * [ ] Query user prfile data from facebook
  * [ ] Add User entry in firebase.
* [ ] User can view list of active people nearby (discover) (stop discovering after 15 minutes) 
  * [ ] Ask for the required permissions (location)
  * [ ] Error handling (permissions, internet connectivity) 
* [ ] User can click on a person to view their profile (also allow sending a friend request)
* [ ] In a notification view, user can view their friend request(s) and contact details of accepted friends
  * [ ] Use Fragments + Tabs to differentiate between sent, received and accepted requests
  * [ ] Clicking on the phone number should open the phone/message app.
* [ ] Fire push notifications for events
* [ ] Improve the UI of the app (icons, color scheme, launch screen)

The following **optional** features are implemented (Sprint 2):

* [ ] User can edit/fill their profile (description, choose interest) [PUT /user]
* [ ] User can edit their settings (radius)
* [ ] Add a location service that runs in the background even when the app is not running
* [ ] Show people around you on a map
* [ ] User can send/receive messages in the app
* [ ] Allow user to filter/discover based on interests
* [ ] Choose profile photo using camera?
* [ ] App badging (on notifications)
* [ ] App shortcuts
* [ ] Auto turn off (based on location and/or time)

The following **bonus** features are implemented (Sprint 3):
* [ ] Include Facebook interests as part of user filtering (may need some grouping algorithm to match interests)
* [ ] Smart sort algorithm?
* [ ] Allow meeting multiple (setup groups? based on interest? Ability to close groups?)
* [ ] Include LinkedIn profile (have a professional mode aimed at conferences?)

## Video Walkthrough

## Open-source libraries used

- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android
- [ButterKnife](https://github.com/JakeWharton/butterknife) - Bind Android views and callbacks to fields and methods 
