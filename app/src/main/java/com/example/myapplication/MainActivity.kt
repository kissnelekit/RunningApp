package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.edit_workout.EditWorkoutActivity
//import com.example.myapplication.ui.workout.CreateWorkoutActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.myapplication.ui.show_workouts.ShowWorkoutsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_calender,
                R.id.navigation_statistics
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val menuButton: FloatingActionButton = findViewById(R.id.menu_button)
        menuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }

        val createWorkoutButton: Button = findViewById(R.id.button_create_workout)
        createWorkoutButton.setOnClickListener {
            openCreateWorkoutActivity()
        }
        val showWorkoutListButton: Button = findViewById(R.id.show_workout_list)
        showWorkoutListButton.setOnClickListener {
            openShowWorkoutListActivity()
        }


    }
    private fun openCreateWorkoutActivity() {
        val intent = Intent(this, EditWorkoutActivity::class.java)
        startActivity(intent)
    }
    private fun openShowWorkoutListActivity() {
        val intent = Intent(this, ShowWorkoutsActivity::class.java)
        startActivity(intent)
    }
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.burger_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_settings -> {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                    true
                }
                R.id.menu_profile -> {
                    // Profil öffnen
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}