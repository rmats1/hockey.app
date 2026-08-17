package com.example.hockey_app.features.training.domain.repositories

import com.example.hockey_app.data.models.*

interface TrainingRepository {
    suspend fun getLatestTrainingPlan(clubId: String, categoria: String, division: String?): TrainingPlanModel?
    suspend fun getUserCallUp(userId: String): CallUpModel?
    suspend fun postCallUps(callUps: List<CallUpModel>): Boolean
    suspend fun postTrainingPlan(plan: TrainingPlanModel): Boolean
}
