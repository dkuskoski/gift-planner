package mk.com.darko.giftplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import mk.com.darko.giftplanner.unauthorized.LoginActivity


class MainActivity : AuthActivity() {

    private var mUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getUser()
        if (mUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        populateUI()

        create_plan.setOnClickListener {
            startActivity(Intent(this, NewPlanActivity::class.java))
        }
    }

    private fun populateUI() {

    }

    private fun getUser() {
        mUser = FirebaseAuth.getInstance().currentUser
        if (mUser != null) {
            // Name, email address, and profile photo Url
            val name = mUser!!.displayName
            val email = mUser!!.email
            val photoUrl = mUser!!.photoUrl

            // Check if user's email is verified
            val emailVerified = mUser!!.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = mUser!!.uid
        }
    }
}
