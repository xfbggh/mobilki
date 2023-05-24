package com.example.mobileapp.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.mobileapp.db.MainDb
import com.example.mobileapp.db.UserModel
import com.example.mobileapp.some.TabItem
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            deleteDatabase("test.db")
            val db = MainDb.getDb(this)
            FirstActivity(db)
        }

    }

}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable

fun FirstActivity(db: MainDb) {
    val tabs = listOf(TabItem.Login(db), TabItem.Register(db))
    val pagerState = rememberPagerState()
    val rememberCoroutineScope = rememberCoroutineScope()
    val user1 = UserModel(0, "Олег", "89846684130", "2000", "user", 0, 0)
    val user2 = UserModel(0, "Миша", "89587830273", "2001", "user", 0, 0)
    val user = UserModel(0, "Админ", "89274139312", "2002", "admin", 0, 0)
    rememberCoroutineScope.launch {
        if (db.getDao().GetIdByPhone("89274139312") != null){
           db.getDao().delete(user)
            db.getDao().delete(user1)
            db.getDao().delete(user2)

        }else {
            db.getDao().insertAll(user1)
            db.getDao().insertAll(user2)
            db.getDao().insertAll(user)
        }
    }
    Column {
        Tabs(tabs = tabs, pagerState = pagerState)
        TabsContent(tabs = tabs, pagerState = pagerState)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(tabs: List<TabItem>, pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    // OR ScrollableTabRow()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.Blue,
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }) {
        tabs.forEachIndexed { index, tab ->
            // OR Tab()
            LeadingIconTab(
                icon = { Icon(painter = painterResource(id = tab.icon), contentDescription = "") },
                text = { Text(tab.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabsContent(tabs: List<TabItem>, pagerState: PagerState) {
    HorizontalPager(state = pagerState, count = tabs.size) { page ->
        tabs[page].screen()
    }
}



