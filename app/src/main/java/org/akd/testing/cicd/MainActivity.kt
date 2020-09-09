package org.akd.testing.cicd

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.legatotechnologies.updater.ForceUpdate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val json = ForceUpdate.initUpdateJSon(
            "https://developer.android.com/about?gclid=EAIaIQobChMI8aOCnpvb6wIVlJvCCh03RwFBEAAYASAAEgLp1vD_BwE&gclsrc=aw.ds",
            "3.0",
            "Android 11, the newest version of Android, sets you up to take advantage of a range of new experiences, from foldable devices to stronger protections for your users.",
            0
        )

        val updater = ForceUpdate(this)
            .setJSON(json)
            .setTheme(R.style.AlertDialogCustom)
            .setCustomView(R.layout.dialog_new_version)
            .setNotificationTime(30, ForceUpdate.Milli)
            .start()

        Log.d("MainActivity", "version=${updater.version}, milliseconds=${updater.milliseconds}")
    }
}