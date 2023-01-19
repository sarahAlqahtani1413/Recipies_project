package com.example.recipes.data

import com.example.recipes.data.database.RecipesDao
import com.example.recipes.data.database.entities.FavoritesEntity
import com.example.recipes.data.database.entities.JokeEntity
import com.example.recipes.data.database.entities.RecipesEntity
import com.example.recipes.data.database.entities.TriviaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalData @Inject constructor(
    private val recipesDao: RecipesDao
) {

    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    fun readJoke(): Flow<List<JokeEntity>> {
        return recipesDao.readJoke()
    }


    fun readTrivia(): Flow<List<TriviaEntity>> {
        return recipesDao.readTrivia()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    suspend fun insertJoke(jokeEntity: JokeEntity) {
        recipesDao.insertJoke(jokeEntity)
    }

    suspend fun insertTrivia(triviaEntity: TriviaEntity) {
        recipesDao.insertTrivia(triviaEntity)
    }

    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }

}