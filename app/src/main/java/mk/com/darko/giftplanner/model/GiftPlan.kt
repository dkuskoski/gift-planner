package mk.com.darko.giftplanner.model

import java.util.*

data class GiftPlan (val uId: String = UUID.randomUUID().toString()){

    var title: String = ""
    var ownerId: String = ""
    val timeCreatedMillis = System.currentTimeMillis()
    var isAnonymous: Boolean = false
    var isPublic: Boolean = false
    var users: List<String> = ArrayList()
    var items: List<Item> = ArrayList()
}