package com.example.recipes.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Parcelable
import androidx.lifecycle.*
import com.example.recipes.data.Repository
import com.example.recipes.data.database.entities.FavoritesEntity
import com.example.recipes.data.database.entities.JokeEntity
import com.example.recipes.data.database.entities.RecipesEntity
import com.example.recipes.data.database.entities.TriviaEntity
import com.example.recipes.models.Joke
import com.example.recipes.models.FoodRecipe
import com.example.recipes.models.Trivia
import com.example.recipes.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    var recyclerViewState: Parcelable? = null

    /** ROOM DATABASE */

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()
    val readJoke: LiveData<List<JokeEntity>> = repository.local.readJoke().asLiveData()
    val readTrivia: LiveData<List<TriviaEntity>> = repository.local.readTrivia().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipes(favoritesEntity)
        }

    private fun insertJoke(jokeEntity: JokeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertJoke(jokeEntity)
        }

    private fun insertTrivia(triviaEntity: TriviaEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertTrivia(triviaEntity)
        }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    fun deleteAllFavoriteRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }

    /** RETROFIT */
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var jokeResponse: MutableLiveData<NetworkResult<Joke>> = MutableLiveData()
    var triviaResponse: MutableLiveData<NetworkResult<Trivia>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    fun getJoke(apiKey: String) = viewModelScope.launch {
        getJokeSafeCall(apiKey)
    }

    fun getTrivia(apiKey: String) = viewModelScope.launch {
        getTriviaSafeCall(apiKey)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            recipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getJokeSafeCall(apiKey: String) {
        jokeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getJoke(apiKey)
                jokeResponse.value = handleJokeResponse(response)

                val foodJoke = jokeResponse.value!!.data
                if(foodJoke != null){
                    offlineCacheJoke(foodJoke)
                }
            } catch (e: Exception) {
                jokeResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            jokeResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getTriviaSafeCall(apiKey: String) {
        triviaResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getTrivia(apiKey)
                triviaResponse.value = handleTriviaResponse(response)

                val trivia = triviaResponse.value!!.data
                if(trivia != null){
                    offlineCacheTrivia(trivia)
                }
            } catch (e: Exception) {
                triviaResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            triviaResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun offlineCacheJoke(joke: Joke) {
        val jokeEntity = JokeEntity(joke)
        insertJoke(jokeEntity)
    }

    private fun offlineCacheTrivia(trivia: Trivia) {
        val triviaEntity = TriviaEntity(trivia)
        insertTrivia(triviaEntity)
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleJokeResponse(response: Response<Joke>): NetworkResult<Joke> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val joke = response.body()
                NetworkResult.Success(joke!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleTriviaResponse(response: Response<Trivia>): NetworkResult<Trivia> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val trivia = response.body()
                NetworkResult.Success(trivia!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}