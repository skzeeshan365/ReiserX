# ReiserX driver
An android app for remote access to your device

For in-app features and documentation please visit [Documentation](http://reiserx.herokuapp.com/Documentation/)

It’s a remote service for android for accessing data remotely from an android device. We can install 
ReiserX driver app in an android device and then can access all the data of the device from ReiserX 
panel. 
Included Features  
> Access call logs  
> Access contacts  
> Perform CRUD operations on files  
> Get location  
> View notification history  
> Get device usage stats  
> Get list of installed apps  
> Get running apps  
> Capture picture  
> Take screenshot  
> Record audio  
> Executing python scripts  
> Get Device information  
> Get network information  
> And a lot more  
We can access all the data that is listed here using ReiserX  


> ReiserX performs all the operations in background without notifying the 
user 

Technology used:

It’s core component is a background service that is responsible for executing and managing all the 
operations. The service is designed in such a way that it cannot be killed permanently by Android 
Operating system or by stopping the app manually (e.g., FORCE_STOP), it is built using Alarm manager to 
start the service every 15 seconds whenever it receives a command from the server. There are multiple 
classes involved that keep running the service as long as the server commands it. Once all the operations 
have been executed and the service is no longer in use it will be killed and the next time it receives a 
command from server it will be restarted.  
Components involved in main service  
1. Android Notification Service (Notification access)  
2. FCM  
3. Broadcast receiver  
4. Alarm manager  

It performs all the operations according to commands received from the server (that is sent by 
ReiserX panel)  

Server:  
Firebase Realtime Database: Used to deliver commands and manage functioning of the app  
Firebase Firestore: Used to store data that is collected from device  
Firebase Storage: Used to store various types of files upload by ReiserX  
Firebase Authentication: for authenticating users  

Feature components:  
Accessibility service: to take screenshot, capture audio, capture image, block uninstall 
Notification service: To collect and store notification history, restart the main service 
FCM: for cloud messaging, critical commands delivery  
These are the main components of ReiserX driver that are necessary for functioning of the app. 
Other components are feature based (e.g. they are involved only in their respective feature 
operations not the the whole app for example, location service, Camera service, Python 
interpreter, etc.)  

You can also check its documentation: http://reiserx.herokuapp.com/Documentation/  
It has a wide range of components for all the features and explaining all of them is time 
consuming, So I recommend to use it for once and you will be able to understand it properly. 
In simple terms it’s like a Spyware to spy on other people but I had not created it for spying on 
anyone it was created for testing and educational purpose only, I do not recommend to use this 
service for spying on anyone  

Control panel for this app
ReiserX panel [repository](https://github.com/skzeeshan365/ReiserX.panel)  
