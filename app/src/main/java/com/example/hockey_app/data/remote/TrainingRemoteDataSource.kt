package com.example.hockey_app.data.remote

import com.example.hockey_app.data.models.*
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingRemoteDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getLatestTrainingPlan(clubId: String, categoria: String, division: String?): TrainingPlanModel? = withContext(Dispatchers.IO) {
        try {
            postgrest.from("training_plans")
                .select() {
                    filter {
                        eq("club_id", clubId)
                        eq("categoria", categoria)
                        if (division != null) eq("division", division)
                        eq("activo", true)
                    }
                    order("fecha_creacion", order = Order.DESCENDING)
                    limit(1)
                }
                .decodeSingleOrNull<TrainingPlanModel>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserCallUp(userId: String): CallUpModel? = withContext(Dispatchers.IO) {
        try {
            postgrest.from("citaciones")
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                    order("fecha_partido", order = Order.DESCENDING)
                    limit(1)
                }
                .decodeSingleOrNull<CallUpModel>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun postCallUps(callUps: List<CallUpModel>): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("citaciones").insert(callUps)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun postTrainingPlan(plan: TrainingPlanModel): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("training_plans").insert(plan)
            true
        } catch (e: Exception) {
            false
        }
    }
}
