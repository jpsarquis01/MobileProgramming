
package com.lasallecollagevancouver.memorygame

import android.content.Context

class AppData
{
    companion object
    {
        var gridSize: Int = 4

        var tileArr: ArrayList<Tile> = ArrayList()

        fun createTiles (context: Context)
        {
            tileArr.clear()
            val pairCount = gridSize * gridSize / 2  // 8 pairs for 4x4

            for (value in 1..pairCount)
            {
                tileArr.add(Tile(context, value))
                tileArr.add(Tile(context, value))
            }

            tileArr.shuffle() // random
        }
    }
}
