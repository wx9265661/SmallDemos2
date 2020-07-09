package com.zhanghaochen.smalldemos.demos;

import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.framework.BaseActivity;

/**
 * @author created by zhanghaochen
 * @date 2019-12-11 3:44 PM
 * 描述：
 */
public class MyViewsDisplayActivity extends BaseActivity {
    @Override
    protected void handleMessage(Message message) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views_display);
    }
}
