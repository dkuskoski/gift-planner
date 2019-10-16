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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import mk.com.darko.giftplanner.MainActivity
import mk.com.darko.giftplanner.R
import mk.com.darko.giftplanner.database.DatabaseConstants
import mk.com.darko.giftplanner.model.User
import mk.com.darko.giftplanner.utils.AuthUtils

class LoginActivity : UnauthorizedBaseActivity() {

    @Suppress("NO_REFLECTION_IN_CLASS_PATH")
    private val tag = LoginActivity::class.qualifiedName
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val rcSignIn: Int = 2342

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        email_sign_in_button.setOnClickListener { attemptLogin() }
        login_register_button.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        login_signin_google.setOnClickListener { googleSignin() }

        setupGoogleSignin()
    }

    private fun googleSignin() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, rcSignIn)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(tag, "Google sign in failed", e)
                Snackbar.make(login_wrapper, R.string.auth_failed, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupGoogleSignin() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(tag, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithCredential:success")
                    if (AuthUtils.isEmailVerified()) {
                        val user = mAuth.currentUser
                        val u = User(user!!.uid)
                        u.email = user.email.toString()
                        u.name = user.displayName.toString()
                        u.photoUrl = user.photoUrl.toString()
                        FirebaseDatabase.getInstance().reference.child(DatabaseConstants.table_users).setValue(u)
                            .addOnSuccessListener {
                                Log.d(tag, "saveUserInDatabase:success")
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }.addOnFailureListener {
                                Log.d(tag, "saveUserInDatabase:failure")
                                mAuth.currentUser!!.delete()
                                mAuth.signOut()
                                Snackbar.make(login_wrapper, R.string.auth_failed, Snackbar.LENGTH_LONG).show()
                            }
                    } else {
                        startActivity(Intent(this@LoginActivity, VerifyEmailActivity::class.java))
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithCredential:failure", task.exception)
                    Snackbar.make(login_wrapper, R.string.auth_failed, Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun attemptLogin() {
        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
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
            signinWithEmailAndPassword(emailStr, passwordStr)
        }
    }

    private fun signinWithEmailAndPassword(email: String, pw: String) {
        password.clearFocus()
        mAuth.signInWithEmailAndPassword(email, pw)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithEmail:success")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithEmail:failure", task.exception)
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

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
    }
}
