package com.testlabic.datenearu.Utils;

import com.google.firebase.auth.FirebaseAuth;

public class Constants {
    public static final String Notifications = "Notifications";
    public static final String Messages = "Messages";
    public static final String CHATS = "chats";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timeStamp";
    public static String userInfo = "userInfo";
    public static String location = "location";
    public static  String uid = FirebaseAuth.getInstance().getUid();
    public static String clickedUid = "clickedUid";
    public static String questions = "questions";
    public static String score = "matchScore";
    public static String markedOpt = "markedOpt";
    public static String unread = "unread";
    public static String cityLabels = "cityLabels";
    public static String requestMessages = "requestMessages";
    public static String chatName = "chatName";
    public static String cityLabel = "cityLabel";
    public static String refresh = "refresh";
    public static String imageUrl = "imageUrl";
    public static String read = "read";
    public static String notifCount = "notifCount";
    public static String contacts = "contacts";
    public static String sendToUid = "sendToUid";
    public static String sendToName = "sendToName";
    public static String messageDelivered = "messageDelivered";
    public static String status = "status";
    public static String online = "online";
    public static String offline = "offline";
    public static String usersStatus = "usersStatus";
}
