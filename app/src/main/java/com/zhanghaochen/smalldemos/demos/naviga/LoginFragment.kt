package com.zhanghaochen.smalldemos.demos.naviga

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.zhanghaochen.smalldemos.R
import com.zhanghaochen.smalldemos.databinding.FragmentLoginBinding
import com.zhanghaochen.smalldemos.demos.NavigationDemoActivity
import com.zhanghaochen.smalldemos.demos.model.LoginModel
import com.zhanghaochen.smalldemos.framework.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.sdk27.coroutines.textChangedListener

/**
 * @author created by zhanghaochen
 * @date 2021-06-17 3:47 PM
 * 描述：
 */
class LoginFragment : BaseFragment() {
    lateinit var loginModel: LoginModel
    override fun handleMessage(message: Message?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val mainView = inflater.inflate(R.layout.fragment_login, container, false)
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater, R.layout.fragment_login, container, false)
        loginModel = LoginModel("aaa", "", context!!)
        binding.model = loginModel
        binding.activity = activity as NavigationDemoActivity?

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 一些绑定操作
        ed1.setText(loginModel.n.get())
        ed1.textChangedListener {
            onTextChanged { charSequence, i, i2, i3 ->
                charSequence?.let { loginModel.onNameChanged(it) }
            }
        }

        regist.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegistFragment()
                    .setName("wer")
                    .setAge(30)
            findNavController().navigate(action)
        }
    }
}