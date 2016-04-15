package com.example.peng.dynamicloader1host;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    View btn_proxy_load;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_proxy_load = findViewById(R.id.btn_proxy_load);
        btn_proxy_load.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_proxy_load:
                Intent intent  = new Intent(this,ProxyActivity.class);
                intent.putExtra(ProxyActivity.EXTRA_DEX_PATH,"/mnt/sdcard/DynamicLoadHost/plugin.apk");
                startActivity(intent);
                break;
        }
    }
}
