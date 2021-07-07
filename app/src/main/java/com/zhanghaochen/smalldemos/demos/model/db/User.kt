package com.zhanghaochen.smalldemos.demos.model.db

import android.location.Address
import androidx.annotation.NonNull
import androidx.room.*

/**
 * @author created by zhanghaochen
 * @date 2021-06-29 4:05 PM
 * 描述：用户表
 */
@Entity(tableName = "user")
data class User(
        @ColumnInfo(name = "user_role")
        var role: String = "",
        @ColumnInfo(name = "user_name")
        var name: String = "",
        @ColumnInfo(name = "user_pwd")
        var pwd: String = "",
//        // 用于嵌套，里面的字段同样会存储在数据库中。
//        @Embedded
//        var address: Address,
        // 声明某个字段只是临时用，不存储在数据库中。
        @Ignore
        var state: Int = 0
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    var id: Long = 0
}