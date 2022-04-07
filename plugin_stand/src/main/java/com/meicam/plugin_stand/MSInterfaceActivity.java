package com.meicam.plugin_stand;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/3/4 13:59
 * @Description : 插件Activity的标准
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public interface MSInterfaceActivity {

    public void attach(Activity proxyActivity);
    /**
     * 生命周期
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState);
    public void onStart();
    public void onResume();
    public void onPause();
    public void onStop();
    public void onDestroy();
    public void onSaveInstanceState(Bundle outState);
    public boolean onTouchEvent(MotionEvent event);
    public void onBackPressed();

}
