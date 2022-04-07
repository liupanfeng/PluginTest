package com.meicam.plugindemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.meicam.plugin_stand.MSInterfaceActivity;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/3/4 15:14
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
class BaseActivity extends AppCompatActivity implements MSInterfaceActivity {

    protected  Activity that;

    @Override
    public void attach(Activity proxyActivity) {
        that =proxyActivity;
    }


    @Override
    public void setContentView(View view) {
        if (that!=null){
            that.setContentView(view);
        }else{
            super.setContentView(view);
        }
    }



    @Override
    public void setContentView(int layoutResID) {
        that.setContentView(layoutResID);
    }


    public View findViewById(int id){
        return that.findViewById(id);
    }


    @Override
    public Intent getIntent() {
        if(that!=null){
            return that.getIntent();
        }
        return super.getIntent();
    }


    @Override
    public ClassLoader getClassLoader() {
        return that.getClassLoader();
    }


    @Override
    public void startActivity(Intent intent) {
        Intent newIntent = new Intent();
        newIntent.putExtra("className",intent.getComponent().getClassName());
        that.startActivity(newIntent);
    }

    @Override
    public ComponentName startService(Intent service) {
        Intent newIntent = new Intent();
        newIntent.putExtra("serviceName",service.getComponent().getClassName());
        return that.startService(newIntent);
    }


    @Override
    public void sendBroadcast(Intent intent) {
        that.sendBroadcast(intent);
    }


    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return that.registerReceiver(receiver, filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
