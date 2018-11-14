package com.testlabic.datenearu.Utils;

import com.google.firebase.auth.FirebaseAuth;

public class Constants {
    public static String userInfo = "userInfo";
    public static String location = "location";
    public static  String uid = FirebaseAuth.getInstance().getUid();
}
