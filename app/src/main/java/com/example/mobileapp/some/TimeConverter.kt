package com.example.mobileapp.some

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun TimeConverter():Int{
    val sdf = SimpleDateFormat("hh:mm:ss")
    val currentDate = sdf.format(Date())

    val cur_hour = if (currentDate[0] == '0') currentDate[1].toString() else (currentDate.substring(0, 2))
    val cur_minute = if (currentDate[3] == '0') currentDate[4].toString()  else (currentDate.substring(3, 5))
    val cur_second = if (currentDate[6] == '0') currentDate[7].toString()  else (currentDate.substring(6, 8))

    var result = 0
    result += 60*60*cur_hour.toInt() + 60*cur_minute.toInt() + cur_second.toInt()

    return result
}