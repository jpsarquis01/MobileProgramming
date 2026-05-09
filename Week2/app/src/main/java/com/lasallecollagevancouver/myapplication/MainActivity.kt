package com.lasallecollagevancouver.myapplication

import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity()
{
  fun content()
    {
        // val v. var
        var myVal = 45
        myVal = 65

        var country: String = "USA"

       // val button = findViewById<Button>(R.id.myButton_id)
        var lengueg: String

        val myFloat_1 = 12.12f // 16 bit
        val myFloat_2 = 12.12f // 32 bit

        val myInt = 12
        val myLong =12L

        val conversion = myFloat_1.toLong()

        val myL = 1_000_000

        var myBool: Boolean = true
        var myChar = 'A'

        val s1 = "Hello"
        val s2 = "Vanc"

        val res = s1 + " " + s2 // "$s1 $s2"

        var myChar2: Char = res[0] // val s1 = "H" 0, 1, 2...

        // Log.d(tag = "Tag", msg = "First letter: ${myChar2.toString()}")

        val age = 40

        if(age > 45)
           // Log.d( tag = "AccountActivity", msg = "Hes older")
        else if (age < 30)
           // Log.d( tag = "AccountActivity", msg = "Hes young")
        else
           // Log.d( tag = "AccountActivity", msg = "Hes the right one")


        iloop@ for( i in 1..10)
        {
            jloop@ for (j in 1..20)
            {
                if(j>12)
                    break@iloop
            }
        }

        // Collections
        val myAges = arrayOf(20, 34, 19, 9, 12)
        val mixedArray = arrayOf(12, true, "Hello", 'i')
        val myArrayNum: Array<Int> = arrayOf<Int>(1,2,3,4)

        val f = myArrayNum.first()
        val l = myArrayNum.last()

        val t = myArrayNum[2]

        val mySeq = Array(size=5, init = {i -> i*i} ) // 0, 1, 4, 9, 16

        val myList = mutableListOf<String>("Hi", "Hello")
        myList.add("Vancouver")
        myList.removeAt(index = 0)

        val userMap = hashMapOf<String, String>("Steve" to "name",
            "Vancouver" to "city",
            "45" to "age",
            "false"  to "married")
        userMap["friend"] = "Yes"


    }

    fun ClassWork(): Unit
    {
        //val rollButton = findViewById<Button>(R.id.rollButton_id)
       /* val dieImageView = findViewById<ImageView>(R.id.scissorsAI_id)

        rollButton.setOnClickListener{
            val images = arrayOf( -1,
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five,
                R.drawable.six)

            val myDie = Random.nextInt(from = 1, until = 7)

            dieImageView.setImageResource(images[myDie])
        }

        val attempts = 10_000_000

        val myDie = Random.nextInt(from = 1, until = 7)

        var counters_1 = 0
        var counters_2 = 0
        var counters_3 = 0
        var counters_4 = 0
        var counters_5 = 0
        var counters_6 = 0

        */



    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Player op
        var playerRock = findViewById<ImageView>(R.id.rockPlayer_id)
        var playerPaper = findViewById<ImageView>(R.id.paperPlayer_id)
        var playerScissors = findViewById<ImageView>(R.id.scissorsPlayer_id)

        // AI op
        var aiRock = findViewById<ImageView>(R.id.rockAI_id)
        var aiPaper = findViewById<ImageView>(R.id.paperAI_id)
        var aiScissores = findViewById<ImageView>(R.id.scissoresAi_id)

        // HUD
        var playerBox = findViewById<ImageView>(R.id.playerBox_id)
        var aiBox = findViewById<ImageView>(R.id.aiBox_id)
        var countDown = findViewById<ImageView>(R.id.countDown_id)

        // Bools
        var isRockPlayer: Boolean = false
        var isPaperPlayer: Boolean = false
        var isScissoresPlayer: Boolean = false

        var isRockAi: Boolean = false
        var isPaperAi: Boolean = false
        var isScissoresAi: Boolean = false



    }
}