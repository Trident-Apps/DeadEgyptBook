package ar.tvpla.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ar.tvpla.R
import ar.tvpla.ui.fragments.WebFragment

class WebActivity : AppCompatActivity(R.layout.web_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("url")
        val bundle = Bundle()
        val frag = WebFragment()
        bundle.putString("url", url)
        frag.arguments = bundle
        supportFragmentManager.beginTransaction().add(R.id.web_activity_fragment_container, frag)
            .commit()

    }
}