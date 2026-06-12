package com.lasallecollagevancouver.countriesrvapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val countriesRv = findViewById<RecyclerView>(R.id.CountriesRV_id)
        countriesRv.layoutManager = LinearLayoutManager(this)
        countriesRv.adapter = CountriesAdapter()
    }
}

// a class that adapts from the ros to the content

class SimpleViewHolder(val view: View) : RecyclerView.ViewHolder(view)
class CountryViewHolder (val textView: TextView): RecyclerView.ViewHolder(textView)

class CountriesAdapter () : RecyclerView.Adapter<CountryViewHolder>()
{
    override fun getItemCount(): Int = AppData.countries.count()

    override fun onCreateViewHolder(container: ViewGroup, p1: Int): CountryViewHolder
    {
        val countryTextView = LayoutInflater.from(container.context)
                        .inflate(R.layout.country_row,
                         container, false) as TextView

        return CountryViewHolder(countryTextView)
    }

    override fun onBindViewHolder(viewHolder: CountryViewHolder, index: Int)
    {
        viewHolder.textView.text = AppData.countries[index]

        viewHolder.itemView.setOnClickListener
        {
            listener.onRowClicked(index)
        }
    }
}
