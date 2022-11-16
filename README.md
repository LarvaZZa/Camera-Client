# Camera-Client

This is the camera client that runs pure Java code. It is meant to:
  1) send notifications (when an activity is detected) to a phone
  2) stream live footage
  3) play any audio the user sends from their phone app to the client
  
This code needs to be used with the "Smart-Home-Security-System" app on my repository.

Prerequisites:
  1) create a location to temporarly store audio files received from the phone (for the client to play via speakers)
  2) create an FCM registration token via google
  3) create your google firebase server key (because the client needs to access the server to send the notification)
  4) enjoy!
