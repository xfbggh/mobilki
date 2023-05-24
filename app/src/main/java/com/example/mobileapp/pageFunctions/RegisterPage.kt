package com.example.mobileapp.pageFunctions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mobileapp.Activities.Loader
import com.example.mobileapp.Activities.MainActivity
import com.example.mobileapp.Activities.ProfileActivity
import java.util.regex.Pattern
import com.example.mobileapp.R
import com.example.mobileapp.db.MainDb
import com.example.mobileapp.db.UserModel
import com.example.mobileapp.some.RegCheck
import kotlinx.coroutines.launch
import java.io.Serializable

@Composable
fun RegisterPage(db: MainDb) {
    val context = LocalContext.current
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var IsWrap by remember { mutableStateOf(true) }

    val rememberCoroutineScope = rememberCoroutineScope()

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Имя")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (error == "All") Color.Red else Color.Blue
                    )
            )
            Text(text = "Номер телефона")
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (error == "All") Color.Red else Color.Blue
                    )
            )
            Text(text = "Пароль")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (error == "All") Color.Red else Color.Blue
                    )
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = if (IsWrap) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier
                        .weight(0.80f)
                        .height(60.dp)
                )
                Button(
                    onClick = { IsWrap = !IsWrap },
                    modifier = Modifier
                        .weight(0.20f)
                        .height(60.dp)
                        .background(Color.Blue)
                ) {
                    Image(
                        painter = if (IsWrap) painterResource(id = R.drawable.closeye) else painterResource(
                            id = R.drawable.openye
                        ),
                        contentDescription = "image",
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        var InBase = false
                        val IsLoginTrue = Pattern.compile("89[0-9]{9}").matcher(phone).find()
                        val IsNameTrue = Pattern.compile("[А-Яа-я]+").matcher(name).find()
                        val IsPasswordTrue = Pattern.compile("[0-9]{4}").matcher(password).find()

                        rememberCoroutineScope.launch {
                            val users = db.getDao().GetAllUsers()
                            InBase = RegCheck(users as ArrayList<UserModel>, phone, password)
                            if (!InBase && IsLoginTrue && IsNameTrue && IsPasswordTrue){
                                val user = UserModel(0, name, phone, password, "user", 0, 0)
                                db.getDao().insertAll(user)
                                Toast.makeText(context, "Успешно!", Toast.LENGTH_SHORT)
                                    .show()

                                ChangeActivity(context)
                                error = ""
                                return@launch
                            } else {
                                Toast.makeText(context, "Не верные данные", Toast.LENGTH_SHORT)
                                    .show()
                                error = "All"
                                return@launch
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text = "Зарегистрироваться"
                    )
                }
            }
        }
    }
}

fun ChangeActivity(context: Context){
    context.startActivity(Intent(context, Loader::class.java))
    MainActivity().finish()
    Handler().postDelayed(Runnable {
        var intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        Loader().finish()
    }, 3000)
}