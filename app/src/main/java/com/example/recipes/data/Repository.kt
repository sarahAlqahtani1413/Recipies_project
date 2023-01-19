package com.example.recipes.data

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(
    remoteData: RemoteData,
    localData: LocalData
) {

    val remote = remoteData
    val local = localData

}