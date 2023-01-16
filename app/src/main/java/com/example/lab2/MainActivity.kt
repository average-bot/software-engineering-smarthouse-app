package com.example.lab2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.content.Intent
import android.speech.RecognizerIntent
import android.text.Editable
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var lamp: String
    private lateinit var door: String
    private lateinit var window: String
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    lateinit var lampSwitch: Switch
    lateinit var doorSwitch: Switch
    lateinit var windowSwitch: Switch

    private lateinit var lampImage: ImageView
    private lateinit var doorImage: ImageView
    private lateinit var windowImage: ImageView

    // code for speech to text here https://www.youtube.com/watch?v=YWNkNrmqff8
    lateinit var micImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lampSwitch = findViewById(R.id.switch_lamp)
        doorSwitch = findViewById(R.id.switch_door)
        windowSwitch = findViewById(R.id.switch_window)

        lampImage = findViewById(R.id.imageView_light)
        doorImage = findViewById(R.id.imageView_door)
        windowImage = findViewById(R.id.imageView_window)

        // db
        var database = Firebase.database("https://lab2-2059e-default-rtdb.europe-west1.firebasedatabase.app").reference

        // on load
        // retrieve all current data from db, flip switches accordingly
        database.get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            lamp = it.child("lamp").getValue(true).toString()
            door = it.child("door").getValue(true).toString()
            window = it.child("window").getValue(true).toString()
            if(lamp == "on") turnOnLights()
            if(door == "open") openDoor()
            if(window == "open") openWindow()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        // when switch flipped change state in db
        lampSwitch.setOnCheckedChangeListener { _,isChecked ->
            if (isChecked) turnOnLights() else turnOffLights()
            database.child("lamp").setValue(lamp)
        }

        doorSwitch.setOnCheckedChangeListener { _,isChecked ->
            if (isChecked) openDoor() else closeDoor()
            database.child("door").setValue(door)
        }

        windowSwitch.setOnCheckedChangeListener { _,isChecked ->
            if (isChecked) openWindow() else closeWindow()
            database.child("window").setValue(window)
        }


        // speech to text
        micImage = findViewById(R.id.imageView_mic)

        micImage.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the command")

            try {
                activityResultLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(
                        applicationContext, " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it!!.resultCode == RESULT_OK && it!!.data != null) {
                val speechText =
                    it!!.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0).toString().lowercase()
                Toast.makeText(applicationContext, speechText, Toast.LENGTH_SHORT).show()

                if (speechText == "turn on lights") turnOnLights()
                if (speechText == "turn off lights") turnOffLights()

                if (speechText == "open door") openDoor()
                if (speechText == "close door") closeDoor()

                if (speechText == "open window") openWindow()
                if (speechText == "close window") closeWindow()
                }
            }
        }


    private fun turnOnLights(){
        lamp = "on"
        lampSwitch.isChecked = true
        lampImage.setImageResource(R.drawable.light_on)
    }
    private fun turnOffLights(){
        lamp = "off"
        lampSwitch.isChecked = false
        lampImage.setImageResource(R.drawable.light_off)
    }

    private fun openDoor(){
        door = "open"
        doorSwitch.isChecked = true
        doorImage.setImageResource(R.drawable.door_open)
    }
    private fun closeDoor(){
        door = "closed"
        doorSwitch.isChecked = false
        doorImage.setImageResource(R.drawable.door_closed)
    }

    private fun openWindow(){
        window = "open"
        windowSwitch.isChecked = true
        windowImage.setImageResource(R.drawable.open_window)
    }
    private fun closeWindow(){
        window = "closed"
        windowSwitch.isChecked = false
        windowImage.setImageResource(R.drawable.closed_window)
    }
}