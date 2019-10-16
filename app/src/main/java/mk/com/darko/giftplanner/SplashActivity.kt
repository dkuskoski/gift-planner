package mk.com.darko.giftplanner

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View.GONE
import kotlinx.android.synthetic.main.part_toolbar.*
import mk.com.darko.giftplanner.unauthorized.LoginActivity
import mk.com.darko.giftplanner.unauthorized.VerifyEmailActivity
import mk.com.darko.giftplanner.utils.AuthUtils


class SplashActivity : BaseActivity() {

    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        toolbar_back.visibility = GONE
        toolbar_settings.visibility = GONE
    }

    override fun onResume() {
        super.onResume()

        toolbar_title.setText("")
        val name = getString(R.string.app_name)
        val typeDelay = 300

        for (i in 0..name.length) {
            mHandler.postDelayed({
                toolbar_title.setText(name.substring(0, i))
            }, (i * typeDelay + typeDelay).toLong())
        }
        mHandler.postDelayed({
            if (AuthUtils.isLoggedIn()) {
                if (AuthUtils.isEmailVerified())
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                else
                    startActivity(Intent(this@SplashActivity, VerifyEmailActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }, (name.length * typeDelay + 1000).toLong())
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacksAndMessages(null)
    }
}
