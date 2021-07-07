package com.zhanghaochen.smalldemos.demos.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zhanghaochen.smalldemos.demos.model.db.User

/**
 * @author created by zhanghaochen
 * @date 2021-07-01 4:23 PM
 * 描述：
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Query("select * from user where id=:id")
    fun findUserById(id: Long): LiveData<User>

    @Query("select * from user where user_name=:name")
    fun findUserByName(name: String): List<User?>

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("select * from user where user_name= :account and user_pwd = :pwd")
    fun login(account: String, pwd: String): LiveData<User?>

    @Query("select * from user")
    fun getAllUsers(): List<User>
}