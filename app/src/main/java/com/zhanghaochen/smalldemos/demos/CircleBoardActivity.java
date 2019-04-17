package com.zhanghaochen.smalldemos.demos;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.customer.views.CircleBoardView;
import com.zhanghaochen.smalldemos.customer.views.CountDownTextView;
import com.zhanghaochen.smalldemos.framework.BaseActivity;
import com.zhanghaochen.smalldemos.utils.CustomerViewUtils;
import com.zhanghaochen.smalldemos.utils.SysUtils;

public class CircleBoardActivity extends BaseActivity {
    private EditText mCountDownEt;
    private CountDownTextView mCountDownTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circleboard);

        initViews();
    }

    @Override
    protected void handleMessage(Message message) {

    }

    private void initViews() {
        final EditText et1 = findViewById(R.id.mid_value);
        final EditText et2 = findViewById(R.id.cur_value);

        final CircleBoardView circleBoardView = findViewById(R.id.circle_view);
        mCountDownEt = findViewById(R.id.countdown_ms);
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
        findViewById(R.id.countdown_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long countDowmNumber = SysUtils.parseLong(SysUtils.getSafeString(mCountDownEt.getText().toString()));
                if (countDowmNumber > 0) {
                    mCountDownTextView.startCountDown(countDowmNumber);
                }
            }
        });

        mCountDownTextView = findViewById(R.id.countdown_tv);
        final String label = "还剩下 ";
        mCountDownTextView.setNormalText("倒计时控件")
                .setBeforeIndex(label.length())
                .setCountDownClickable(false)
                .setIsShowComplete(true)
                .setShowFormatTime(true)
                .setOnCountDownTickListener(new CountDownTextView.OnCountDownTickListener() {
                    @Override
                    public void onTick(long untilFinished, String showTime, CountDownTextView tv) {
                        tv.setText(CustomerViewUtils.getMixedText(label + showTime, tv.getTimeIndexes(), true));
                    }
                })
                .setOnCountDownFinishListener(new CountDownTextView.OnCountDownFinishListener() {
                    @Override
                    public void onFinish() {
                        mCountDownTextView.setText("倒计时结束");
                    }
                });
    }
}
