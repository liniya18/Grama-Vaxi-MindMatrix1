package com.example.gramavaximindmatrix

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class VaccinationReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)

        if (!notificationsEnabled) {
            return Result.success()
        }

        val database = AppDatabase.getDatabase(applicationContext)
        val vaccinationDao = database.vaccinationDao()
        val animalDao = database.animalDao()
        val campDao = database.campDao()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // 1. Check Vaccination Reminders
        val allVaccinations = vaccinationDao.getAllVaccinations().first()
        val allAnimals = animalDao.getAllAnimals().first()

        allVaccinations.forEach { vaccination ->
            try {
                val dueDate = sdf.parse(vaccination.nextDueDate)
                if (dueDate != null) {
                    val diff = dueDate.time - today.time
                    val daysRemaining = diff / (24 * 60 * 60 * 1000)

                    // Remind if due in 1 day
                    if (daysRemaining == 1L) {
                        val animal = allAnimals.find { it.id == vaccination.animalId }
                        NotificationHelper.showVaccinationNotification(
                            applicationContext,
                            animal?.name ?: "Animal",
                            vaccination.vaccineName,
                            vaccination.nextDueDate
                        )
                    }
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }

        // 2. Check Veterinary Camp Reminders
        val allCamps = campDao.getAllCamps().first()
        allCamps.forEach { camp ->
            try {
                val campDate = sdf.parse(camp.date)
                if (campDate != null) {
                    val diff = campDate.time - today.time
                    val daysRemaining = diff / (24 * 60 * 60 * 1000)

                    // Remind if camp is tomorrow
                    if (daysRemaining == 1L) {
                        NotificationHelper.showCampNotification(applicationContext, camp)
                    }
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }

        return Result.success()
    }
}
