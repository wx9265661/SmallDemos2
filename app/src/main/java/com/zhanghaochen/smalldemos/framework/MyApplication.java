package com.zhanghaochen.smalldemos.framework;

import android.app.Application;

import com.zhanghaochen.smalldemos.utils.GlobalParams;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalParams.mApplication = this;
    }
}
