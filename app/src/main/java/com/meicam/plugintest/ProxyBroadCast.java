package com.meicam.plugintest;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.meicam.plugin_stand.MSInterfaceBroadcast;

import java.lang.reflect.Constructor;

/**
 * 广播的插桩代理类
 */
public class ProxyBroadCast extends BroadcastReceiver {

    private String mClassName;
    private MSInterfaceBroadcast msInterfaceBroadcast;

    public ProxyBroadCast(String className,Context context) {
        this.mClassName = className;
        try {
            Class<?> aClass = PluginLoadManager.getInstance().getDexClassLoader().loadClass(mClassName);  //记载插件广播类文件
            Constructor<?> constructor = aClass.getConstructor(new Class[]{});  //得到构造函数
            Object o = constructor.newInstance();                               //实例化函数
            if (o instanceof MSInterfaceBroadcast){
                msInterfaceBroadcast= (MSInterfaceBroadcast) o;
                msInterfaceBroadcast.attach(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        msInterfaceBroadcast.onReceive(context,intent); //转发到插件广播接收者
    }
}
