package com.example.recipes.models

import com.google.gson.annotations.SerializedName

data class Trivia (
    @SerializedName("text")
    val text: String
)