package com.example.recipes.data

import com.example.recipes.data.network.FoodRecipesApi
import com.example.recipes.models.Joke
import com.example.recipes.models.FoodRecipe
import com.example.recipes.models.Trivia
import retrofit2.Response
import javax.inject.Inject

class RemoteData @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.getRecipes(queries)
    }

    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.searchRecipes(searchQuery)
    }

    suspend fun getJoke(apiKey: String): Response<Joke> {
        return foodRecipesApi.getJoke(apiKey)
    }

    suspend fun getTrivia(apiKey: String): Response<Trivia> {
        return foodRecipesApi.getTrivia(apiKey)
    }

}