package com.zhanghaochen.smalldemos.demos.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author created by zhanghaochen
 * @date 2021-06-29 5:04 PM
 * 描述：玩具表
 */
@Entity(tableName = "toy")
data class Toy(
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "desc")
        val description: String,
        @ColumnInfo(name = "price")
        val price: String,
        @ColumnInfo(name = "url")
        val url: String,
        @ColumnInfo(name = "brand")
        val brand: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}