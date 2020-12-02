package com.bradyaiello.fiveespells

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.*
import com.bradyaiello.fiveespells.models.SpellInMemoryWithClasses
import com.bradyaiello.fiveespells.repository.SpellRepository
import com.bradyaiello.fiveespells.work.PopulateDBWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val repository: SpellRepository,
    @ApplicationContext private val context: Context
    ): ViewModel() {


    @ExperimentalCoroutinesApi
    val spellStateFlow: StateFlow<DataState<List<SpellInMemoryWithClasses>>> =
        repository.getSpellsAsc().stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading)


    private var _dbPopulateProgress = MutableLiveData(0F)
    var dbPopulateProgress: LiveData<Float> = _dbPopulateProgress
        private set

    init {
        setupDB()
    }

    private fun setupDB() {
        viewModelScope.launch {
            val initialized = repository.databaseIsInitialized()
            if (initialized) {
                _dbPopulateProgress.postValue(1.0F)
            } else {
                val insertSpellsWorkRequest =
                    OneTimeWorkRequest.from(PopulateDBWorker::class.java)

                val workManager = WorkManager.getInstance(context)
                val workRequest: Operation = workManager
                    .enqueueUniqueWork(
                        "populateDB",
                        ExistingWorkPolicy.KEEP,
                        insertSpellsWorkRequest
                    )

                dbPopulateProgress =
                    Transformations.map(workManager.getWorkInfoByIdLiveData(insertSpellsWorkRequest.id)) {
                        if (it.state == WorkInfo.State.SUCCEEDED) {
                            1.0F
                        } else {
                            it.progress.getFloat(PopulateDBWorker.PROGRESS, 0F)
                        }
                    }
            }
        }
    }

}