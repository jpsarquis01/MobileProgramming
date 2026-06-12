package com.lasallecollagevancouver.week9

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), FragmentListener
{
    override fun removeButtonClicked()
    {
        supportFragmentManager.findFragmentByTag("MyFragTag")?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val addFragButton = findViewById<Button>(R.id.addFragButton_id)
        addFragButton.setOnClickListener {
            if(supportFragmentManager.findFragmentByTag("MyFragTag") == null)
            {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container_id,
                        MyFragments.instance(),
                        "MyFragTag")
                    .commit()
            }
        }

        val removeFragButton = findViewById<Button>(R.id.RemoveFragButton_id)
        removeFragButton.setOnClickListener {

            supportFragmentManager.findFragmentByTag("MyFragTag")?.let {
                supportFragmentManager
                    .beginTransaction()
                    .remove(it)
                    .commit()
            }
        }
    }
}