package mk.com.darko.giftplanner.utils

import com.google.firebase.auth.FirebaseAuth

class AuthUtils {

    companion object {
        fun isLoggedIn(): Boolean {
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                return true
            }
            return false
        }

        fun isEmailVerified(): Boolean {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                currentUser.reload()
                if (currentUser.isEmailVerified)
                    return true
            }
            return false
        }

        fun sendVerificationEmail(): Boolean {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null || currentUser.isEmailVerified) {
                return false
            }
            currentUser.sendEmailVerification()
            return true
        }

        fun signOut() {
            FirebaseAuth.getInstance().signOut()
        }
    }
}