package ru.practicum.android.diploma.favorites.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.search.domain.model.Vacancy

interface FavoriteVacancyRepository {

    fun getFavoriteVacancies(): Flow<List<Vacancy>>

    suspend fun addVacancyToFavorites(vacancy: Vacancy)

    suspend fun deleteVacancyFromFavorites(vacancy: Vacancy)

    suspend fun isVacancyInFavorites(vacancyId: String): Boolean
}
