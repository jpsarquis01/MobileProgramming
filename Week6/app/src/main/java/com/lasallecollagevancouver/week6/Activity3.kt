package com.lasallecollagevancouver.week6

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Activity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_3)

        val containerLL = findViewById<LinearLayout>(R.id.containerLL_id)

        val textView = TextView(this)
        textView.text  = "HELLO"
        textView.textSize = 34f
        textView.gravity = Gravity.CENTER
        textView.setBackgroundColor(Color.DKGRAY)
        textView.setTextColor(Color.WHITE)

        val param = LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        param.setMargins(0,10,0,0)

        textView.layoutParams = param
        containerLL.addView(textView)
    }
}