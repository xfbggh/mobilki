package com.example.mobileapp.some

import androidx.compose.runtime.Composable
import com.example.mobileapp.R
import com.example.mobileapp.db.MainDb
import com.example.mobileapp.pageFunctions.LoginPage
import com.example.mobileapp.pageFunctions.RegisterPage

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(var icon: Int, var title: String, var screen: ComposableFun) {
    class Login(db: MainDb) : TabItem(R.drawable.login, "Авторизация", { LoginPage(db) })
    class Register(db: MainDb) : TabItem(R.drawable.machine, "Регистрация", { RegisterPage(db) })
}