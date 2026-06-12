package com.lasallecollagevancouver.finalactivityweek7

class Person (val name: String, val adress: String,val age: Int, val height: String)
{
    override fun toString(): String {
        return "Name: $name\n Address: $adress\n age: $age\n, height: $height\n"
    }
}