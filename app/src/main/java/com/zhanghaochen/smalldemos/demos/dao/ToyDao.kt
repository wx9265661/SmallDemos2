package com.zhanghaochen.smalldemos.demos.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zhanghaochen.smalldemos.demos.model.db.Toy

/**
 * @author created by zhanghaochen
 * @date 2021-06-30 10:51 AM
 * 描述：查询玩具的方法
 * @property OnConflictStrategy 新增冲突的时候处理的方法
 * @property Query 不仅可以声明这是一个查询语句，也可以用来删除和修改，不可以用来新增。
 */
@Dao
interface ToyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToy(toy: Toy)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToys(toys: List<Toy>)

    @Delete
    fun deleteToy(toy: Toy)

    @Delete
    fun deleteToys(toys: List<Toy>)

    @Update
    fun updateToy(toy: Toy)

    // todo 貌似要用array，不用list，具体使用过程再看
    @Update
    fun updateToys(toys: List<Toy>)

    // 通过id查询具体的玩具
    @Query("SELECT * FROM toy WHERE id=:id")
    fun findToyById(id: Long): Toy?

    // 批量查询，同品牌的玩具
    @Query("SELECT * FROM toy WHERE brand=:brand")
    fun findToysByBrand(brand: String): List<Toy>

    // 模糊查询，同名的玩具排序
    @Query("SELECT * FROM toy WHERE name LIKE :name ORDER BY brand ASC")
    fun findToysByName(name: String): List<Toy>

    // 配合livedata，返回所有玩具
    @Query("SELECT * FROM toy")
    fun getAllToysLD(): LiveData<List<Toy>>

    @Query("SELECT * FROM toy WHERE id=:id")
    fun getToyByIdLD(id: Long): LiveData<Toy>

    // 查询用户喜欢的玩具的集合 内联查询
    @Query("SELECT toy.id,toy.name,toy.`desc`,toy.price,toy.brand,toy.url FROM toy INNER JOIN fav_toy ON fav_toy.toy_id = toy.id WHERE fav_toy.user_id=:userId")
    fun findToysByUserId(userId: Long): LiveData<List<Toy>>
}