package com.zhanghaochen.smalldemos.demos.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhanghaochen.smalldemos.R
import com.zhanghaochen.smalldemos.kotlinstudy.beans.People
import kotlinx.android.synthetic.main.holder_item_people.view.*

/**
 * @author created by zhanghaochen
 * @date 2021-06-25 5:38 PM
 * 描述：mvvm测试的主页
 */
class WowMainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val datas = mutableListOf<People>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_item_people, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyHolder) {
            holder.name.text = datas[position].name
        }
    }

    fun notifyAll(peoples: List<People>) {
        if (!peoples.isNullOrEmpty()) {
            datas.clear()
            datas.addAll(peoples)
            notifyDataSetChanged()
        }
    }

    private inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.people_name)
    }
}