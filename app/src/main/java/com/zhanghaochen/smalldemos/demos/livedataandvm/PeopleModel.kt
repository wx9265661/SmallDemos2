package com.zhanghaochen.smalldemos.demos.livedataandvm

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.zhanghaochen.smalldemos.demos.dao.UserDao_Impl
import com.zhanghaochen.smalldemos.demos.database.AppDataBase
import com.zhanghaochen.smalldemos.demos.database.AppDataBase_Impl
import com.zhanghaochen.smalldemos.demos.model.PeopleDataModel
import com.zhanghaochen.smalldemos.demos.model.db.User
import com.zhanghaochen.smalldemos.demos.repository.UserRepository
import com.zhanghaochen.smalldemos.framework.MyApplication
import com.zhanghaochen.smalldemos.kotlinstudy.beans.People
import com.zhanghaochen.smalldemos.utils.GlobalParams
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

    fun login(account: String, pwd: String): LiveData<User?> {
        return UserRepository.getInstance(AppDataBase.getInstance(GlobalParams.mApplication).userDao()).login(account, pwd)
    }

    fun register(account: String, pwd: String) {
        GlobalScope.launch {
            UserRepository.getInstance(AppDataBase.getInstance(GlobalParams.mApplication).userDao()).register(account, pwd)
        }
    }

    fun delete(user: User) {
        GlobalScope.launch {
            UserRepository.getInstance(AppDataBase.getInstance(GlobalParams.mApplication).userDao()).delete(user)
        }

    }
}