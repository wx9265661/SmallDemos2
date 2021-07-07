package com.zhanghaochen.smalldemos.demos.naviga

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.zhanghaochen.smalldemos.R
import com.zhanghaochen.smalldemos.demos.livedataandvm.PeopleModel
import com.zhanghaochen.smalldemos.demos.model.db.User
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

    val viewModel: PeopleModel by lazy {
        // 这里可以用activity也可以用fragment，这决定了这个viewModel的生命周期
        ViewModelProviders.of(activity!!).get(PeopleModel::class.java)
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

        rg_tv.setOnClickListener {
            viewModel.register("admin", "admin")
        }
        delete_admin.setOnClickListener {
            viewModel.delete(User("NORMAL", "admin", "admin", 0))
        }
    }
}