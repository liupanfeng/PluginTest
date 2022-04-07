package com.meicam.plugindemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.meicam.plugin_stand.MSInterfaceBroadcast;

public class PluginReceiver extends BroadcastReceiver implements MSInterfaceBroadcast {

    @Override
    public void attach(Context context) {
        Toast.makeText(context, "attach success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "插件收到广播", Toast.LENGTH_SHORT).show();
    }
}
