package com.example.mobileapp

import android.annotation.SuppressLint
import com.example.mobileapp.some.TimeConverter
import java.util.*

@SuppressLint("SimpleDateFormat")
fun TokenIsExpired(access: Int, refresh: Int):String{
    if ((TimeConverter()-access)<=15){
        return "active"
    }
    if ((TimeConverter()-access)>15 && (TimeConverter()-refresh)>30){
        return "expired"
    }
    return "refresh"
}