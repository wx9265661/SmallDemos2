package com.zhanghaochen.smalldemos.demos.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zhanghaochen.smalldemos.demos.dao.FavouriteToyDao
import com.zhanghaochen.smalldemos.demos.dao.ToyDao
import com.zhanghaochen.smalldemos.demos.dao.UserDao
import com.zhanghaochen.smalldemos.demos.model.db.FavouriteToy
import com.zhanghaochen.smalldemos.demos.model.db.Toy
import com.zhanghaochen.smalldemos.demos.model.db.User

/**
 * @author created by zhanghaochen
 * @date 2021-07-01 3:36 PM
 * 描述：数据库文件
 */
@Database(entities = [User::class, Toy::class, FavouriteToy::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    // 获取dao
    abstract fun toyDao(): ToyDao

    abstract fun userDao(): UserDao

    abstract fun favToyDao(): FavouriteToyDao

    companion object {
        // 单例
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return instance ?: synchronized(this) {
                instance ?: buildDataBase(context).also {
                    instance = it
                }
            }
        }

        private fun buildDataBase(context: Context): AppDataBase {
            return Room.databaseBuilder(context, AppDataBase::class.java, "jetPackDemo-database")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // 读取玩具的集合
                        }
                    }).build()
        }
    }


}