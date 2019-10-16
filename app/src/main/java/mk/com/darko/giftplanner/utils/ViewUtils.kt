package mk.com.darko.giftplanner.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import android.view.View
import mk.com.darko.giftplanner.R

class ViewUtils {

    companion object {
        fun setupLineBackground(view: View) {
            val displayMetrics = DisplayMetrics()
            val context = view.context as Activity
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = Math.round(context.resources.getDimension(R.dimen.row_size))

            val b = BitmapFactory.decodeResource(context.resources, R.drawable.lines_bg)

            if (b != null) {
                val bm = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(b, width, height, true))
                bm.tileModeY = Shader.TileMode.REPEAT
                view.background = bm
            }
        }
    }
}