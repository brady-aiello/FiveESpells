package com.bradyaiello.fiveespells

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bradyaiello.fiveespells.repository.SpellRepository
import com.bradyaiello.fiveespells.work.PopulateDBWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(
    private val repository: SpellRepository,
    @ApplicationContext private val context: Context
    ): ViewModel() {

    fun setupDB() {
        val insertSpellsWorkRequest =
            OneTimeWorkRequest.from(PopulateDBWorker::class.java)

        WorkManager.getInstance(context)
            .enqueue(insertSpellsWorkRequest)
    }
}