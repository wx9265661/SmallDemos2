package com.zhanghaochen.smalldemos.demos.naviga

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.zhanghaochen.smalldemos.R
import com.zhanghaochen.smalldemos.framework.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * @author created by zhanghaochen
 * @date 2021-06-17 3:47 PM
 * 描述：
 */
class LoginFragment : BaseFragment() {
    override fun handleMessage(message: Message?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(R.layout.fragment_login, container, false)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regist.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegistFragment()
            findNavController().navigate(action)
        }
    }
}