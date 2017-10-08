# *Frinder*

**Frinder** is an android app matches you to people nearby who are also looking to meet new people (not focussed on dating). e.g.: while you are waiting for a flight, having lunch alone, traveling by the train

Have you found yourself in the following situations when you were bored and could use some conversation? - Waiting for a flight alone - Taking the train by yourself - At a bar alone - Having a meal by yourself at your office cafe - At a fair where you barely know anyone

I'm sure everyone has some time been in such a situation where they were alone and wouldn't mind meeting a new person. However, the initial conversation or ice-breaker can be particularly awkward.

This app links to your Facebook profile and has a profile that contains basic information about you. On opening the app, you can see nearby people in the area and swipe right in case you'd like to meet them. Similar to Tinder, the app notifies you only when the other person reciprocates interest in meeting you. Some additions to this would be a chat functionality and the ability to create interest-based groups (e.g. an IoT focussed ad-hoc group at a computer science conference)..

## User Stories

The following **required** functionality are completed (Sprint 1):

* [ ] User can sign into Frinder using Facebook login
  * [ ] Query user profile data from facebook
  * [ ] Add User entry in firebase.
* [ ] User can view list of active people nearby (discover)
  * [ ] Ask for the required permissions (location)
  * [ ] Error handling (permissions, internet connectivity) 
* [ ] User can click on a person to view their profile (also allow sending a friend request)
* [ ] In a notification view, user can view their friend request(s) and contact details of accepted friends
  * [ ] Use Fragments + Tabs to differentiate between sent, received and accepted requests
  * [ ] Clicking on the phone number should open the phone/message app.
  * [ ] Add badging for unread notifications
* [ ] Fire push notifications for events
* [ ] Improve the UI of the app (icons, color scheme, launch screen)
* [ ] Create scripts to fake data in Firebase so as to help with testing

The following **optional** features are implemented (Sprint 2):

* [ ] Add a navigation bar
* [ ] User can edit/fill their profile (description, choose interests)
* [ ] User can edit their search settings (radius, gender, age, interest, turn off discoverability)
* [ ] User can send/receive messages in the app
* [ ] Add a messages view to view all your recent messages
* [ ] Support sharing contact details with accepted contacts
* [ ] Add a location service that runs in the background even when the app is not running
* [ ] Support sharing contact details with accepted contact
* [ ] Explore different list view options (card stack, single card)
* [ ] App badging (on notifications)
* [ ] App shortcuts
* [ ] Auto refresh

The following **bonus** features are implemented (Sprint 3):
* [ ] Find common attributes with the person
  * [ ] Mutual friends
  * [ ] Common interests
  * [ ] Similar visited places
* [ ] On meeting, support sharing contact details via NFC
* [ ] Choose profile photo using camera
* [ ] Actions such as Add on Facebook, Save to Contacts, etc
* [ ] Show people around you on a map
* [ ] Extending chat
  * [ ] VoIP calls
* [ ] [ambitious] Allow group meetups


## Wireframe

![Sprint 1 Wireframe](https://user-images.githubusercontent.com/1111292/31313212-b4bbb4d8-ab90-11e7-9346-af22cdf64056.JPG)

## Open-source libraries used

- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android
- [ButterKnife](https://github.com/JakeWharton/butterknife) - Bind Android views and callbacks to fields and methods 
