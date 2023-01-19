package com.example.recipes.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipes.models.Joke
import com.example.recipes.util.Constants.Companion.JOKE_TABLE

@Entity(tableName = JOKE_TABLE)
class JokeEntity(
    @Embedded
    var joke: Joke
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}