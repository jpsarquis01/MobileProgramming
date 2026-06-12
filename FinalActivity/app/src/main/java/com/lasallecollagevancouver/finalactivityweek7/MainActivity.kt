package com.lasallecollagevancouver.finalactivityweek7

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val searchBox = findViewById<EditText>(R.id.SerchTextBox_id)
        val resultText = findViewById<TextView>(R.id.name_id)

        val user1 = Person("Brunais", "Coquitlam", 27, "1.70")
        val user2 = Person("BongoKat", "Vancouver", 21, "1.40")
        val user3 = Person("JP", "Burnaby", 23, "1.90")
        val user4 = Person("Arthur", "Burnaby", 20, "1.75")
        val user5 = Person("Grevin", "NorthVan", 20, "1.50")
        val user6 = Person("Romeo", "Surrey", 20, "1.50")

        val userBase: Array<Person> =
            arrayOf(user1, user2, user3, user4, user5)

        searchBox.addTextChangedListener(
            object : android.text.TextWatcher
            {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                )
                {
                    val searchWord = s.toString()

                    val results = userBase.filter {
                        it.name.contains(searchWord, ignoreCase = true) ||
                                it.adress.contains(searchWord, ignoreCase = true) ||
                                it.age.toString().contains(searchWord) ||
                                it.height.contains(searchWord, ignoreCase = true)

                    }

                    if(results.isNotEmpty())
                    {
                        resultText.text = results.joinToString("\n")
                    }
                    else
                    {
                        resultText.text = "No match found"
                    }
                }

                override fun afterTextChanged(s: android.text.Editable?) {}
            }
        )
    }
}