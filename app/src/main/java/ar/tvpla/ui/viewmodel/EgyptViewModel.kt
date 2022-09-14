package ar.tvpla.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ar.tvpla.ui.model.Url
import ar.tvpla.ui.model.UrlDatabase
import ar.tvpla.utils.Const
import ar.tvpla.utils.TagSender
import ar.tvpla.utils.UriBuilder
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EgyptViewModel(application: Application, private val db: UrlDatabase) :
    AndroidViewModel(application) {

    private val TAG = "customVM"
    private val sender = TagSender()
    private val builder = UriBuilder()

    val urlLiveData: MutableLiveData<String> = MutableLiveData()

    fun fetchDeeplink(activity: Activity) {
        AppLinkData.fetchDeferredAppLinkData(activity) {
            val deepLink = it?.targetUri.toString()
            Log.d(TAG, "in deep")
            if (deepLink == "null") {
                fetchAppsData(activity)
                Log.d(TAG, "started apps")
            } else {
                urlLiveData.postValue(builder.createUrl(deepLink, null, activity))
                sender.sendTag(deepLink, null)
            }
        }
    }

    private fun fetchAppsData(activity: Activity) {
        AppsFlyerLib.getInstance().init(Const.APPS_DEV_KEY, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                urlLiveData.postValue(builder.createUrl("null", data, activity))
                sender.sendTag("null", data)
                Log.d(TAG, "aps success")
            }

            override fun onConversionDataFail(data: String?) {Log.d(TAG, "apps fail")}

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {}

            override fun onAttributionFailure(data: String?) {}
        }, activity)
        AppsFlyerLib.getInstance().start(activity)
    }

    fun getUrlFromDb() = db.getDao().getUrl()

    fun saveUrlToDb(url: Url){
        db.getDao().insertUrl(url)
    }

    fun getUrlSync(): Url?{
        return db.getDao().getUrlSync()
    }

}