package com.lasallecollagevancouver.week8

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val container = findViewById<FrameLayout>(R.id.container_id)

        // inflate a layout from the layout source\
        val myLayout: LinearLayout = layoutInflater.inflate(R.layout.my_layout, container,
            false) as LinearLayout

        val myButton = myLayout.findViewById<Button>(R.id.myButton_id)
        val myTextView = myLayout.findViewById<TextView>(R.id.myTextView_id)

        container.addView(myLayout)
    }
}