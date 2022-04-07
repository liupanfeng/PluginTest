package com.meicam.plugindemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    static final String ACTION = "com.meishe.PLUGIN_ACTION";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View viewById = findViewById(R.id.image);

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(new PluginReceiver(),intentFilter);

        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(that,"发送广播",Toast.LENGTH_SHORT).show();
                startService(new Intent(that,PluginService.class));
              sendBroadcast(new Intent(ACTION));
            }
        });
    }
}