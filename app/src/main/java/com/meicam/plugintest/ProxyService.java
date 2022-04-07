package com.meicam.plugintest;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.meicam.plugin_stand.MSInterfaceActivity;
import com.meicam.plugin_stand.MSInterfaceService;

import java.lang.reflect.Constructor;

/**
 * 代理Service 用于加载插件Service
 */
public class ProxyService extends Service {

    public static final String KEY_SERVICE_NAME="serviceName";

    String serviceName;
    MSInterfaceService mInterfaceService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        init(intent);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mInterfaceService==null){
            init(intent);
        }
        return mInterfaceService.onStartCommand(intent, flags, startId);
    }



    private void init(Intent intent) {
        serviceName=intent.getStringExtra(KEY_SERVICE_NAME);
        try {
            Class<?> serviceClass = PluginLoadManager.getInstance().getDexClassLoader().loadClass(serviceName);
            Constructor<?> constructor = serviceClass.getConstructor();
            Object serviceObject = constructor.newInstance();
            if (serviceObject instanceof MSInterfaceService){
                mInterfaceService= (MSInterfaceService) serviceObject;
                mInterfaceService.attach(this);
                mInterfaceService.onCreate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        if (mInterfaceService!=null){
            mInterfaceService.onDestroy();
        }
        super.onDestroy();
    }


    @Override
    public void onLowMemory() {
        if (mInterfaceService!=null){
            mInterfaceService.onLowMemory();
        }
        super.onLowMemory();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        mInterfaceService.onUnbind(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        mInterfaceService.onRebind(intent);
        super.onRebind(intent);
    }
}
