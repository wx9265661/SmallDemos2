package com.zhanghaochen.smalldemos.demos.model.db

import androidx.room.*
import java.util.*

/**
 * @author created by zhanghaochen
 * @date 2021-06-29 5:26 PM
 * 描述：最爱的玩具
 * @property ForeignKey A表中的一个字段，是B表的主键，那他就可以是A表的外键。
 * @property Entity 声明这是一个表（实体），主要参数：tableName-表名、foreignKeys-外键、indices-索引
 * @property ColumnInfo 主要用来修改在数据库中的字段名。
 * @property Ignore 声明某个字段只是临时用，不存储在数据库中。
 * @property Embedded 用于嵌套，里面的字段同样会存储在数据库中
 * @property PrimaryKey 声明该字段主键并可以声明是否自动创建。
 */
@Entity(tableName = "fav_toy", foreignKeys = [ForeignKey(entity = Toy::class, parentColumns = ["id"], childColumns = ["toy_id"]),
    ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["user_id"])], indices = [Index("toy_id")])
data class FavouriteToy(
        @ColumnInfo(name = "toy_id")
        val toyId: Long,
        @ColumnInfo(name = "user_id")
        val userId: Long,
        @ColumnInfo(name = "fav_date")
        val date: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}