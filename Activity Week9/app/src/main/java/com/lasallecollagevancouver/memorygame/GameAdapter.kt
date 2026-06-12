
package com.lasallecollagevancouver.memorygame

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView

class GameAdapter () : RecyclerView.Adapter<TileViewHolder> ()
{
    // tracks the position of the first tile
    private var firstFlippedIndex: Int = -1

    // tracks the position of the second tile
    private var secondFlippedIndex: Int = -1

    // when true, all clicks are ignored can't tap a 3rd tile while two are being compared
    private var isBoardLocked: Boolean = false

    override fun getItemCount(): Int = AppData.tileArr.count()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        index: Int
    ): TileViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.tile_layout,
            parent, false)
                as FrameLayout
        return TileViewHolder (root)
    }

    override fun onBindViewHolder(viewHolder: TileViewHolder, index: Int) {
        val tile = AppData.tileArr[index]

        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        tile.layoutParams = params
        params.setMargins(5)
        tile.textSize = 36f
        tile.gravity = Gravity.CENTER

        // detach parent to prevent crash
        (tile.parent as? ViewGroup)?.removeView(tile)
        viewHolder.root.removeAllViews()
        viewHolder.root.addView(tile)

        // draw the tile in state
        tile.updateTile()

        viewHolder.root.setOnClickListener {
            val currentIndex = viewHolder.bindingAdapterPosition

            if (currentIndex == RecyclerView.NO_ID.toInt()) return@setOnClickListener

            val currentTile = AppData.tileArr[currentIndex]

            if (isBoardLocked) return@setOnClickListener               // waiting for wrong pair to flip back
            if (currentTile.status == Status.MATCHED) return@setOnClickListener  // already matched
            if (currentTile.status == Status.FLIPPED) return@setOnClickListener  // already face up

            // flip this tile face up and redraw it
            currentTile.status = Status.FLIPPED
            currentTile.updateTile()

            if (firstFlippedIndex == -1) {
                // First click saves position
                firstFlippedIndex = currentIndex
            } else {
                // Second click compares tiles
                secondFlippedIndex = currentIndex

                // blocks board so there is no 3th click
                isBoardLocked = true

                val firstTile = AppData.tileArr[firstFlippedIndex]
                val secondTile = AppData.tileArr[secondFlippedIndex]

                if (firstTile.num == secondTile.num) {
                    // both tiles have the same number
                    // mark them as matched, redraw both green, unlock the board right away
                    firstTile.status = Status.MATCHED
                    secondTile.status = Status.MATCHED
                    firstTile.updateTile()
                    secondTile.updateTile()
                    resetSelection()
                    isBoardLocked = false
                } else {
                    // No match
                    // 
                    Handler(Looper.getMainLooper()).postDelayed({
                        firstTile.status = Status.HIDDEN
                        secondTile.status = Status.HIDDEN
                        firstTile.updateTile()
                        secondTile.updateTile()
                        resetSelection()
                        isBoardLocked = false  // unlock board after flipping back
                    }, 900L)
                }
            }
        }
    }

    // resets both tracked positions back
    private fun resetSelection() {
        firstFlippedIndex = -1
        secondFlippedIndex = -1
    }
}
