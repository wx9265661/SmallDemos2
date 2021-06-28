package com.zhanghaochen.smalldemos.demos.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zhanghaochen.smalldemos.demos.livedataandvm.PeopleModel
import com.zhanghaochen.smalldemos.kotlinstudy.beans.People

/**
 * @author created by zhanghaochen
 * @date 2021-06-25 4:00 PM
 * 描述：获取人员列表，都是假数据，测试用
 */
object PeopleDataModel {

    fun getPeoples(role: String): LiveData<List<People>> {
        val peopleList = mutableListOf<People>()
        return when (role) {
            PeopleModel.ADMIN -> {
                for (i in 0..20) {
                    peopleList.add(People("admin$i", i, "$i"))
                }
                MutableLiveData(peopleList)
            }
            else -> {
                for (i in 0..10) {
                    peopleList.add(People("zhang$i", i, "$i"))
                }
                MutableLiveData(peopleList)
            }
        }
    }
}