package com.example.peng.dynamicloader1host;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class ProxyActivity extends ActionBarActivity {

    public static final String FROM = "extra.from";
    public static final int FROM_EXTERNAL = 0;
    public static final int FROM_INTERNAL = 1;


    /**
     * 外包类的路径
     */
    public static final String EXTRA_DEX_PATH = "extra.dex.path";
    /**
     *外包类的名字
     */
    public static final String EXTRA_CLASS = "extra.class";
    //类全名
    private String mClass;
    //dex包的路径
    private String mDexPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        mDexPath = getIntent().getStringExtra(EXTRA_DEX_PATH);
        mClass = getIntent().getStringExtra(EXTRA_CLASS);

//        lunchTargetActivity();
    }

    /**
     * 这里是找到activity的主启动项
     * 然后如果没有指定class，就调起主activity
     * ArchiveInfo档案文件
     */
    private void lunchTargetActivity( ){
        //得到包管理者
        PackageInfo packageArchiveInfo = getPackageManager().getPackageArchiveInfo(mDexPath, PackageManager.GET_ACTIVITIES);
        if(packageArchiveInfo.activities!=null&&packageArchiveInfo.activities.length>0){
            String name = packageArchiveInfo.activities[0].name;
            mClass = name;
            lunchTargetActivity(mClass);
        }
    }

    private void lunchTargetActivity(String className){
        /**
         * 1.得到解压到dex的文件路径
         * 2.加载dex通过类加载器
         * 3.反射创建对象启动
         */
        File dexOutputDir = getDir("dex", MODE_PRIVATE);
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        //新建一个类加载器  源文件  输出文件家  现在只支持app私有  依赖包  类加载器
        DexClassLoader dexClassLoader = new DexClassLoader(mDexPath, dexOutputDir.getAbsolutePath(), null, systemClassLoader);

        //用类加载器进行类的加载

        try {
            Class<?> loadClass = dexClassLoader.loadClass(className);
            //为什么知道是有这个构造方法？    在activity源码中知道他只有一个无惨构造
            Constructor constructor = loadClass.getConstructor(new Class[]{});
            Object activity = constructor.newInstance(new Object[]{});

            /**
             * setProxy方法的作用是将activity内部的执行全部交由宿主程序中的proxy（也是一个activity），onCreate方法是activity的入口，
             * setProxy以后就调用onCreate方法，这个时候activity就被调起来了。?
             */
            Method setProxy = loadClass.getMethod("setProxy", new Class[]{Activity.class});
            setProxy.setAccessible(true);
            Object invoke = setProxy.invoke(activity, new Object[]{this});

            Method onCreat = loadClass.getMethod("onCreat", new Class[]{Bundle.class});
            onCreat.setAccessible(true);
            Bundle builde  = new Bundle();
            builde.putInt(FROM,FROM_EXTERNAL);
            onCreat.invoke(activity,builde);
            /**
             * 这里实验activities的构造方法
             */
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
