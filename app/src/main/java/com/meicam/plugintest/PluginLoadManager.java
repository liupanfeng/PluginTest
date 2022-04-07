package com.meicam.plugintest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/3/4 14:05
 * @Description : 插件管理        饿汉式单例
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class PluginLoadManager {

    private static final String TAG = PluginLoadManager.class.getSimpleName();

    private static final PluginLoadManager ourInstance = new PluginLoadManager();

    public static PluginLoadManager getInstance() {
        return ourInstance;
    }


    private PackageInfo packageInfo;
    private Resources resources;
    //dex 类加载器
    private DexClassLoader dexClassLoader;

    private PluginLoadManager() {
    }

    /**
     * 加载插件
     * 加载插件的目的就是要得到Resource对象以及dexClassLoader
     *
     * @param context    上下文
     * @param pluginPath plugin文件的绝对路径
     */
    public void loadPath(Context context, String pluginPath) {
        // File filesDir = context.getDir("plugin", Context.MODE_PRIVATE);
        Log.d(TAG, "pluginPath=" + pluginPath);

        PackageManager packageManager = context.getPackageManager();
        //得到安装包的信息
        packageInfo = packageManager.getPackageArchiveInfo(pluginPath, PackageManager.GET_ACTIVITIES);

        // activity 名字
        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);

        //第一个参数是dex的路径，第二个是优化路径    最后一个参数是父类加载器
        dexClassLoader = new DexClassLoader(pluginPath,
                dexOutFile.getAbsolutePath(), null, context.getClassLoader());

        try {
            //反射得到AssetManager
            AssetManager assetManager = AssetManager.class.newInstance();
            //方法名和参数类型  addAssetPath官方已经不建议使用这个方法了，推荐使用setApkAssets
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);

            addAssetPath.invoke(assetManager, pluginPath);    //方法在assetManager上面 需要传递一个路径当做参数

            //创建一个Resource 第一个参数就是AssetManager  但是AssetManager的实例化被隐藏了，所以只能通过反射的方式得到
            resources = new Resources(assetManager,
                    context.getResources().getDisplayMetrics(),
                    context.getResources().getConfiguration());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public Resources getResources() {
        return resources;
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }


    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    /**
     * 解析插件中的静态广播  并进行注册
     * @param context
     * @param path
     */
    private void parseReceivers(Context context, String path) {
        try {
            Class<?> packagerParseClazz = Class.forName("android.content.pm.PackageParser");    //class android.content.pm.PackageParser  得到PackageParser字节码
            Method parsePackageMethod = packagerParseClazz.getDeclaredMethod("parsePackage", File.class, int.class);    //得到parsePackage方法
            Object packagerParserObject = packagerParseClazz.newInstance();     //得到packagerParser对象
            Object packageObj = parsePackageMethod.invoke(packagerParserObject, new File(path), PackageManager.GET_ACTIVITIES);  //执行parsePackage方法

            Class<?> packageClazz = packageObj.getClass();
            Field receivers = packageClazz.getDeclaredField("receivers"); //ArrayList receivers
            List receiverList = (List) receivers.get(packageObj);

            Class<?> componentClazz = Class.forName("android.content.pm.PackageParser$Component");
            Field intentsField = componentClazz.getDeclaredField("intents");

            //调用generateActivityInfo 方法, 把PackageParser.Activity 转换成
            Class<?> packageParser$ActivityClazz = Class.forName("android.content.pm.PackageParser$Activity");
            //  generateActivityInfo方法
            Class<?> packageUserStateClazz = Class.forName("android.content.pm.PackageUserState");
            Object packageUserStateObject = packageUserStateClazz.newInstance();
            Method generateActivityInfoMethod = packagerParseClazz.getDeclaredMethod("generateActivityInfo", packageParser$ActivityClazz, int.class, packageUserStateClazz, int.class);

            Class<?> userHandleClazz = Class.forName("android.os.UserHandle");
            Method getCallingUserIdMethod = userHandleClazz.getDeclaredMethod("getCallingUserId");
            int  userId = (int) getCallingUserIdMethod.invoke(null);
            for (Object activity:receiverList) {
                ActivityInfo info = (ActivityInfo) generateActivityInfoMethod.invoke(packagerParserObject, activity, 0, packageUserStateObject, userId);
                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) dexClassLoader.loadClass(info.name).newInstance();
                List<? extends IntentFilter> intents = (List<? extends IntentFilter>) intentsField.get(activity);
                for (IntentFilter intentFilter:intents){
                    //注册广播
                    context.registerReceiver(broadcastReceiver,intentFilter);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过Hook的方式加载插件apk
     * pluginPath：插件apk的绝对路径
     */
    public void injectPluginClass(Context context, String pluginPath) {
        //步骤：1.通过插件绝对路径，实例化DexClassLoader
        //     2.反射拿到dex存放的插件数组
        //     3.反射拿到系统存放dex的插件数组
        //     4.将两个数组的内容合并，并设置到系统的插件数组里边去
        String cachePath = context.getCacheDir().getAbsolutePath();
        dexClassLoader = new DexClassLoader(pluginPath, cachePath, cachePath, context.getClassLoader());

        //找到插件的element数组  dexPathlist     核心是找到这两个成员变量  1. DexPathList pathList；   2.   Element[] dexElements；  然后进行合并处理
        //反射的思路是：得到字节码clazz  通过字节码得到Field，field可以进行实例化，也设置数据

        try {
            Class<?> baseDexClassLoader = Class.forName("dalvik.system.BaseDexClassLoader");  //得到BaseDexClassLoader 字节码
            Field pathList = baseDexClassLoader.getDeclaredField("pathList");   //得到DexPathList 成员变量pathList
            pathList.setAccessible(true);
            Object myPathListObject = pathList.get(dexClassLoader);     //这个是从插件的dex类加载器上得到  得到DexPathList对象

            Class<?> myPathClazz = myPathListObject.getClass();  //得到DexPathList字节码
            Field dexElements = myPathClazz.getDeclaredField("dexElements");   //得到Element[] 类型的成员变量dexElements
            dexElements.setAccessible(true);

            //插件的 dexElements[]
            Object myElements = dexElements.get(myPathListObject);// 得到 成员变量dexElements 对象 当前数组包含一个数据


            //  找到系统的Elements数组    dexElements
            PathClassLoader classLoader = (PathClassLoader) context.getClassLoader();   //得到系统的PathClassLoader
            Class<?> baseDexClazzLoader = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListSystem = baseDexClazzLoader.getDeclaredField("pathList");
            pathListSystem.setAccessible(true);
            Object pathListObjectSystem = pathListSystem.get(classLoader);

            Class<?> pathListClazzSystem = pathListObjectSystem.getClass();
            Field dexElementsSystem = pathListClazzSystem.getDeclaredField("dexElements");
            dexElementsSystem.setAccessible(true);
            //得到系统的element数组
            Object elementSystem = dexElementsSystem.get(pathListObjectSystem);


            //将两个dex数组进行合并，通过反射注入到系统的Field （dexElements）中去
            int lengthSystem = Array.getLength(elementSystem);  //系统dex数组的长度
            int lengthPlugin = Array.getLength(myElements);     //插件dex数组的长度

            Class<?> componentType = elementSystem.getClass().getComponentType();  //getComponentType 返回数组元素中的class对象
            int newLength = lengthSystem + lengthPlugin;
            Object newElementsArray = Array.newInstance(componentType, newLength);

            for (int i = 0; i < newLength; i++) {
                if (i < lengthPlugin) {
                    Array.set(newElementsArray, i, Array.get(myElements, i));
                } else {
                    Array.set(newElementsArray, i, Array.get(elementSystem, i - lengthPlugin));
                }
            }

            Field dexElementsSys = pathListObjectSystem.getClass().getDeclaredField("dexElements");
            dexElementsSys.setAccessible(true);
            dexElementsSys.set(pathListObjectSystem, newElementsArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        initAssetManagerAndResource(context, pluginPath);
    }



    private void initAssetManagerAndResource(Context context, String pluginPath) {
        try {

            PackageManager packageManager = context.getPackageManager();
            //得到安装包的信息
            packageInfo = packageManager.getPackageArchiveInfo(pluginPath, PackageManager.GET_ACTIVITIES);

            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);//得到这个方法：addAssetPath  接受一个String类型的参数
            addAssetPath.setAccessible(true);
            addAssetPath.invoke(assetManager, pluginPath);


            Method ensureStringBlocks = AssetManager.class.getDeclaredMethod("ensureStringBlocks");
            ensureStringBlocks.setAccessible(true);
            ensureStringBlocks.invoke(assetManager);

            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());

            parseReceivers(context,pluginPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
