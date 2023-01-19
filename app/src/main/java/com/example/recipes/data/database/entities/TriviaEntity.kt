package com.example.recipes.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipes.models.Trivia
import com.example.recipes.util.Constants

@Entity(tableName = Constants.TRIVIA_TABLE)
class TriviaEntity (
    @Embedded
    var trivia: Trivia
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}