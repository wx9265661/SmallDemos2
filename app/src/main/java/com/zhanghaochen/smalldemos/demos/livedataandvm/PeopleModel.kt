package com.zhanghaochen.smalldemos.demos.livedataandvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.zhanghaochen.smalldemos.demos.model.PeopleDataModel
import com.zhanghaochen.smalldemos.kotlinstudy.beans.People

/**
 * @author created by zhanghaochen
 * @date 2021-06-25 10:15 AM
 * 描述：人员viewmodel
 */
class PeopleModel : ViewModel() {
    companion object {
        const val ADMIN = "admin"
        const val NORMAL = "normal"
    }

    // 角色的观察对象，普通员工或者管理员
    val role = MutableLiveData<String>().apply {
        value = ADMIN
    }

    // 人员集合的观察类
    val peoples: LiveData<List<People>> = role.switchMap {
        if (it == ADMIN) {
            PeopleDataModel.getPeoples(ADMIN)
        } else {
            PeopleDataModel.getPeoples(NORMAL)
        }
    }
}