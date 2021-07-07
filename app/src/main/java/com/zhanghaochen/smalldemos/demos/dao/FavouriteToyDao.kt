package com.zhanghaochen.smalldemos.demos.dao

import androidx.room.*
import com.zhanghaochen.smalldemos.demos.model.db.FavouriteToy

/**
 * @author created by zhanghaochen
 * @date 2021-07-01 4:24 PM
 * 描述：
 */
@Dao
interface FavouriteToyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavToy(favouriteToy: FavouriteToy)

    @Update
    fun updateFavToy(favouriteToy: FavouriteToy)

    @Delete
    fun deleteFavToy(favouriteToy: FavouriteToy)

    @Query("select * from fav_toy where user_id=:userId")
    fun findFavToyByUserId(userId: Long): List<FavouriteToy>
}