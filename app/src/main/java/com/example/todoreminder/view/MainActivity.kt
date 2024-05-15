package com.example.todoreminder.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.todoreminder.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navController = (supportFragmentManager.findFragmentById(R.id.hostFragment) as
                NavHostFragment).navController
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val navView = findViewById<NavigationView>(R.id.navView)
        bottomNav.setupWithNavController(navController)
        NavigationUI.setupWithNavController(navView, navController)
    }
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
    }
}