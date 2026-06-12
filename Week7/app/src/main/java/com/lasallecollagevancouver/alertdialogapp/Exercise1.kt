package com.lasallecollagevancouver.alertdialogapp

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
import kotlin.jvm.java

// write a simple class for vehicles
// they have names and ids

// then place 3 vehicles in an array
// search for a vehicle by a name
// if it exists, print their ID

class Exercise1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercise1)
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
            val intent = Intent(this, Exercise2_Pt2::class.java)

            startActivity(intent)
        }

    }

    class Vehicles(var name: String, var uid: UUID = UUID.randomUUID())
    {

    }

    // EXAMPLE
/*
    val myUser = User ("John", "j@gmail.com", 23)


    //fun myFun(i: Int, b: Int) : Int
    //{
    //    return 0
    //}
}

class User (var name: String,
            val email: String,
            var age: Int,
            val  uuid: UUID = UUID.randomUUID())
{
    fun printName()
    {
        Log.d("tag", name)
    }
}
class Friend()
{
    var name: String? = null
    lateinit var email: String
    var age: Int = 0
    val uuid: UUID = UUID.randomUUID()

    init {
        name = "default"
    }

    constructor(n: String, e: String) : this ()
    {
        this.name = n
        this.email = e
    }

    */

    //EXCERCISE

}