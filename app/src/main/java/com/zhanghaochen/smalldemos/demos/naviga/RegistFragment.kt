package com.zhanghaochen.smalldemos.demos.naviga

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.zhanghaochen.smalldemos.R
import com.zhanghaochen.smalldemos.framework.BaseFragment
import kotlinx.android.synthetic.main.fragment_regest.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 接收参数
        val safeArgs: RegistFragmentArgs by navArgs()

        rg_tv.text = "注册的人叫${safeArgs.name},年龄${safeArgs.age}"
    }
}