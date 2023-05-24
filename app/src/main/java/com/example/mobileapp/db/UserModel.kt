package com.example.mobileapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user")
data class UserModel(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "access") val access: Int,
    @ColumnInfo(name = "refresh") val refresh: Int
) : Serializable