package com.lasallecollagevancouver.activityweek7

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val j = Vehicles ("Jeep")
        val f = Vehicles ("Ford")
        val t = Vehicles ("Toyota")
        val b = Vehicles ("BMW")

        val vehicles: Array<Vehicles> = arrayOf(j, f, t, b)

        for (vehicle: Vehicles? in vehicles)
        {
            if (vehicle?.name == "Honda")
                Log.d("Tag", "Id is: ${vehicle.uid}")
        }

        findViewById<TextView>(R.id.myText_id).text = "there are ${vehicles.count()} cars in the hood"

        findViewById<Button>(R.id.myButton2_id).setOnClickListener {
            val intent = Intent(this, Exercise::class.java)

            startActivity(intent)
        }
    }

    class Vehicles(var name: String, var uid: UUID = UUID.randomUUID())
    {

    }
}