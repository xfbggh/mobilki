package com.example.mobileapp.some

import com.example.mobileapp.db.UserModel

fun RegCheck(users: ArrayList<UserModel>, login: String, password: String):Boolean{
    for (user in users){
        if (user.phone == login || user.password == password){
            return true
        }
    }
    return false
}