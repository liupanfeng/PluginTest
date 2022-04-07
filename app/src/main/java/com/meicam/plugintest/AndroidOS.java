package com.meicam.plugintest;

import android.content.Context;
import android.os.Build;

public class AndroidOS {
    public static boolean USE_SCOPED_STORAGE;
    public static void initConfig(Context context){
        //android11的适配版本
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            USE_SCOPED_STORAGE = true;
        }
    }
}
