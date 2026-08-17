package com.example.hockey_app.features.training.data

import com.example.hockey_app.data.models.*
import com.example.hockey_app.data.remote.TrainingRemoteDataSource
import com.example.hockey_app.features.training.domain.repositories.TrainingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseTrainingRepository @Inject constructor(
    private val remoteDataSource: TrainingRemoteDataSource
) : TrainingRepository {

    override suspend fun getLatestTrainingPlan(clubId: String, categoria: String, division: String?): TrainingPlanModel? = 
        remoteDataSource.getLatestTrainingPlan(clubId, categoria, division)

    override suspend fun getUserCallUp(userId: String): CallUpModel? = 
        remoteDataSource.getUserCallUp(userId)

    override suspend fun postCallUps(callUps: List<CallUpModel>): Boolean = 
        remoteDataSource.postCallUps(callUps)

    override suspend fun postTrainingPlan(plan: TrainingPlanModel): Boolean = 
        remoteDataSource.postTrainingPlan(plan)
}
