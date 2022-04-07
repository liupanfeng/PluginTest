package com.meicam.plugintest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    static final String ACTION = "com.meishe.PLUGIN_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

        //动态注册
        registerReceiver(broadcastReceiver,new IntentFilter(ACTION));
    }


    //匿名内部类
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"这里是宿主，收到消息！",Toast.LENGTH_SHORT).show();
        }
    };


    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.i(TAG,"用户申请过权限，但是被拒绝了（不是彻底决绝）");
//               申请权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},1);

            } else {
                Log.i(TAG,"申请过权限，但是被用户彻底决绝了或是手机不允许有此权限（依然可以在此再申请权限）");
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
    }

    //加载插件
    //第一步 将文件从sd卡 /data/user/0/com.meicam.plugintest/app_plugin/plugindemo-debug.apk
     //在sd卡中显示的是：/data/data/com.meicam.plugintest/app_plugin/plugindemo-debug.apk

    // 拷贝到
    public void load(View view){
        loadPlugin();
    }

    private void loadPlugin() {
        File fileDir=this.getDir("plugin", Context.MODE_PRIVATE);
        String name="plugindemo.apk";
        String filePath=new File(fileDir, name).getAbsolutePath();
        Log.d(TAG,filePath);
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        InputStream is = null;
        FileOutputStream os = null;
        try {
            is = getAssets().open("plugindemo-debug.apk");
            os = new FileOutputStream(filePath);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            File f = new File(filePath);
            if (f.exists()) {
                Toast.makeText(this, "插件模拟从服务端下载成功", Toast.LENGTH_SHORT).show();
            }
            PluginLoadManager.getInstance().loadPath(this,filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //页面跳转
    public void click(View view) {

        //这个是插桩式
        Intent intent = new Intent(this,ProxyActivity.class);
        intent.putExtra("className", PluginLoadManager.getInstance().getPackageInfo().activities[0].name);
        startActivity(intent);

    }


}