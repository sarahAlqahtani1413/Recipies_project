package com.example.recipes.ui.fragments.foodjoke

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.recipes.R
import com.example.recipes.databinding.FragmentFoodJokeBinding
import com.example.recipes.util.Constants.Companion.API_KEY
import com.example.recipes.util.NetworkResult
import com.example.recipes.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class FoodJokeFragment : Fragment() {

    private val mainViewModel by viewModels<MainViewModel>()

    private var _binding: FragmentFoodJokeBinding? = null
    private val binding get() = _binding!!

    private lateinit var textToSpeech: TextToSpeech

    private var funnyText = "No  Funny text"
    private val RQ_SPEECH_REC = 102

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodJokeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainViewModel = mainViewModel

        textToSpeech = TextToSpeech(requireContext()){
            textToSpeech.language = Locale.ENGLISH
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.food_joke_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.share_fun_menu) {
                    val shareIntent = Intent().apply {
                        this.action = Intent.ACTION_SEND
                        this.putExtra(Intent.EXTRA_TEXT, funnyText)
                        this.type = "text/plain"
                    }
                    startActivity(shareIntent)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        runTrivia()
        runJoke()

        binding.btnTapToSpeech.setOnClickListener { handleSpeechToText() }

        return binding.root
    }

    private fun runJoke(){
        mainViewModel.getJoke(API_KEY)
        mainViewModel.jokeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.foodJokeTextView.text = response.data?.text
                    if (response.data != null) {
                        funnyText = response.data.text
                        textToSpeech.speak(funnyText, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
                is NetworkResult.Error -> {
                    loadJokeDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    Log.d("FoodJokeFragment", "Loading")
                }
            }
        }
    }

    private fun runTrivia(){
        mainViewModel.getTrivia(API_KEY)
        mainViewModel.triviaResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.foodJokeTextView.text = response.data?.text
                    if (response.data != null) {
                        funnyText = response.data.text
                        textToSpeech.speak(funnyText, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
                is NetworkResult.Error -> {
                    loadTriviaDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    Log.d("FoodJokeFragment", "Loading")
                }
            }
        }
    }

    private fun handleSpeechToText(){
        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())){
            Toast.makeText(requireContext(),"Speech is not available!!",Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say word joke or trivia")
            startActivityForResult(intent,RQ_SPEECH_REC)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0).toString()
            Log.e("TAG ::",result)

            if (result.contains("joke")){
                runJoke()
            }else if (result.contains("trivia")){
                runTrivia()
            }else{
                Toast.makeText(requireContext(),"please say joke or trivia",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadJokeDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readJoke.observe(viewLifecycleOwner) { database ->
                if (!database.isNullOrEmpty()) {
                    binding.foodJokeTextView.text = database.first().joke.text
                    funnyText = database.first().joke.text
                    textToSpeech.speak(funnyText, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }
    private fun loadTriviaDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readTrivia.observe(viewLifecycleOwner) { database ->
                if (!database.isNullOrEmpty()) {
                    binding.foodJokeTextView.text = database.first().trivia.text
                    funnyText = database.first().trivia.text
                    textToSpeech.speak(funnyText, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}