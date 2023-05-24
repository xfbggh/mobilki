package com.example.mobileapp.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mobileapp.TokenIsExpired
import com.example.mobileapp.db.MainDb
import com.example.mobileapp.db.UserModel
import com.example.mobileapp.some.TimeConverter
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

private val gpsLocationListener = object : LocationListener {
    var cachedLocation: Location? = null

    override fun onLocationChanged(location: Location) {
        cachedLocation = location
    }
}

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val db = MainDb.getDb(this)
            val id = intent.extras?.getInt("id")
            val args = intent.getBundleExtra("UserList")
            val arr = args!!.getSerializable("ARRAYLIST") as ArrayList<UserModel>
            if (id != null) {
                MainPage(id, db, arr)
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@Composable
fun MainPage(id: Int, db: MainDb, arr: ArrayList<UserModel>){
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    try {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            1000, 0f, gpsLocationListener)
    }
    catch (e: SecurityException){

    }
    var user = arr[id - 1]
    var IsAdmin by remember { mutableStateOf(false) }
    var checkedUser by remember { mutableStateOf("") }
    var cityName by remember { mutableStateOf("") }
    val rememberCoroutineScope = rememberCoroutineScope()
    var response by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick={
                    var Page = Intent(context, MainActivity::class.java)
                    rememberCoroutineScope.launch {
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
                    }
                    context.startActivity(Intent(context, Loader::class.java))
                    ProfileActivity().finish()
                    Handler().postDelayed(Runnable {
                        context.startActivity(Page)
                        Loader().finish()
                    }, 3000)
                },
                    modifier = Modifier
                        .width(120.dp)
                        .height(60.dp)
                        .background(Color.Blue)
                ){
                    Text(text="Выйти")
                }
                Button(
                    onClick = {
                        rememberCoroutineScope.launch {
                            var Page: Intent? = null
                            if (TokenIsExpired(user.access, user.refresh) === "expired") {
                                Page = Intent(context, MainActivity::class.java)
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
                            }
                            if (TokenIsExpired(user.access, user.refresh) === "active"){
                                Page = Intent(context, ActivityForInfoChange::class.java)
                                Page.putExtra("user", user)
                            }
                            if (TokenIsExpired(user.access, user.refresh) === "refresh"){
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
                                    "Access обновел",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Page = Intent(context, ActivityForInfoChange::class.java)
                                user = db.getDao().GetAllById(user.id)
                                Page.putExtra("user", user)
                            }

                            context.startActivity(Intent(context, Loader::class.java))
                            ProfileActivity().finish()
                            Handler().postDelayed(Runnable {
                                context.startActivity(Page)
                                Loader().finish()
                            }, 3000)
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(60.dp)
                        .background(Color.Blue)
                ) {
                    Text(text = user.name)
                }
            }
            if (user.role == "user") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.Blue),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        if (response == "") Text("") else Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Image(
                                painter = rememberImagePainter(response.split("-")[1]),
                                contentDescription = "My content description",
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(100.dp)
                            )
                        }
                        if (response == "") Text("") else Column(
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = response.split("-")[2] + " C",
                                Modifier.padding(10.dp),
                                fontSize = 40.sp, fontWeight = FontWeight.Bold, color=Color.White
                            )
                            Text(text = response.split("-")[0], fontSize = 30.sp, fontWeight = FontWeight.Light, color=Color.White)
                        }
                    }
                    Column(modifier= Modifier
                        .fillMaxWidth()
                        .height(200.dp), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally){
                        OutlinedTextField(
                            value = cityName,
                            onValueChange = { cityName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color.Blue, shape = RectangleShape)
                                .background(color = Color.White),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Blue)
                        )
                        Button(modifier=Modifier.padding(5.dp), onClick={
                            val key="406e13154b45fb6e5c74e999da7e36be"
                            val url = "http://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&lang=ru&APPID=$key"
                            val queue = Volley.newRequestQueue(context)
                            val stringRequest = StringRequest(
                                Request.Method.GET,
                                url,
                                {
                                        res->
                                    val description = JSONObject(res).getJSONArray("weather").getJSONObject(0).getString("description")
                                    val icon = JSONObject(res).getJSONArray("weather").getJSONObject(0).getString("icon")
                                    val temp = JSONObject(res).getJSONObject("main").getString("temp")

                                    response = "$description-https://openweathermap.org/img/wn/$icon.png-$temp"
                                })
                            {
                            }
                            queue.add(stringRequest)
                    }){
                            Text(text="Получить погоду")
                        }

                        Button(onClick={
                            val location = gpsLocationListener.cachedLocation
                            val lat = location?.latitude
                            val long = location?.longitude
                            Log.d("MyLog", location.toString())
                            Log.d("MyLog", lat.toString())
                            Log.d("MyLog", long.toString())
                            val key="406e13154b45fb6e5c74e999da7e36be"
                            val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&units=metric&lang=ru&appid=$key"
                            val queue = Volley.newRequestQueue(context)
                            val stringRequest = StringRequest(
                                Request.Method.GET,
                                url,
                                {
                                        res->
                                    val description = JSONObject(res).getJSONArray("weather").getJSONObject(0).getString("description")
                                    val icon = JSONObject(res).getJSONArray("weather").getJSONObject(0).getString("icon")
                                    val temp = JSONObject(res).getJSONObject("main").getString("temp")

                                    response = "$description-https://openweathermap.org/img/wn/$icon.png-$temp"
                                })
                            {
                            }
                            queue.add(stringRequest)
                        }){
                            Text(text="По текущей геолокации")
                        }
                    }
                }
            }
            else{
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.LightGray),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Text(text="Открыть меню администратора")
                    Checkbox(checked = IsAdmin, onCheckedChange = {value -> IsAdmin = value})
                    if (IsAdmin) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .height(1000.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround){
                            UserList(arr, user)

                            Text("Введите id пользователя")
                            OutlinedTextField(
                                value = checkedUser,
                                onValueChange = { checkedUser = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        2.dp,
                                        Color.Blue
                                    )
                            )
                            Button(
                                onClick = {
                                    var Page: Intent? = null
                                    rememberCoroutineScope.launch {
                                        if (TokenIsExpired(user.access, user.refresh) === "expired") {
                                            Page = Intent(context, MainActivity::class.java)
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
                                            context.startActivity(Intent(context, Loader::class.java))
                                            ProfileActivity().finish()
                                            Handler().postDelayed(Runnable {
                                                context.startActivity(Page)
                                                Loader().finish()
                                            }, 3000)
                                        }
                                        if (TokenIsExpired(user.access, user.refresh) === "refresh"){
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
                                    }
                                    if (checkedUser !== "" && checkedUser.toInt()-1 <= arr.size && checkedUser.toInt()-1 >= 0 && checkedUser.toInt() != user.id){
                                        rememberCoroutineScope.launch {
                                            var int_id = checkedUser.toInt()
                                            val user = UserModel(int_id, arr.get(int_id-1).name, arr.get(int_id-1).phone, arr.get(int_id-1).password, "admin", 0, 0)
                                            db.getDao().update(user)
                                            Toast.makeText(
                                                context,
                                                "Роль пользователя изменена",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }else{
                                        Toast.makeText(
                                            context,
                                            "Неверно указан id",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier
                                    .width(800.dp)
                                    .height(60.dp)
                                    .background(Color.Blue)
                            ) {
                                Text("Изменить статус пользователя")
                            }
                        }

                    }
                }
            }
    }
}

@Composable
fun UserList(arr: ArrayList<UserModel>, user: UserModel){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text("id")
        Text("name")
        Text("phone")
        Text("password")
        Text("role")
    }
    for (item in arr) {
        if (item.id != user.id){
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = item.id.toString())
                Text(text = item.name)
                Text(text = item.phone)
                Text(text = item.password)
                Text(text = item.role)
            }
        }
    }
}