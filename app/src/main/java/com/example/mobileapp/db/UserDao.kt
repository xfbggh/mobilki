package com.example.mobileapp.db

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT id FROM user WHERE phone = :phone LIMIT 1")
    suspend fun GetIdByPhone(phone: String): Int?

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    suspend fun GetAllById(id: Int?): UserModel

    @Query("SELECT name FROM user WHERE id = :id LIMIT 1")
    suspend fun GetNameById(id: Int): String

    @Query("SELECT access FROM user WHERE id = :id LIMIT 1")
    suspend fun GetAccessById(id: Int): Int

    @Query("SELECT refresh FROM user WHERE id = :id LIMIT 1")
    suspend fun GetRefreshById(id: Int): Int

    @Query("SELECT * FROM user")
    suspend fun GetAllUsers(): List<UserModel>

    @Query("SELECT password FROM user WHERE id = :id LIMIT 1")
    suspend fun GetPassById(id: Int): String

    @Query("SELECT id FROM user WHERE password = :pass AND id != :id LIMIT 1")
    suspend fun WhereIsPass(pass: String, id: Int): Int?

    @Query("SELECT id FROM user WHERE phone = :login AND password = :password LIMIT 1")
    suspend fun WhereIsUser(login: String, password: String): Int?

    @Insert
    suspend fun insertAll(vararg users: UserModel)

    @Delete
    suspend fun delete(user: UserModel)

    @Update
    suspend fun update(user: UserModel)
}
