package com.zhanghaochen.smalldemos.demos

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhanghaochen.smalldemos.R
import com.zhanghaochen.smalldemos.demos.livedataandvm.PeopleModel
import com.zhanghaochen.smalldemos.demos.view.adapter.WowMainAdapter
import com.zhanghaochen.smalldemos.framework.BaseFragment
import kotlinx.android.synthetic.main.fragment_wow_main.*

/**
 * @author created by zhanghaochen
 * @date 2021-06-24 3:57 PM
 * 描述：jetpack 登录成功主界面
 */
class WowMainFragment : BaseFragment() {
    val adapter: WowMainAdapter by lazy {
        WowMainAdapter()
    }

    val viewModel: PeopleModel by lazy {
        ViewModelProviders.of(activity!!).get(PeopleModel::class.java)
    }

    override fun handleMessage(message: Message?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(R.layout.fragment_wow_main, container, false)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wow_recycler.layoutManager = object : LinearLayoutManager(context) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (ignored: Exception) {
                }
            }
        }

        // 按钮切换
        admin_btn.setOnClickListener {
            viewModel.role.postValue(PeopleModel.ADMIN)
        }
        normal_btn.setOnClickListener {
            viewModel.role.value = PeopleModel.NORMAL
        }

        wow_recycler.adapter = adapter
        viewModel.peoples.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.notifyAll(it)
            }
        })
    }
}