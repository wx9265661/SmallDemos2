package com.zhanghaochen.smalldemos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zhanghaochen.smalldemos.demos.ConstraintLayoutDemoActivity;
import com.zhanghaochen.smalldemos.demos.FxActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        // test01 函数图像
        findViewById(R.id.demo01).setOnClickListener(this);
        findViewById(R.id.demo02).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.demo01:
                startActivity(new Intent(MainActivity.this, FxActivity.class));
                break;
            case R.id.demo02:
                startActivity(new Intent(MainActivity.this, ConstraintLayoutDemoActivity.class));
                break;
            default:
                break;
        }
    }
}
