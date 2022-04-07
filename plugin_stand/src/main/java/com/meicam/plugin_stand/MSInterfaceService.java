package com.meicam.plugin_stand;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/3/4 14:03
 * @Description : 插件Service的标准
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public interface MSInterfaceService {
    public void onCreate();

    public void onStart(Intent intent, int startId);

    public int onStartCommand(Intent intent, int flags, int startId);

    public void onDestroy();

    public void onConfigurationChanged(Configuration newConfig);

    public void onLowMemory();

    public void onTrimMemory(int level);

    public IBinder onBind(Intent intent);

    public boolean onUnbind(Intent intent);

    public void onRebind(Intent intent);

    public void onTaskRemoved(Intent rootIntent);

    public void attach(Service proxyService);
}
