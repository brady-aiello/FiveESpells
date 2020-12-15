package com.bradyaiello.fiveespells

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.*
import com.bradyaiello.fiveespells.models.SpellInMemory
import com.bradyaiello.fiveespells.repository.SpellRepository
import com.bradyaiello.fiveespells.work.PopulateDBWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainViewModel @ViewModelInject constructor(
    private val repository: SpellRepository,
    @ApplicationContext private val context: Context
    ): ViewModel() {

    private val _spellLiveData: MutableLiveData<DataState<List<SpellInMemory>>> = MutableLiveData(DataState.Loading)
    val spellLiveData: LiveData<DataState<List<SpellInMemory>>> = _spellLiveData

    private val _spellsExpanded: MutableLiveData<DataState<List<Boolean>>> = MutableLiveData(DataState.Loading)
    val spellsExpanded: LiveData<DataState<List<Boolean>>> = _spellsExpanded

    private var _dbPopulateProgress = MutableLiveData(0F)
    var dbPopulateProgress: LiveData<Float> = _dbPopulateProgress
        private set

    init {
        setupDB()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

    @ExperimentalCoroutinesApi
    fun expandToggle(index: Int) {
        when(spellLiveData.value) {
            is DataState.Success -> {
                val expandedDataState = _spellsExpanded.value
                if (expandedDataState is DataState.Success) {
                    val tempExpanded = expandedDataState.data.toMutableList()
                    tempExpanded[index] = !tempExpanded[index]
                    val newExpanded = tempExpanded.toList()
                    _spellsExpanded.postValue(DataState.Success(newExpanded))
                }
            }
/*            is DataState.Error -> TODO()
            DataState.Empty -> TODO()
            DataState.Loading -> TODO()
            null -> TODO()*/
        }
    }

    @ExperimentalCoroutinesApi
    private fun setupDB() {

        viewModelScope.launch {
            val initialized = repository.databaseIsInitialized()
            if (initialized) {
                _dbPopulateProgress.postValue(1.0F)
                repository
                    .getSpellsAsc()
                    .stateIn(viewModelScope, SharingStarted.Eagerly, DataState.Loading)
                    .collect{ dataStateForSpells ->
                        _spellLiveData.postValue(dataStateForSpells)
                        when(dataStateForSpells) {
                            is DataState.Success -> {
                                val expanded: List<Boolean> = List(dataStateForSpells.data.size){
                                    false
                                }
                                _spellsExpanded.postValue(DataState.Success(expanded))
                            }
/*                            is DataState.Error -> TODO()
                            DataState.Empty -> TODO()
                            DataState.Loading -> TODO()*/
                        }

                    }

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