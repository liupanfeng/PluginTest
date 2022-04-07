package com.meicam.plugindemo;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PluginService extends BaseService {


    public PluginService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
    }
}