package com.example.recipes.data.network

import com.example.recipes.models.Joke
import com.example.recipes.models.FoodRecipe
import com.example.recipes.models.Trivia
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface FoodRecipesApi {

    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipe>

    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery: Map<String, String>
    ): Response<FoodRecipe>

    @GET("food/jokes/random")
    suspend fun getJoke(
        @Query("apiKey") apiKey: String
    ): Response<Joke>

    @GET("food/jokes/random")
    suspend fun getTrivia(
        @Query("apiKey") apiKey: String
    ): Response<Trivia>

}