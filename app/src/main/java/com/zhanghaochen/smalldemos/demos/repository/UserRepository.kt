package com.zhanghaochen.smalldemos.demos.repository

import androidx.lifecycle.LiveData
import com.zhanghaochen.smalldemos.demos.dao.UserDao
import com.zhanghaochen.smalldemos.demos.model.db.User
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

/**
 * @author created by zhanghaochen
 * @date 2021-07-05 11:09 AM
 * 描述：用户处理仓库
 */
class UserRepository private constructor(private val userDao: UserDao) {

    /**
     * 登录用户 本地数据库查询
     */
    fun login(account: String, pwd: String): LiveData<User?> = userDao.login(account, pwd)

    /**
     * register()方法是一个普通方法，所以它需要在子线程使用，如代码所见，通过协程实现。
     * login()是配合LiveData使用的，不需要额外创建子线程，但是他的核心数据库操作还是在子线程中实现的。
     */

    /**
     * 挂起函数只能在协程中和其他挂起函数中调用，不能在其他地方使用。
     *
     */
    suspend fun register(account: String, pwd: String): Boolean {
        // withContextt这个函数主要可以切换到指定的线程，并在闭包内的逻辑执行结束之后，自动把线程切回去继续执行
        return withContext(IO) {
            // 切换到 IO 线程，并在执行完成后切回 UI 线程
            // 将会运行在 IO 线程

            // 首先看看这个账户有没有
            val user = userDao.findUser(account, pwd)
            if (user == null) {
                // 没有这个账号，插入
                userDao.insertUser(User("NORMAL", account, pwd, 0))
                return@withContext true
            }
            // 有这个账号，注册失败
            return@withContext false
        }
    }

    suspend fun delete(user: User) {
        withContext(IO) {
            val users = userDao.findUserByName(user.name)
            if (!users.isNullOrEmpty()) {
                users.forEach {
                    it?.let {
                        userDao.deleteUser(it)
                    }
                }
            }
        }
    }

    companion object {
        private var instance: UserRepository? = null
        fun getInstance(userDao: UserDao): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(userDao)
            }
        }
    }
}