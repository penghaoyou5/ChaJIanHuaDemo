package dl2host.dl2host;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

public class ProxyActivity extends AppCompatActivity {

    //差点忘了这个东西  标记activity是从哪里进的方便同时能够进行单独运行与代理运行
    public static final String FROM = "extra.from";
    //external 外部
    public static final int  FROM_EXTRA = 0;



    //这里定义常量为了通用的加载外部资源  决定一个类  通过包的路径跟类名  这里主要是Activity
    public static final String  EXTRA_DEX_PATH = "extra.dex.path";
    public static final String  EXTRA_DEX_CLASS = "extra.class";

    //这是包的路径
    public String mDexPath = "";
    public String mDexClass = "";

    AssetManager mAssetManager;
    Resources mResources;

    Resources.Theme mTheme;

    Map<String,Method> methodMap = new HashMap<>();

    Activity mRemoteActivity;
    Class<?> mRemoteClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_proxy);

        mDexPath = getIntent().getStringExtra(EXTRA_DEX_PATH);
        mDexClass = getIntent().getStringExtra(EXTRA_DEX_CLASS);

        //进行资源加载问题解决
        loadResources();

        //进行activity生命周期问题解决   这里反射方法到集合中
        if(TextUtils.isEmpty(mDexClass)){
            launchTargetActivity();
        }else{
            launchTargetActivity(mDexClass);
        }

        //进行方法的调用
        try{
            methodMap.get("setProxy").invoke(mRemoteActivity,this);
            if(savedInstanceState==null){
                savedInstanceState = new Bundle();
            }
            savedInstanceState.putInt(FROM,FROM_EXTRA);
            methodMap.get("onCreate").invoke(mRemoteActivity,savedInstanceState);
        }catch (Exception e){
        }

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

    /**
     * 如果没有className就找到默认的
     */
    protected void launchTargetActivity(){
//        File file = new File(mDexPath);
//        boolean exit = file.exists();
//        System.out.print(exit);
        PackageInfo packageArchiveInfo = getPackageManager().getPackageArchiveInfo(mDexPath, PackageManager.GET_ACTIVITIES);
        if(packageArchiveInfo.activities!=null&&packageArchiveInfo.activities.length>0){
//            mDexClass = packageArchiveInfo.activities[0].getClass().getName();  这里犯了一个错误
            mDexClass = packageArchiveInfo.activities[0].name;
            launchTargetActivity(mDexClass);
        }
    }


    protected void launchTargetActivity(final String className){
        //初始化累加载器  需要解压目录  文件目录 DexClassLoader

        //解压目录  必须为程序私有目录
        File dex = getDir("dex", MODE_PRIVATE);
        //http://blog.csdn.net/com360/article/details/14125683 这是注释文档
//        DexClassLoader dexClassLoader = new DexClassLoader(mDexPath, dex.getAbsolutePath(), null, this.getClass().getClassLoader());
        DexClassLoader dexClassLoader = new DexClassLoader(mDexPath, dex.getAbsolutePath(), null, ClassLoader.getSystemClassLoader());
        try {
            mRemoteClass = dexClassLoader.loadClass(className);
            mRemoteActivity = (Activity) mRemoteClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        instantiateLifecircleMethods(mRemoteClass);
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

            Method setProxyActivity = localClass.getMethod("setProxy", Activity.class);
            setProxyActivity.setAccessible(true);
            methodMap.put("setProxy",setProxyActivity);
        }catch (Exception e){

        }

        //有参数onCreate
        try {

            Method onCreate = localClass.getMethod("onCreate", Bundle.class);
            onCreate.setAccessible(true);
            methodMap.put("onCreate",onCreate);
        }catch (Exception e){

        }

        //这里报了异常
//        try {
//            Method onActivityResult = localClass.getMethod("onActivityResult", Integer.class, Integer.class, Intent.class);
//            Method onActivityResult = localClass.getMethod("onActivityResult", int.class, int.class, Intent.class);  这里也出现了方法找不到异常
            // Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.reflect.Method java.lang.Class.getDeclaredMethod(java.lang.String, java.lang.Class[])' on a null object reference
//            Method onActivityResult = localClass.getDeclaredMethod("onActivityResult", int.class, int.class, Intent.class);
//            Method onActivityResult = localClass.getDeclaredMethod("onActivityResult", new Class[]{int.class, int.class, Intent.class});
//            onActivityResult.setAccessible(true);
//            methodMap.put("onActivityResult",onActivityResult);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }

    }

    //==方法重写        与原代码不同  因为我跟本不考虑这种异常了


    @Override
    protected void onStart() {
        super.onStart();
        Method onStart = methodMap.get("onStart");
        try {
            onStart.invoke(mRemoteActivity);
        }catch (Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            methodMap.get("onResume").invoke(mRemoteActivity);
        }catch (Exception e){

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            methodMap.get("onPause").invoke(mRemoteActivity);
        }catch (Exception e){

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            methodMap.get("onStop").invoke(mRemoteActivity);
        }catch (Exception e){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            methodMap.get("onDestroy").invoke(mRemoteActivity);
        }catch (Exception e){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            methodMap.get("onDestroy").invoke(mRemoteActivity,requestCode, resultCode, data);
        }catch (Exception e){

        }
    }
}
