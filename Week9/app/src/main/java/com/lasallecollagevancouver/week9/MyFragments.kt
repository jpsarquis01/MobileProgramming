package com.lasallecollagevancouver.week9

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

interface FragmentListener // this is whichever class that listens in for th events from the fragment
{
    fun removeButtonClicked()
}

class MyFragments : Fragment()
{
    companion object
    {
        fun instance(): MyFragments
        {
            return MyFragments()
        }
    }


    // assign this variable to whatever is the listener to tis instance of the fragment
    lateinit var listener: FragmentListener

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val myFragView = inflater.inflate(R.layout.my_fragment_layout,
                                        container, false)

        val deleteButton = myFragView.findViewById<Button>(R.id.deleteFragmentButton_id)
        deleteButton.setOnClickListener {
            listener.removeButtonClicked()

        }

        return myFragView
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)

        if (context is FragmentListener)
            listener = context
    }

    override fun onDetach()
    {
        super.onDetach()
    }
}