package mk.com.darko.giftplanner.unauthorized

import android.content.Intent
import android.view.View.GONE
import kotlinx.android.synthetic.main.part_toolbar.*
import mk.com.darko.giftplanner.BaseActivity
import mk.com.darko.giftplanner.MainActivity
import mk.com.darko.giftplanner.utils.AuthUtils

open class UnauthorizedBaseActivity : BaseActivity() {

    override fun onStart() {
        super.onStart()
        overridePendingTransition(0, 0)
        if (AuthUtils.isLoggedIn()) {
            if (AuthUtils.isEmailVerified())
                startActivity(Intent(this, MainActivity::class.java))
            else
                startActivity(Intent(this, VerifyEmailActivity::class.java))
        }

        toolbar_settings.visibility = GONE
    }
}
