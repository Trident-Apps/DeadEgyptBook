package ar.tvpla.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object Const {

    const val ONESIGNAL_ID = "95a3b3e4-1de3-42fc-b257-6a69afecb8d0"
    const val APPS_DEV_KEY = "Pd3MThiaa6rxbx86xeyFud"
    const val BASE_URL = "https://deadegyptbook.online/deadegypt.php"

    const val SECURE_GET_PARAMETR = "Ybn692vot4"
    const val SECURE_KEY = "02b55X9oCn"
    const val DEV_TMZ_KEY = "Dk1wwx3ce6"
    const val GADID_KEY = "8ipygoepsA"
    const val DEEPLINK_KEY = "89cXUT7kGX"
    const val SOURCE_KEY = "ualpfH9ko9"
    const val AF_ID_KEY = "rzEKhoJxRT"
    const val ADSET_ID_KEY = "FEmr1qGSxe"
    const val CAMPAIGN_ID_KEY = "tJcz6hIo9M"
    const val APP_COMPAIGN_KEY = "w2q0oNBBsy"
    const val ADSET_KEY = "DFqAmWFizX"
    const val ADGROUP_KEY = "6yVAuMj101"
    const val ORIG_COST_KEY = "szoeBDZGdg"
    const val AF_SITEID_KEY = "94j1zllsIT"
}


fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}