package mk.com.darko.giftplanner.unauthorized

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import mk.com.darko.giftplanner.R
import mk.com.darko.giftplanner.database.DatabaseConstants
import mk.com.darko.giftplanner.model.User


class RegisterActivity : UnauthorizedBaseActivity() {
    @Suppress("NO_REFLECTION_IN_CLASS_PATH")
    private val tag = RegisterActivity::class.qualifiedName
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        register_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptRegister()
                return@OnEditorActionListener true
            }
            false
        })

        register_button.setOnClickListener { attemptRegister() }
    }

    private fun attemptRegister() {
        // Reset errors.
        register_email.error = null
        register_password.error = null

        // Store values at the time of the login attempt.
        val emailStr = register_email.text.toString()
        val passwordStr = register_password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            register_password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            register_email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            register_email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            register(emailStr, passwordStr)
        }
    }

    private fun register(email: String, password: String) {
        register_password.clearFocus()
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth?.currentUser
                    if (user != null) {
                        Log.d(tag, "createUserWithEmail:success")
                        val u = User(user.uid)
                        u.email = user.email.toString()
                        u.name = user.displayName.toString()
                        u.photoUrl = user.photoUrl.toString()
                        FirebaseDatabase.getInstance().reference.child(DatabaseConstants.table_users).setValue(u)
                            .addOnSuccessListener {
                                Log.d(tag, "saveUserInDatabase:success")
                                user.sendEmailVerification()
                                startActivity(Intent(this@RegisterActivity, VerifyEmailActivity::class.java))
                                finish()
                            }.addOnFailureListener {
                                Log.d(tag, "saveUserInDatabase:failure")
                                mAuth!!.currentUser!!.delete()
                                mAuth!!.signOut()
                                Snackbar.make(login_wrapper, R.string.auth_failed, Snackbar.LENGTH_LONG).show()
                                showProgress(false)
                            }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "createUserWithEmail:failure", task.exception)
                    Snackbar.make(login_wrapper, R.string.auth_failed, Snackbar.LENGTH_LONG).show()
                    showProgress(false)
                }
            }
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 6
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        register_form.visibility = if (show) View.GONE else View.VISIBLE
        register_form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    register_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        register_progress.visibility = if (show) View.VISIBLE else View.GONE
        register_progress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    register_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
    }
}
