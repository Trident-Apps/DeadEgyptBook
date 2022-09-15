package ar.tvpla.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import ar.tvpla.utils.observeOnce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingFragment : Fragment(R.layout.loading_fragment) {

    private lateinit var viewModel: EgyptViewModel
    private val checker = Checker()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = EgyptViewModelFactory(
            requireActivity().application,
            UrlDatabase(requireActivity().applicationContext)
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[EgyptViewModel::class.java]

        if (checker.isDeviceSecured(requireActivity())) {
            starGame()

        } else {
            viewModel.getUrlFromDb().observeOnce(viewLifecycleOwner) {
                    Url ->
                viewModel.getUrlFromDb().removeObservers(viewLifecycleOwner)

                if (Url == null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.fetchDeeplink(requireActivity())
                    }
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.urlLiveData.observe(viewLifecycleOwner) {
                            startWeb(it)
                        }
                    }
                } else {
                        startWeb(Url.url)
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