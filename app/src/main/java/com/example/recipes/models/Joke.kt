package com.example.recipes.models


import com.google.gson.annotations.SerializedName

data class Joke(
    @SerializedName("text")
    val text: String
)