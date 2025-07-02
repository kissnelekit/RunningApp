package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.data.Workout
import com.example.myapplication.data.WorkoutDataSerializer
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.edit_workout.EditWorkoutActivity
//import com.example.myapplication.ui.workout.CreateWorkoutActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.myapplication.ui.show_workouts.ShowWorkoutsActivity
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

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
        val sendPingButton: Button = findViewById(R.id.sendPing)
        sendPingButton.setOnClickListener {
            //sendTestWorkout()
            sendMinimalTestData()
        }
    }
    private fun sendMinimalTestData() {
        val dataMap = DataMap().apply {
            putString("test_key", "Hello Wear OS!")
            putLong("timestamp", System.currentTimeMillis())
        }

        val putRequest = PutDataMapRequest.create("/test_path").apply {
            dataMap.putAll(dataMap)
            setUrgent()
        }.asPutDataRequest()

        Wearable.getDataClient(this).putDataItem(putRequest).addOnSuccessListener {
            Log.d("MobileTest", "Minimaldaten gesendet: ${it.uri}")
        }
    }
    private fun sendTestWorkout() {
        checkWearConnection()
        val testWorkout = Workout(
            id = System.currentTimeMillis(), // Eindeutige ID
            name = "TEST-${System.currentTimeMillis()}",
            description = "Testdaten",
            date = System.currentTimeMillis(),
            length = 10.0,
            intervals = listOf()
        )
        sendWorkoutData(testWorkout)
    }
    private fun sendWorkoutData(workout: Workout) {
        val dataClient = Wearable.getDataClient(this)
        Log.d("WorkoutSync", "Versuche Workout ${workout.id} ('${workout.name}') zu senden")

        runOnUiThread {
            Toast.makeText(this, "Sende Workout an Uhr...", Toast.LENGTH_SHORT).show()
        }

        val workoutDataMap = WorkoutDataSerializer.workoutToDataMap(workout)
        val workoutPath = "${WorkoutDataSerializer.SINGLE_WORKOUT_PATH_PREFIX}${workout.id}"

        val putDataMapReq = PutDataMapRequest.create(workoutPath)
        putDataMapReq.dataMap.putAll(workoutDataMap)
        val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()

        dataClient.putDataItem(putDataReq).apply {
            addOnSuccessListener { dataItem ->
                Log.i("WorkoutSync", "Workout erfolgreich gesendet: ${dataItem.uri}")
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Workout an Uhr gesendet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            addOnFailureListener { e ->
                Log.e("WorkoutSync", "Senden fehlgeschlagen: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Fehler beim Senden: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
        }
    }
    private fun checkWearConnection() {
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            if (nodes.isEmpty()) {
                Toast.makeText(this, "Keine Uhr verbunden!", Toast.LENGTH_LONG).show()
                Log.e("Connection", "Keine verbundene Wear OS Geräte")
            } else {
                val nodeNames = nodes.joinToString { it.displayName }
                Toast.makeText(this, "Verbunden mit: $nodeNames", Toast.LENGTH_SHORT).show()
                Log.d("Connection", "Verbunden mit: $nodeNames")
            }
        }.addOnFailureListener {
            Log.e("Connection", "Fehler bei Verbindungsprüfung", it)
        }
    }

    private fun sendPing() {
        val messageClient = Wearable.getMessageClient(this)

        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                for (node in nodes) {
                    messageClient.sendMessage(
                        node.id,
                        "/ping",
                        "Hallo Uhr".toByteArray()
                    ).addOnSuccessListener {
                        Log.i("PingTest", "Ping erfolgreich an ${node.displayName}")
                    }.addOnFailureListener { e ->
                        Log.e("PingTest", "Ping fehlgeschlagen: ${e.message}")
                    }
                }
            }

    }
    private fun sendDataPing() {
        val pingMapRequest = PutDataMapRequest.create("/ping").apply {
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }
        val request = pingMapRequest.asPutDataRequest().setUrgent()
        Wearable.getDataClient(this)
            .putDataItem(request)
            .addOnSuccessListener {
                Log.i("PingTest", "DataItem /ping erfolgreich gesendet")
            }
            .addOnFailureListener { e ->
                Log.e("PingTest", "Fehler beim Senden von /ping: ${e.message}")
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