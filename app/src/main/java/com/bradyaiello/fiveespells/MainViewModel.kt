package com.bradyaiello.fiveespells

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.bradyaiello.fiveespells.repository.SpellRepository
import com.bradyaiello.fiveespells.work.PopulateDBWorker
import dagger.hilt.android.qualifiers.ApplicationContext


class MainViewModel @ViewModelInject constructor(
    private val repository: SpellRepository,
    @ApplicationContext private val context: Context
    ): ViewModel() {
    private val _dbPopulateWorkInfo: LiveData<WorkInfo> = setupDB()

    val dbPopulateProgress: LiveData<Float> = Transformations.map(_dbPopulateWorkInfo){
        if (it.state == WorkInfo.State.SUCCEEDED) {
            1.0F
        } else {
            it.progress.getFloat(PopulateDBWorker.PROGRESS, 0F)
        }
    }

    private fun setupDB(): LiveData<WorkInfo> {
        val insertSpellsWorkRequest =
            OneTimeWorkRequest.from(PopulateDBWorker::class.java)


        val workManager = WorkManager.getInstance(context)
        val workRequest: Operation = workManager
            .enqueueUniqueWork(
                "populateDB",
                ExistingWorkPolicy.KEEP,
                insertSpellsWorkRequest
            )

        return workManager.getWorkInfoByIdLiveData(insertSpellsWorkRequest.id)
    }
}