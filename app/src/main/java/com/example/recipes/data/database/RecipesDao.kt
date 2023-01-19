package com.example.recipes.data.database

import androidx.room.*
import com.example.recipes.data.database.entities.FavoritesEntity
import com.example.recipes.data.database.entities.JokeEntity
import com.example.recipes.data.database.entities.RecipesEntity
import com.example.recipes.data.database.entities.TriviaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJoke(jokeEntity: JokeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrivia(triviaEntity: TriviaEntity)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM joke_table ORDER BY id ASC")
    fun readJoke(): Flow<List<JokeEntity>>

    @Query("SELECT * FROM trivia_table ORDER BY id ASC")
    fun readTrivia(): Flow<List<TriviaEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()

}