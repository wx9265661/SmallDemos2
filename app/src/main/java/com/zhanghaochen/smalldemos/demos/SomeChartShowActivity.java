package com.zhanghaochen.smalldemos.demos;

import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;
import android.view.View;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.beans.TrendDataBean;
import com.zhanghaochen.smalldemos.framework.BaseActivity;
import com.zhanghaochen.smalldemos.widget.trend.TrendView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author created by zhanghaochen
 * @date 2019-08-08 2:26 PM
 * 描述：
 */
public class SomeChartShowActivity extends BaseActivity implements View.OnClickListener {
    private List<TrendDataBean> mTrendDataBeans = new ArrayList<>();

    private TrendView mTrendView;

    @Override
    protected void handleMessage(Message message) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_show);

        initViews();
    }

    private void initViews() {
        findViewById(R.id.trend).setOnClickListener(this);
        mTrendView = findViewById(R.id.trend_view);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.trend:
                mTrendView.setVisibility(View.VISIBLE);
                initTrendData();
                mTrendView.setData(mTrendDataBeans);
                break;
            default:
                break;
        }
    }

    /**
     * 搞点假数据
     */
    private void initTrendData() {
        mTrendDataBeans.clear();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            TrendDataBean bean = new TrendDataBean();
            bean.newValue = random.nextInt(100);
            bean.vol = random.nextFloat() * 100;
            mTrendDataBeans.add(bean);
        }
    }
}
