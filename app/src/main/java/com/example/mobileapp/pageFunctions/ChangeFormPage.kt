package com.example.mobileapp.pageFunctions


import android.annotation.SuppressLint
import android.app.Activity
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
import com.example.mobileapp.Activities.ActivityForInfoChange
import com.example.mobileapp.Activities.MainActivity
import com.example.mobileapp.Activities.ProfileActivity
import com.example.mobileapp.Activities.Loader
import com.example.mobileapp.db.MainDb
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import com.example.mobileapp.R
import com.example.mobileapp.TokenIsExpired
import com.example.mobileapp.db.UserModel
import com.example.mobileapp.some.TimeConverter
import java.io.Serializable

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChangeFormPage(db: MainDb, user: UserModel){
    val context = LocalContext.current
    val rememberCoroutineScope = rememberCoroutineScope()

    val phone by remember { mutableStateOf(user.phone) }
    var password1 by remember { mutableStateOf(user.password) }
    var password2 by remember { mutableStateOf(user.password) }
    var name by remember { mutableStateOf(user.name) }
    var IsWrap1 by remember { mutableStateOf(true) }
    var IsWrap2 by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var users = ArrayList<UserModel>()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(50.dp).border(2.dp, Color.Blue),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = phone,
                )
            }
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
                    value = password1,
                    onValueChange = { password1 = it },
                    visualTransformation = if (IsWrap1) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier
                        .weight(0.80f)
                        .height(60.dp)
                )
                Button(
                    onClick = {IsWrap1 = !IsWrap1},
                    modifier = Modifier
                        .weight(0.20f)
                        .height(60.dp)
                        .background(Color.Blue)
                ){
                    Image(
                        painter = if (IsWrap1) painterResource(id = R.drawable.closeye) else painterResource(id = R.drawable.openye),
                        contentDescription = "image",
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)

                    )
                }

            }
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
                    value = password2,
                    onValueChange = { password2 = it },
                    visualTransformation = if (IsWrap2) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier
                        .weight(0.80f)
                        .height(60.dp)
                )
                Button(
                    onClick = {IsWrap2 = !IsWrap2},
                    modifier = Modifier
                        .weight(0.20f)
                        .height(60.dp)
                        .background(Color.Blue)
                ){
                    Image(
                        painter = if (IsWrap2) painterResource(id = R.drawable.closeye) else painterResource(id = R.drawable.openye),
                        contentDescription = "image",
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)

                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        val PassReg = Pattern.compile("[0-9]{4}")
                        val IsNameTrue = Pattern.compile("[А-Яа-я]+").matcher(name).find()
                        val isTruePass1 = PassReg.matcher(password1).find()
                        val isTruePass2 = PassReg.matcher(password2).find()
                        rememberCoroutineScope.launch {

                        if (TokenIsExpired(user.access, user.refresh) === "expired"){
                            db.getDao().update(
                                UserModel(
                                    user.id,
                                    user.name,
                                    user.phone,
                                    user.password,
                                    user.role,
                                    0,
                                    0
                                )
                            )
                            Toast.makeText(
                                context,
                                "Сессия истекла",
                                Toast.LENGTH_SHORT
                            ).show()
                            ChangeActivityEvent(MainActivity(), context, user.id, false, users)
                        }else {
                            if (TokenIsExpired(user.access, user.refresh) === "refresh") {
                                db.getDao().update(
                                    UserModel(
                                        user.id,
                                        user.name,
                                        user.phone,
                                        user.password,
                                        user.role,
                                        TimeConverter(),
                                        user.refresh
                                    )
                                )
                                Toast.makeText(
                                    context,
                                    "Access обновлен",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            val IsExist = db.getDao().WhereIsPass(password1, user.id) == null
                            val old_password = db.getDao().GetPassById(user.id)
                            val old_name = db.getDao().GetNameById(user.id)
                            if (isTruePass1 && isTruePass2 && IsNameTrue && password1 == password2 && IsExist && (old_password != password1 || old_name != name)
                            ) {
                                Toast.makeText(
                                    context,
                                    "Успешно!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                db.getDao().update(
                                    UserModel(
                                        user.id, name,
                                        phone, password1, user.role, user.access, user.refresh
                                    )
                                )
                                users = db.getDao().GetAllUsers() as ArrayList<UserModel>
                                ChangeActivityEvent(
                                    ProfileActivity(),
                                    context,
                                    user.id,
                                    true,
                                    users
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Неверные данные",
                                    Toast.LENGTH_SHORT
                                ).show()
                                error = "All"
                            }
                        }
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text = "Изменить данные"
                    )
                }
            }
        }
    }
}

fun ChangeActivityEvent(NewActivity: Activity, context: Context, id: Int, IsProfile: Boolean, users: ArrayList<UserModel>){
    context.startActivity(Intent(context, Loader::class.java))
    ActivityForInfoChange().finish()
    Handler().postDelayed(Runnable {
        val intent = Intent(context, NewActivity::class.java)
        if (IsProfile){
            intent.putExtra("id", id)
            val args = Bundle()
            args.putSerializable("ARRAYLIST", users as Serializable?)
            intent.putExtra("UserList", args)
        }
        context.startActivity(intent)
        Loader().finish()
    }, 3000)
}

