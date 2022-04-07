package com.meicam.plugintest;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.logging.Logger;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/3/4 21:20
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class PathUtil {
    private static final String TAG="PathUtil";

    private static String SDK_FILE_ROOT_DIRECTORY = "PluginTest" + File.separator;
    private static String PLUGIN_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "Plugin";


    public static String getFolderDirPath(String dstDirPathToCreate) {
        File dstFileDir = new File(Environment.getExternalStorageDirectory(), dstDirPathToCreate);
        if( AndroidOS.USE_SCOPED_STORAGE){
            dstFileDir = new File(App.mContext.getExternalFilesDir(""),dstDirPathToCreate);
        }
        if (!dstFileDir.exists() && !dstFileDir.mkdirs()) {
            Log.e(TAG, "Failed to create file dir path--->" + dstDirPathToCreate);
            return null;
        }
        return dstFileDir.getAbsolutePath();
    }

    public static String getPluginDirPath(){
        return getFolderDirPath(PLUGIN_DIRECTORY);
    }
}
