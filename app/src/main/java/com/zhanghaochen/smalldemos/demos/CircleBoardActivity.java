package com.zhanghaochen.smalldemos.demos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.customer.views.CircleBoardView;
import com.zhanghaochen.smalldemos.framework.BaseActivity;

public class CircleBoardActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circleboard);

        initViews();
    }

    private void initViews() {
        final EditText et1 = findViewById(R.id.mid_value);
        final EditText et2 = findViewById(R.id.cur_value);

        final CircleBoardView circleBoardView = findViewById(R.id.circle_view);
        Button button = findViewById(R.id.circle_go);

        // 设置监听
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取数值
                String valueMid = et1.getText().toString();
                String valueCur = et2.getText().toString();

                if (!TextUtils.isEmpty(valueMid) && !TextUtils.isEmpty(valueCur)) {
                    circleBoardView.setMiddleValue(valueMid);
                    circleBoardView.setCurrentValue(valueCur);
                }
            }
        });
    }
}
