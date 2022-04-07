package com.meicam.plugin_stand;


import android.content.Context;
import android.content.Intent;

/**
 * 广播接口标准
 */
public interface MSInterfaceBroadcast {

    public void attach(Context context);

    public void onReceive(Context context, Intent intent);


}
