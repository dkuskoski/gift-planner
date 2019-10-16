package mk.com.darko.giftplanner

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*
import mk.com.darko.giftplanner.unauthorized.LoginActivity
import mk.com.darko.giftplanner.utils.AuthUtils

class SettingsActivity : AuthActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settings_version.text = BuildConfig.VERSION_NAME
        settings_signout.setOnClickListener {
            AuthUtils.signOut()
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}
