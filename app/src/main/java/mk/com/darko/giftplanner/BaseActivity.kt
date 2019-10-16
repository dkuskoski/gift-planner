package mk.com.darko.giftplanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import kotlinx.android.synthetic.main.part_toolbar.*
import mk.com.darko.giftplanner.utils.ViewUtils

open class BaseActivity : Activity() {

    override fun setContentView(layoutResID: Int) {
        val root = LinearLayout(this)
        root.orientation = VERTICAL
        super.setContentView(root)

        LayoutInflater.from(this).inflate(R.layout.part_toolbar, root)
        val view = LayoutInflater.from(this).inflate(layoutResID, root)

        toolbar_settings.setOnClickListener {
            startActivity(Intent(this@BaseActivity, SettingsActivity::class.java))
        }

        toolbar.setBackgroundResource(R.drawable.lines_bg)
        toolbar_back.setOnClickListener { onBackPressed() }
        ViewUtils.setupLineBackground(view)
    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(0, 0)
    }
}
