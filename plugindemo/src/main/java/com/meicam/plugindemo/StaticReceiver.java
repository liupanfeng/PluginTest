package com.meicam.plugindemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StaticReceiver extends BroadcastReceiver {

    static final String ACTION = "com.meishe.PLUGIN_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "我是插件   收到宿主的消息  静态注册的广播  收到宿主的消息----->", Toast.LENGTH_SHORT).show();
        context.sendBroadcast(new Intent(ACTION));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "休眠之后---->", Toast.LENGTH_SHORT).show();
    }
}
