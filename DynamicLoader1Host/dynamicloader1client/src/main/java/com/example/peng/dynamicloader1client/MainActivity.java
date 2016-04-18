package com.example.peng.dynamicloader1client;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

    }

    //这里是重写抽象方法，让父类控制加载view  好好理解控制反转
    @Override
    public View initView(final Context context) {
        LinearLayout linearLayout = new LinearLayout(mProxyActivity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        linearLayout.setBackgroundColor(Color.parseColor("#00ffff"));
        Button button = new Button(context);

        button.setText("代理主页");
        linearLayout.addView(button, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "you clicked button",
                        Toast.LENGTH_SHORT).show();
//                startActivityByProxy(TestActivity.class.getName());
            }
        });
        return linearLayout;
    }


}
