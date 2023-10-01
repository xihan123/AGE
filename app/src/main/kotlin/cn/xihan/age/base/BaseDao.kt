package cn.xihan.age.base

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/27 7:46
 * @介绍 :
 */
@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: T)

    @Upsert
    suspend fun upsert(vararg entity: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg entity: T)

    @Delete
    suspend fun delete(vararg entity: T)
}