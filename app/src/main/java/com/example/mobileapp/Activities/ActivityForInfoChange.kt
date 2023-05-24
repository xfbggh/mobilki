package com.example.mobileapp.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mobileapp.db.MainDb
import com.example.mobileapp.db.UserModel
import com.example.mobileapp.pageFunctions.ChangeFormPage

class ActivityForInfoChange : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = MainDb.getDb(this);
        setContent {
            val user = intent.extras?.get("user")

            ChangeFormPage(db, user as UserModel)

        }
    }

}