package com.zhanghaochen.smalldemos.demos

import android.os.Bundle
import android.os.Message
import com.zhanghaochen.smalldemos.R
import com.zhanghaochen.smalldemos.framework.BaseActivity

/**
 * @author created by zhanghaochen
 * @date 2021-06-17 3:46 PM
 * 描述：
 */
class NavigationDemoActivity : BaseActivity() {
    override fun handleMessage(message: Message?) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_navi)
    }
}