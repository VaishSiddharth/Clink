'use strict';


const functions = require('firebase-functions');

const admin = require('firebase-admin')
admin.initializeApp();

//start writing functions here

exports.sendGiftPush =functions.database.ref('/Gifts/{userId}/unread/{pushId}').onWrite((change, context) => {

console.log("Does data even exist ? " + (change.after.val()!==null));
if(change.after.val!==null)
{
 var topic = context.params.userId;
// See the "Defining the message payload" section below for details
// on how to define a message payload.
console.log("Receiving user " + topic);
var name = change.after.val().giftSendersName;
var body = name + " sent you a gift!";

var payload = {
  notification: {
    title: "You got drops!",
    body: body,
    sound: "default"
  }
};

return admin.messaging().sendToTopic(topic, payload);
 
}
});

//notif for unread messages;

exports.sendUnreadMessagesPush =functions.database.ref('/chatsunread/{userId}/{senderId}/{pushId}').onWrite((change, context) => {

console.log("Does data even exist ? " + (change.after.val()!==null));
if(change.after.val!==null)
{
 var topic = context.params.userId;
// See the "Defining the message payload" section below for details
// on how to define a message payload.
console.log("Receiving user " + topic);
var name = change.after.val().sendersName;
var body = change.after.val().message;

var sendUid = change.after.val().sentFrom;

var sendName = change.after.val().sendToName;

var payload = {
  notification: {
    title: name,
    body: body,
    sound: "default",
    click_action: "chatFullScreen"
  },
  data:
  {
  	sendToUid : sendUid ,
  	sendToName : sendName 
  }
};

return admin.messaging().sendToTopic(topic, payload);
 
}
});


//notif for notification messages;

exports.sendNotificationsPush =functions.database.ref('/Notifications/{userId}/unread/{pushId}').onWrite((change, context) => {

console.log("Does data even exist ? " + (change.after.val()!==null));
if(change.after.val!==null)
{
 var topic = context.params.userId;
// See the "Defining the message payload" section below for details
// on how to define a message payload.
console.log("Receiving user " + topic);
var body = change.after.val().message;

var payload = {
  notification: {
    title: "New notification!",
    body: body,
    sound: "default"
  }
 
};

return admin.messaging().sendToTopic(topic, payload);
 
}
});
