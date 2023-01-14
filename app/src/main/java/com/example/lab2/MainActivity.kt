package com.example.lab2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import com.example.lab2.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var lamp: String
    private lateinit var door: String
    private lateinit var window: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var lamp_switch: Switch = findViewById(R.id.switch_lamp)
        var door_switch: Switch = findViewById(R.id.switch_door)
        var window_switch: Switch = findViewById(R.id.switch_window)

        // db
        var database = Firebase.database("https://lab2-2059e-default-rtdb.europe-west1.firebasedatabase.app").reference

        // on load
        // retrieve all current data from db, flip switches accordingly
        database.get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            lamp = it.child("lamp").getValue(true).toString()
            door = it.child("door").getValue(true).toString()
            window = it.child("window").getValue(true).toString()
            if(lamp == "on"){
                lamp_switch.isChecked = true
            }
            if(door == "open"){
                door_switch.isChecked = true
            }
            if(window == "open"){
                window_switch.isChecked = true
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        // when button clicked change state in db
        lamp_switch.setOnCheckedChangeListener { _,isChecked ->
            if (isChecked) {
                lamp = "on"
            } else {
                lamp = "off"
            }
            database.child("lamp").setValue(lamp)
        }

        door_switch.setOnCheckedChangeListener { _,isChecked ->
            if (isChecked) {
                door = "open"
            } else {
                door = "closed"
            }
            database.child("door").setValue(door)
        }

        window_switch.setOnCheckedChangeListener { _,isChecked ->
            if (isChecked) {
                window = "open"
            } else {
                window = "closed"
            }
            database.child("window").setValue(window)
        }
    }
}