package ar.tvpla.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ar.tvpla.R
import ar.tvpla.ui.activities.GameActivity
import ar.tvpla.ui.activities.WebActivity
import ar.tvpla.ui.model.UrlDatabase
import ar.tvpla.ui.viewmodel.EgyptViewModel
import ar.tvpla.ui.viewmodel.EgyptViewModelFactory
import ar.tvpla.utils.Checker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingFragment : Fragment(R.layout.loading_fragment) {

    private val TAG = "custom"
    private lateinit var viewModel: EgyptViewModel
    private val checker = Checker()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = EgyptViewModelFactory(
            requireActivity().application,
            UrlDatabase(requireActivity().applicationContext)
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[EgyptViewModel::class.java]


        if (!checker.isDeviceSecured(requireActivity())) {
            starGame()
            Log.d(TAG, "passed checker")
        } else {
            viewModel.getUrlFromDb().observe(viewLifecycleOwner) { Url ->
                if (Url == null) {
                    Log.d(TAG, "passed DB check")
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.fetchDeeplink(requireActivity())
                        Log.d(TAG, "started deep")
                    }
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.urlLiveData.observe(viewLifecycleOwner) {
                            startWeb(it)
                            Log.d(TAG, "started web from new")
                        }
                    }
                } else {
                    viewModel.getUrlFromDb().observe(viewLifecycleOwner) {
                        startWeb(Url.url)
                        Log.d(TAG, "started web from db")
                    }
                }
            }
        }
    }

    private fun starGame() {
        with(Intent(requireActivity(), GameActivity::class.java)) {
            startActivity(this)
        }
    }

    private fun startWeb(url: String) {
        with(Intent(requireActivity(), WebActivity::class.java)) {
            this.putExtra("url", url)
            startActivity(this)
        }
    }
}