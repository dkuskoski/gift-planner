package mk.com.darko.giftplanner.utils

import android.widget.TextView
import mk.com.darko.giftplanner.R

class TextUtils {

    companion object {
        fun setLineSpacing(textView: TextView) {
            textView.post {
                val lines = textView.lineCount
                val height = textView.context.resources.getDimension(R.dimen.row_size)
                val currentHeight = textView.height
                val spacing = ((lines * height) - currentHeight.toFloat()) / lines
                textView.height = Math.round(lines * height)
                textView.setLineSpacing(spacing, 1f)
                textView.setPadding(
                    textView.paddingLeft,
                    (spacing / 2).toInt(),
                    textView.paddingRight,
                    textView.paddingBottom
                )
                textView.requestLayout()
            }
        }
    }
}