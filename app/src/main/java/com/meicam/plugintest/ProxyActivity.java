package com.meicam.plugintest;

import static com.meicam.plugintest.ProxyService.KEY_SERVICE_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.meicam.plugin_stand.MSInterfaceActivity;

import java.lang.reflect.Constructor;

/**
 * 代理Activity，这个Activity只是一个壳，插装式插件化实现
 */
public class ProxyActivity extends AppCompatActivity {

    //需要加载的类名
    private String className;
    private MSInterfaceActivity msInterfaceActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        className = getIntent().getStringExtra("className");
        try {
            //得到Activity的Class
            Class<?> activityClass = getClassLoader().loadClass(className);
            //得到构造方法
            Constructor<?> constructor = activityClass.getConstructor(new Class[]{});
            //得到这个类的对象
            Object object = constructor.newInstance();

            if (object instanceof MSInterfaceActivity){
                msInterfaceActivity= (MSInterfaceActivity) object;
            }

            if (msInterfaceActivity!=null){
                msInterfaceActivity.attach(this);
                Bundle bundle=new Bundle();
                msInterfaceActivity.onCreate(bundle);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startActivity(Intent intent) {
        String className = intent.getStringExtra("className");
        Intent newIntent=new Intent(this,ProxyActivity.class);
        newIntent.putExtra("className",className);
        super.startActivity(newIntent);
    }

    @Override
    public ComponentName startService(Intent service) {
        String serviceName=service.getStringExtra(KEY_SERVICE_NAME);
        Intent newIntent=new Intent(this,ProxyService.class);
        newIntent.putExtra(KEY_SERVICE_NAME,serviceName);
        return super.startService(newIntent);
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginLoadManager.getInstance().getDexClassLoader();
    }

    @Override
    public Resources getResources() {
        return PluginLoadManager.getInstance().getResources();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (msInterfaceActivity!=null){
            msInterfaceActivity.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (msInterfaceActivity!=null){
            msInterfaceActivity.onResume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (msInterfaceActivity!=null){
            msInterfaceActivity.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (msInterfaceActivity!=null){
            msInterfaceActivity.onDestroy();
        }
    }


}