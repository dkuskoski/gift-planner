package mk.com.darko.giftplanner.unauthorized

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_verify_email.*
import mk.com.darko.giftplanner.AuthActivity
import mk.com.darko.giftplanner.MainActivity
import mk.com.darko.giftplanner.R
import mk.com.darko.giftplanner.utils.AuthUtils
import mk.com.darko.giftplanner.utils.TextUtils


class VerifyEmailActivity : AuthActivity() {

    private val mHandler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mk.com.darko.giftplanner.R.layout.activity_verify_email)
        TextUtils.setLineSpacing(verify_email_text)
        resend_confirmation_mail.setOnClickListener {
            val sent = AuthUtils.sendVerificationEmail()
            if (!sent) {
                if (AuthUtils.isLoggedIn())
                    startActivity(Intent(this@VerifyEmailActivity, MainActivity::class.java))
                else
                    startActivity(Intent(this@VerifyEmailActivity, LoginActivity::class.java))
                finish()
            } else {
                Snackbar.make(verify_email_wrapper, R.string.email_sent, Snackbar.LENGTH_INDEFINITE).show()
                resend_confirmation_mail.isEnabled = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startIsEmailVerifiedChecker()
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacksAndMessages(null)
    }

    private fun startIsEmailVerifiedChecker() {
        mHandler.postDelayed({
            if (AuthUtils.isEmailVerified()) {
                startActivity(Intent(this@VerifyEmailActivity, MainActivity::class.java))
            } else {
                startIsEmailVerifiedChecker()
            }
        }, 2000)
    }
}
