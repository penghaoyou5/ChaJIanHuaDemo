package dl2host.dl2host;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProxyActivity extends AppCompatActivity {

    //这里定义常量为了通用的加载外部资源  决定一个类  通过包的路径跟类名  这里主要是Activity
    public static final String  EXTRA_DEX_PATH = "dl2host_dex_path";
    public static final String  EXTRA_DEX_CLASS = "dl2host_dex_class";

    //这是包的路径
    public String mDexPath = "";

    AssetManager mAssetManager;
    Resources mResources;

    Resources.Theme mTheme;

    Map<String,Method> methodMap = new HashMap<>();

    Activity mRemoteActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        //进行资源加载问题解决

        //进行activity生命周期问题解决
    }

    /**
     * 进行资源类AssetManager的初始化
     * 为什么这个方法写在里不在 client中             ====终于明白了如果是代理类的话最终调用的是这个activity的方法
     */
    protected void loadResources(){
        try {
            Class<AssetManager> assetManagerClass = AssetManager.class;
            AssetManager assetManager = assetManagerClass.newInstance();
            Method addAssetPath = assetManagerClass.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager,mDexPath);
            mAssetManager = assetManager;
        }catch (Exception e){

        }
        //这里之所以用super是因为重写了
        Resources resources = super.getResources();
        //??后面这两个参数是什么意思？？
//        mResources = new Resources(mAssetManager,resources.getDisplayMetrics(),resources.getConfiguration());
        mResources = new Resources(mAssetManager,resources.getDisplayMetrics(),resources.getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(getTheme());
    }

    @Override
    public AssetManager getAssets() {
        //这里之所以用super是因为已经重写了它的方法
        return mAssetManager==null?super.getAssets():mAssetManager;
    }

    @Override
    public Resources getResources() {
        //这里之所以用super是因为已经重写了它的方法,调用自己的就没有意义了
        return mResources==null?super.getResources():mResources;
    }

    //这里是反射activity生命周期
    protected  void  instantiateLifecircleMethods(Class<?> localClass){
        //反射常用生命周期并保存到一个数组中，只反射一次然后进行运用调用
        //这里都是没有参数的方法
        String[] methodNames = new String[]{
          "onStart" ,
                "onResume" ,
                "onPause" ,
                "onStop" ,
                "onDestroy"
        };

        for (String methodName:methodNames){
            try {
                //与代码不太相同
                Method method = localClass.getMethod(methodName);
                method.setAccessible(true);
                methodMap.put(methodName,method);
            }catch (Exception e){

            }
        }

        //有参数onCreate
        try {

            Method onCreate = localClass.getMethod("onCreate", Bundle.class);
            onCreate.setAccessible(true);
            methodMap.put("onCreate",onCreate);
        }catch (Exception e){

        }

        try {
//            Method onActivityResult = localClass.getMethod("onActivityResult", Integer.class, Integer.class, Intent.class);
            Method onActivityResult = localClass.getMethod("onActivityResult", int.class, int.class, Intent.class);
            onActivityResult.setAccessible(true);
            methodMap.put("onActivityResult",onActivityResult);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    //==方法重写        与原代码不同  因为我跟本不考虑这种异常了


    @Override
    protected void onStart() {
        super.onStart();
//        Method onStart = methodMap.get("onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
