
package com.lasallecollagevancouver.memorygame

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView

enum class Status
{
    HIDDEN, FLIPPED, MATCHED
}

data class Tile (val myContext: Context, var num: Int) : AppCompatTextView(myContext)
{
    var status = Status.HIDDEN

    fun updateTile ()
    {
        when (status)
        {
            Status.HIDDEN -> {
                this.text = "?"
                this.setBackgroundColor(android.graphics.Color.parseColor("#1565C0"))
                this.setTextColor(android.graphics.Color.WHITE)
            }
            Status.FLIPPED -> {
                this.text = "$num"
                this.setBackgroundColor(android.graphics.Color.WHITE)
                this.setTextColor(android.graphics.Color.parseColor("#212121"))
            }
            Status.MATCHED -> {
                this.text = ":)"
                this.setBackgroundColor(android.graphics.Color.parseColor("#A5D6A7"))
                this.setTextColor(android.graphics.Color.parseColor("#1B5E20"))
            }
        }
    }
}
