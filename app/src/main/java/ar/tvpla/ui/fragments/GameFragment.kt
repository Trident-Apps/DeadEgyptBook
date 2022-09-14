package ar.tvpla.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ar.tvpla.R
import ar.tvpla.databinding.GameFragmentBinding

class GameFragment : Fragment() {
    private var _binding: GameFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOf(
            binding.imageView, binding.imageView2, binding.imageView3,
        ).forEach { imageView ->
            imageView.setOnClickListener {
                playGame(it as ImageView)
            }
        }
        binding.gameBtn.setOnClickListener {
            repeatGame()
        }
    }

    private fun playGame(view: View) {

        view.animate().apply {
            duration = 1000
            rotationYBy(360f)
        }.withEndAction {
            showResult()
            val randomList = listOf(1, 2, 3).shuffled()
            if (randomList[0] == 1) {
                binding.imageView4.visibility = View.VISIBLE
                binding.resultText.visibility = View.VISIBLE
                binding.imageView4.setImageResource(R.drawable.ic3)
                binding.resultText.text = resources.getString(R.string.won_text)
                binding.resultText.setTextColor(
                    Color.parseColor(resources.getString(R.string.won_color))
                )
            } else {
                binding.imageView4.visibility = View.VISIBLE
                binding.resultText.visibility = View.VISIBLE
                binding.imageView4.setImageResource(R.drawable.ic1)
                binding.resultText.text = resources.getString(R.string.loose_text)
                binding.resultText.setTextColor(
                    Color.parseColor(resources.getString(R.string.loose_color)))
            }
        }
    }

    private fun showResult() {
        binding.imageView.visibility = View.INVISIBLE
        binding.imageView2.visibility = View.INVISIBLE
        binding.imageView3.visibility = View.INVISIBLE
        binding.gameBtn.visibility = View.VISIBLE
    }

    private fun repeatGame() {
        binding.imageView.visibility = View.VISIBLE
        binding.imageView2.visibility = View.VISIBLE
        binding.imageView3.visibility = View.VISIBLE
        binding.imageView4.visibility = View.INVISIBLE
        binding.resultText.visibility = View.INVISIBLE
        binding.gameBtn.visibility = View.INVISIBLE
        binding.gameBtn.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}