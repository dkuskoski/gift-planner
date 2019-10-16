package mk.com.darko.giftplanner

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_new_plan.*
import kotlinx.android.synthetic.main.part_toolbar.*
import mk.com.darko.giftplanner.utils.TextUtils

class NewPlanActivity : AuthActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plan)

        prepareViews()
    }

    private fun prepareViews() {
        prepareTitle()
        prepareAnonymous()
        preparePublic()
    }

    private fun preparePublic() {
        TextUtils.setLineSpacing(new_plan_public_text)
        new_plan_public.setOnClickListener {
            if (new_plan_public.isSelected){
                new_plan_public.isSelected = false
                new_plan_public.setImageResource(R.drawable.ic_square_24dp)
            } else {
                new_plan_public.isSelected = true
                new_plan_public.setImageResource(R.drawable.ic_cancel_24dp)
            }
        }
    }

    private fun prepareAnonymous() {
        TextUtils.setLineSpacing(new_plan_anonymous_text)
        new_plan_anonymous.setOnClickListener {
            if (new_plan_anonymous.isSelected){
                new_plan_anonymous.isSelected = false
                new_plan_anonymous.setImageResource(R.drawable.ic_square_24dp)
            } else {
                new_plan_anonymous.isSelected = true
                new_plan_anonymous.setImageResource(R.drawable.ic_cancel_24dp)
            }
        }
    }

    private fun prepareTitle() {
        toolbar_title.setText("")
        toolbar_title.isEnabled = true
        toolbar_title.setHint(R.string.title)
        toolbar_title.requestFocus()
    }
}
