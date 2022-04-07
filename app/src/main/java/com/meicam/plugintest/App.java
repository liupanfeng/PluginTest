package com.meicam.plugintest;

import android.app.Application;
import android.content.Context;


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/3/4 21:22
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class App extends Application {

    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        AndroidOS.initConfig(this);
    }
}
