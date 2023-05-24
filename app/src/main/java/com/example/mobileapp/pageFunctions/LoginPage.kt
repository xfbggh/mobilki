package com.example.mobileapp.pageFunctions

import android.annotation.SuppressLint
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mobileapp.Activities.Loader
import com.example.mobileapp.Activities.MainActivity
import com.example.mobileapp.Activities.ProfileActivity
import com.example.mobileapp.R
import com.example.mobileapp.db.MainDb
import com.example.mobileapp.db.UserModel
import com.example.mobileapp.some.TimeConverter
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.regex.Pattern


@SuppressLint("SimpleDateFormat", "CoroutineCreationDuringComposition")
@Composable
fun LoginPage(db: MainDb) {
    val context = LocalContext.current
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var IsWrap by remember { mutableStateOf(true) }

    val rememberCoroutineScope = rememberCoroutineScope()



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(text="Логин")
            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (error == "All") Color.Red else Color.Blue
                    )
            )
            Text(text="Пароль")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        if (error == "All") Color.Red else Color.Blue
                    )
            ){
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = if (IsWrap) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier
                        .weight(0.80f)
                        .height(60.dp)
                )
                Button(
                    onClick = {IsWrap = !IsWrap},
                    modifier = Modifier
                        .weight(0.20f)
                        .height(60.dp)
                        .background(Color.Blue)
                ){
                    Image(
                        painter = if (IsWrap) painterResource(id = R.drawable.closeye) else painterResource(id = R.drawable.openye),
                        contentDescription = "image",
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)

                    )
                }

            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        var InBase = false
                        val isTrueLogin = Pattern.compile("89[0-9]{9}").matcher(login).find()
                        val isTruePass = Pattern.compile("[0-9]{4}").matcher(password).find()

                        rememberCoroutineScope.launch {

                            val user_id = db.getDao().GetIdByPhone(login)
                            if (user_id !== null){
                                if (db.getDao().WhereIsUser(login, password) != null){
                                    InBase = true
                                }
                            }
                            if (user_id !== null && InBase && isTrueLogin && isTruePass) {
                                val user = db.getDao().GetAllById(user_id)
                                db.getDao().update(UserModel(user.id, user.name, user.phone, user.password, user.role, TimeConverter(), TimeConverter()))
                                Toast.makeText(
                                    context,
                                    "Успешно",
                                    Toast.LENGTH_SHORT
                                ).show()
                                error = ""
                                val userList = db.getDao().GetAllUsers()
                                SwitchEffect(context, user_id,
                                    userList as ArrayList<UserModel>)
                            } else {
                                error = "All"
                                Toast.makeText(
                                    context,
                                    "Неверные данные",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text = "Войти"
                    )
                }
            }
        }
    }
}

fun SwitchEffect(context: Context, id: Int, UserList: ArrayList<UserModel>){
    context.startActivity(Intent(context, Loader::class.java))
    MainActivity().finish()
    Handler().postDelayed(Runnable {
        var intent = Intent(context, ProfileActivity::class.java)
        val args = Bundle()
        args.putSerializable("ARRAYLIST", UserList as Serializable?)
        intent.putExtra("id", id)
        intent.putExtra("UserList", args)
        context.startActivity(intent)
        Loader().finish()
    }, 3000)

}