
package com.lasallecollagevancouver.memorygame

import android.view.View

fun MainActivity.restart (): View.OnClickListener {
    return View.OnClickListener{
        AppData.createTiles(this)
        gameViewRv.adapter = GameAdapter()
    }
}
