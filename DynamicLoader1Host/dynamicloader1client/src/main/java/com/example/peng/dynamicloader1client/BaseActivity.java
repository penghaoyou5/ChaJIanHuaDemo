package com.example.peng.dynamicloader1client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 其实他只有个空壳子
 * 现在它就是给activity提供view而已
 * <p>
 * <p>
 * <p>
 * <p>
 * 这是客户端activity
 * 这里现在只是一个空壳子了
 * <p>
 * 其实应该让 让主机 像android系统一样支持四大组件的  现在这样终究是被淘汰
 */
public abstract class BaseActivity extends AppCompatActivity {


    private static final String TAG = "Client-BaseActivity";

    public static final String FROM = "extra.from";
    public static final int FROM_EXTERNAL = 0;
    public static final int FROM_INTERNAL = 1;
    public static final String EXTRA_DEX_PATH = "extra.dex.path";
    public static final String EXTRA_CLASS = "extra.class";

    public static final String PROXY_VIEW_ACTION = "com.ryg.dynamicloadhost.VIEW";
    public static final String DEX_PATH = "/mnt/sdcard/DynamicLoadHost/dynamicloader1client-debug.apk";

    protected Activity mProxyActivity;
    protected int mFrom = FROM_INTERNAL;

    /**
     * @param activity 宿主
     */
    public void setProxy(Activity activity) {
        mProxyActivity = activity;
        Toast.makeText(activity,"进入代理类"+this.getClass().getName(),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //如果是从proxy启动的基本上什么都不做
        if (savedInstanceState != null) {
            //如果是正常开启的则 mForm 为内部 1否则为 0        从这句代码来判断是否是从内部进入
            mFrom = savedInstanceState.getInt(FROM, FROM_INTERNAL);
        }
        if (mFrom == FROM_INTERNAL) {
            super.onCreate(savedInstanceState);
            mProxyActivity = this;
        }
//        setContentView(initView(mProxyActivity));
        //终于看出了是这里的问题：：：：：   就是这里的没有设置好。。。
        mProxyActivity.setContentView(initView(mProxyActivity));
    }

    public abstract View initView(Context mProxyActivity);

    protected void startActivityByProxy(String className) {
        //这是从本地进
        if (mProxyActivity == this) {
            Intent intent = new Intent();
            intent.setClassName(this, className);
            this.startActivity(intent);
        } else {
            //这是从代理类进入  ?
            Intent intent = new Intent(PROXY_VIEW_ACTION);
//            intent.setClassName(mProxyActivity,className);
//            intent.putExtra(FROM,FROM_EXTERNAL);
            intent.putExtra(EXTRA_DEX_PATH, DEX_PATH);
            intent.putExtra(EXTRA_CLASS, className);
            mProxyActivity.startActivity(intent);
        }
    }


    @Override
    public void setContentView(View view) {
        if (mProxyActivity == this) {
            super.setContentView(view);
        } else {
            mProxyActivity.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (mProxyActivity == this) {
            super.setContentView(view, params);
        } else {
            mProxyActivity.setContentView(view, params);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (mProxyActivity == this) {
            super.setContentView(layoutResID);
        } else {
            mProxyActivity.setContentView(layoutResID);
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (mProxyActivity == this) {
            super.addContentView(view, params);
        } else {
            mProxyActivity.addContentView(view, params);
        }
    }
}
