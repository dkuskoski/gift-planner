package mk.com.darko.giftplanner.model

import java.util.*

data class Item (val uId: String = UUID.randomUUID().toString()){

    var name: String = ""
    var price: String = ""
    var description: String = ""
}