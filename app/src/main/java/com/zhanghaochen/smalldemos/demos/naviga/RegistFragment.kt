package com.zhanghaochen.smalldemos.demos.naviga

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhanghaochen.smalldemos.R
import com.zhanghaochen.smalldemos.framework.BaseFragment

/**
 * @author created by zhanghaochen
 * @date 2021-06-17 3:48 PM
 * 描述：
 */
class RegistFragment : BaseFragment() {
    override fun handleMessage(message: Message?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(R.layout.fragment_regest, container, false)
        return mainView
    }
}