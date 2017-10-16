# *Frinder* [![Build status](https://travis-ci.org/frinder/frinder-app.svg?branch=master)](https://travis-ci.org/frinder/frinder-app/builds)

**Frinder** is an android app matches you to people nearby who are also looking to meet new people (not focussed on dating). e.g.: while you are waiting for a flight, having lunch alone, traveling by the train

Have you found yourself in the following situations when you were bored and could use some conversation? - Waiting for a flight alone - Taking the train by yourself - At a bar alone - Having a meal by yourself at your office cafe - At a fair where you barely know anyone. We are sure everyone has some time been in such a situation where they were alone and wouldn't mind meeting a new person. However, the initial conversation or ice-breaker can be particularly awkward.

This app links to your Facebook profile and has a profile that contains basic information about you. On opening the app, you can see nearby people in the area and sent them a notification incase you'd like to meet them. Similar to Tinder, the app notifies you only when the other person reciprocates interest in meeting you. Some additions to this would be a chat functionality and the ability to create interest-based groups (e.g. an IoT focussed ad-hoc group at a computer science conference).

## User Stories

**Sprint 1** (basic features):

* [X] User can sign into Frinder using Facebook login
  * [X] Query user profile data from facebook
  * [X] Add User entry in firebase.
* [X] User can view list of active people nearby (discover)
  * [X] Ask for the required permissions (location)
  * [X] Update the server with the user's location updates
  * [ ] Error handling (permissions, internet connectivity)
* [X] In a notification view, user can view their friend request(s) and contact details of accepted friends
  * [X] Use Fragments + Tabs to differentiate between sent, received and accepted requests
  * [X] Update the server with responses to the requests
* [X] Create scripts to fake data in Firebase so as to help with testing
* [X] Setup automated builds to catch build failures
* [ ] Improve the UI of the app (icons, color scheme, launch screen)
* [ ] Fire push notifications for events
* [ ] User can click on a person to view their profile (also allow sending a friend request)

**Sprint 2** (advanced features):

* [ ] User can edit/fill their profile (desc, choose interests)
* [ ] User can edit their search settings (radius, gender, age, interest, turn off discoverability)
* [ ] User can send/receive messages in the app
  * [ ] Add a messages view to view all your recent messages
  * [ ] Server sends notifications for received messages
* [ ] Improvements to app UI
  * [ ] Add a navigation drawer
  * [ ] Add in-app badging for unread notifications
  * [ ] Explore different list view options (card stack, single card)
  * [ ] App icon badging (on notifications)
* [ ] Support sharing contact details with accepted contacts
* [ ] Add a location service that runs in the background even when the app is not running
* [ ] Support sharing contact details with accepted contact
* [ ] Auto refresh UI (discover feed and notifications)

**Sprint 3** (bonus features):
* [ ] Find common attributes with the person
  * [ ] Mutual friends
  * [ ] Similar visited places
  * [ ] (very ambitious) Common interests
* [ ] Show people around you on a map
* [ ] Choose profile photo using camera
* [ ] Add app shortcuts
* [ ] Actions such as Add on Facebook, Save to Contacts, etc
* [ ] Focus on low-end devices and different screen sizes
* [ ] (ambitious )On meeting, support sharing contact details via NFC
* [ ] (very ambitious) Extending chat to VoIP calls
* [ ] (very ambitious) Allow group meetups


## Wireframe

[Sprint 1 Walkthrough]<img src='https://imgur.com/gBtGWj1.gif' width="600" />

![Sprint 1 Wireframe](https://user-images.githubusercontent.com/1111292/31313212-b4bbb4d8-ab90-11e7-9346-af22cdf64056.JPG)

## Open-source libraries used

- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android
- [ButterKnife](https://github.com/JakeWharton/butterknife) - Bind Android views and callbacks to fields and methods
