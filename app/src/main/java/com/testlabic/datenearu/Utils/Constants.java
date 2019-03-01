package com.testlabic.datenearu.Utils;

import com.google.firebase.auth.FirebaseAuth;

public class Constants {
    public static final String Notifications = "Notifications";
    public static final String Messages = "Messages";
    public static final String CHATS = "chats";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timeStamp";
    public static final String FEMALE = "female";
    public static final String MALE = "male";
    public static final String Questions = "Questions";
    public static final String DMIds = "DMIds";
    public static final String LikeInfo = "LikeInfo";
    public static final String Gifts = "Gifts";
    public static final String LikeBacks = "LikeBacks";
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
    public static String xPoints = "XPoints";
    public static String successfullySent = "successfullySent";
    public static String shownShowCaser = "shownShowCaser";
    public static String dateOfBirth = "dateOfBirth";
    public static String userGender = "userGender";
    public static String female = "female";
    public static String male = "male";
    public static String userIntrGender = "userIntrGender";
    public static String userPreferences = "userPreferences";
    public static String age = "age";
    public static String numeralAge = "numeralAge";
    public static String preferedGender = "preferedGender";
    public static int datePlacePoints = 20;
    public static int dateQuestionPoints = 10;
    public static String firstPack = "firstPack";
    public static String oneLine = "oneLine";
    public static int retryAttemptAmount = 150;
    public static int oneTimeMessageCost = 25;
    public static int attemptTestPoints = 100;
    public static int rewardAdPoints = 20;
    public static String blockList = "BlockList";
    public static String reportedUsers = "reportedUser";
    public static String comingFromNotif = "comingFromNotif";
    public static String matchAlgo = "matchAlgo";
    public static String directConvo = "directConvo";
    public static String isBlur = "isBlur";
    public static String setupQuestions = "setUpQuestions";
    public static String moveToLocationActivity = "moveToLocationActivity";
    public static String timeStamp = "timeStamp";
    public static String imageUrl2 = "imageUrl2";
    public static String imageUrl3 = "imageUrl3";
    public static String userDetailsOff = "userDetailsOffline";
    public static String newUserSetupDone = "newUserSetupDown";
    public static int wineAmount = 500;
    public static String regularWine = "Regular Wine";
    public static String premiumWine = "Premium Wine";
    public static String royalWine = "Royal Wine";
    public static String showDialog = "showDialog";
    public static String giftModel = "giftModel";
    
    public static final String SKU_100_drops = "100_drops";
    public static final String SKU_200_drops = "200_drops";
    public static final String SKU_500_drops = "500_drops";
    public static final String SKU_1000_drops = "1000_drops";
    public static String purchaseLogs = "purchaseLogs";
    
    public static String encrypt(String cityLabel) {
        
        return cityLabel.replace(", ", "_");
    }
    
    public static String decrypt(String value) {
        return value.replace("_", ", ");
    }
}
