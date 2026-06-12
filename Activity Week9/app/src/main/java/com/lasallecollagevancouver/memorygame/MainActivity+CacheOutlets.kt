
package com.lasallecollagevancouver.memorygame

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TileViewHolder (val root: FrameLayout) : RecyclerView.ViewHolder (root)


fun MainActivity.cacheOutlets ()
{
    restartButton = findViewById(R.id.restartButton_id)
    restartButton.setOnClickListener(restart())
    gameViewRv = findViewById(R.id.gameView_id)
    gameViewRv.layoutManager = GridLayoutManager (this, AppData.gridSize)
    gameViewRv.adapter = GameAdapter()
}
