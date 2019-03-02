package com.testlabic.datenearu.Utils;

import android.content.Context;
import android.graphics.Typeface;

public class Utils {
    
    public static Typeface SFProRegular(Context context)
    {
        return Typeface.createFromAsset(context.getAssets(), "fonts/SF-Pro-Display-Medium.otf");
    }
    
    public static Typeface SFPRoLight(Context context)
    {
        return Typeface.createFromAsset(context.getAssets(), "fonts/SF-Pro-Display-Light.otf");
    }
}
