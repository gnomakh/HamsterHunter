package ru.practicum.android.diploma.favorites.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.favorites.data.dao.FavoriteVacancyDao
import ru.practicum.android.diploma.favorites.data.entity.FavoriteVacancyEntity
import ru.practicum.android.diploma.favorites.domain.api.FavoriteVacancyRepository
import ru.practicum.android.diploma.favorites.mapper.toDomain
import ru.practicum.android.diploma.favorites.mapper.toEntity
import ru.practicum.android.diploma.search.domain.model.Vacancy

class FavoriteVacancyRepositoryImpl(
    private val favoriteVacancyDao: FavoriteVacancyDao,
) : FavoriteVacancyRepository {

    override fun getFavoriteVacancies(): Flow<List<Vacancy>> = flow {
        val vacancies = favoriteVacancyDao.getFavoriteVacancies()
        emit(convertListFromVacancyEntity(vacancies))
    }

    override suspend fun addVacancyToFavorites(vacancy: Vacancy) {
        favoriteVacancyDao.addVacancyToFavorites(vacancy.toEntity(System.currentTimeMillis()))
    }

    override suspend fun deleteVacancyFromFavorites(vacancyId: String) {
        favoriteVacancyDao.deleteVacancyFromFavorites(vacancyId)
    }

    override suspend fun isVacancyInFavorites(vacancyId: String): Boolean {
        return favoriteVacancyDao.isVacancyInFavorites(vacancyId)
    }

    private fun convertListFromVacancyEntity(vacancies: List<FavoriteVacancyEntity>): List<Vacancy> {
        return vacancies.map { vacancy -> vacancy.toDomain() }
    }
}
