package com.example.peng.dynamicloader1host;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    View btn_proxy_load;
    View btn_proxy_load2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_proxy_load = findViewById(R.id.btn_proxy_load);
        btn_proxy_load2 = findViewById(R.id.btn_proxy_load2);
        btn_proxy_load.setOnClickListener(this);
        btn_proxy_load2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_proxy_load:
                Intent intent  = new Intent(this,ProxyActivity.class);
                //我放到了本地sd卡中
                intent.putExtra(ProxyActivity.EXTRA_DEX_PATH,"/mnt/sdcard/DynamicLoadHost/dynamicloader1client-debug.apk");
                startActivity(intent);
                break;
            case R.id.btn_proxy_load2:
                Intent intent2  = new Intent(this,ProxyActivity.class);
                //我放到了本地sd卡中
                intent2.putExtra(ProxyActivity.EXTRA_DEX_PATH,"/mnt/sdcard/dl2host/dynamicloader1client-debug.apk");
                startActivity(intent2);
                break;
        }
    }
}
