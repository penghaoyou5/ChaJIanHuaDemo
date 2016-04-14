package dynamicloader1host.dynamicloader1host;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    View btn_start_proxy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start_proxy = findViewById(R.id.btn_start_proxy);
        btn_start_proxy.setOnClickListener(this);
    }

    //点击button以后，proxy会被调起，然后加载apk并调起的任务就交给它了
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_proxy:
                Intent intent = new Intent(this, ProxyActivity.class);
                intent.putExtra(ProxyActivity.EXTRA_DEX_PATH, "/mnt/sdcard/DynamicLoadHost/plugin.apk");
                startActivity(intent);
                break;
        }
    }
}
