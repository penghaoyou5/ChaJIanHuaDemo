package dl2host.dl2client;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.reflect.Method;

public class BaseActivity extends AppCompatActivity {


    public static final String FROM = "extra.from";
    //external 外部
    public static final int  FROM_EXTRA = 0;

    public static boolean formWai = false;


    Context context;
    Activity that,mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //如果是外部类加载
        if(savedInstanceState!=null&&savedInstanceState.getInt(FROM)==FROM_EXTRA){
        }else {
            context = that = mActivity = this;
            //如果是直接进入没有代理
            super.onCreate(savedInstanceState);
        }
        showToast("onCreate");
    }

    //这是跟外边配合 方便传入对象进行调用
    public void setProxy(Activity activity){
        context = activity;
        that =mActivity= activity;
        formWai = true;
        showToast("setProxyActivity");
    }

    //重写主要方法


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if(formWai){
            that.setContentView(layoutResID);
        }else{
            super.setContentView(layoutResID);
        }
        showToast("setContentView");
    }

    @Override
    public void setContentView(View view) {
        if(formWai){
            that.setContentView(view);
        }else {
            super.setContentView(view);
        }
        showToast("setContentView");
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if(formWai){
            that.setContentView(view, params);
        }else {
            super.setContentView(view, params);
        }
        showToast("setContentView");
    }

    public void showToast(String text){
        Toast.makeText(that,"client"+text,Toast.LENGTH_LONG).show();
    }


    //生命周期  这里的生命周期控制的还是不够好   应该返回boolean等控制父类是否走生命周期


    @Override
    protected void onStart() {
        if(!formWai){
            super.onStart();

        }
        showToast("onStart");
    }

    @Override
    protected void onResume() {
        if(!formWai)
        super.onResume();
        showToast("onResume");
    }

    @Override
    protected void onPause() {
        if(!formWai)
        super.onPause();
        showToast("onPause");
    }

    @Override
    protected void onStop() {
        if(!formWai)
        super.onStop();
        showToast("onStop");
    }

    @Override
    protected void onDestroy() {
        if(!formWai)
        super.onDestroy();
        showToast("onDestroy");
    }
}
