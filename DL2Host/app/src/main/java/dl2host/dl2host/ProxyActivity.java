package dl2host.dl2host;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ProxyActivity extends AppCompatActivity {

    //这里定义常量为了通用的加载外部资源  决定一个类  通过包的路径跟类名  这里主要是Activity
    public static final String  EXTRA_DEX_PATH = "dl2host_dex_path";
    public static final String  EXTRA_DEX_CLASS = "dl2host_dex_class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        //进行资源加载问题解决

        //进行activity生命周期问题解决
    }
}
