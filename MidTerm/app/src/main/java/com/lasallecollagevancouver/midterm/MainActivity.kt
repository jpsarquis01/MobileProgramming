package com.lasallecollagevancouver.midterm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.ProfileButton_id).setOnClickListener {
            val intent = Intent(this, ProfileSettings::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.FinfMatchButton_id).setOnClickListener {
            val intent = Intent(this, Profile1::class.java)
            startActivity(intent)
        }

    }
}