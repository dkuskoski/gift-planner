package mk.com.darko.giftplanner

open class AuthActivity : BaseActivity() {

    override fun onStart() {
        super.onStart()
        overridePendingTransition(0, 0)
    }
}
