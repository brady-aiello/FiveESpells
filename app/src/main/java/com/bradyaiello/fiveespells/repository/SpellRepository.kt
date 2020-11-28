package com.bradyaiello.fiveespells.repository

import android.content.Context
import com.bradyaiello.fiveespells.DataState
import com.bradyaiello.fiveespells.Database
import com.bradyaiello.fiveespells.models.SpellInMemory
import com.bradyaiello.fiveespells.models.toSpellInMemory
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SpellRepository constructor(
    @ApplicationContext private val context: Context,
    private val spellDatabase: Database,
    private val moshi: Moshi
){
    companion object {
        private const val TAG = "SpellRepository"
    }

    private suspend fun getBackgroundsCount(): Long {
        return spellDatabase.spellQueries.getBackgroundsCount().executeAsOne()
    }

    private suspend fun getClassCount(): Long {
        return spellDatabase.spellQueries.getClassCount().executeAsOne()
    }

    private suspend fun getConditionInflictsCount(): Long {
        return spellDatabase.spellQueries.getConditionInflictsCount().executeAsOne()
    }

    private suspend fun getDamageInflictsCount(): Long {
        return spellDatabase.spellQueries.getDamageInflictsCount().executeAsOne()
    }

    private suspend fun getEntriesCount(): Long {
        return spellDatabase.spellQueries.getEntriesCount().executeAsOne()
    }

    private suspend fun getEntriesHigherLevelCount(): Long {
        return spellDatabase.spellQueries.getEntriesHigherLevelCount().executeAsOne()
    }

    private suspend fun getMaterialsCount(): Long {
        return spellDatabase.spellQueries.getMaterialsCount().executeAsOne()
    }

    private suspend fun getRaceCount(): Long {
        return spellDatabase.spellQueries.getRaceCount().executeAsOne()
    }

    private suspend fun getSpellsCount(): Long {
        return spellDatabase.spellQueries.getSpellsCount().executeAsOne()
    }

    private suspend fun getSubclassCount(): Long {
        return spellDatabase.spellQueries.getSubclassCount().executeAsOne()
    }

    suspend fun databaseIsInitialized(): Boolean {
        return try {
            val records = getBackgroundsCount() + getClassCount() + getConditionInflictsCount() +
                    getDamageInflictsCount() + getEntriesCount() + getEntriesHigherLevelCount() +
                    getMaterialsCount() + getRaceCount() + getSpellsCount() + getSubclassCount()
            records == 3485L
        } catch (e: java.lang.Exception) {
            false
        }
    }


    fun getSpellsAsc(): Flow<DataState<List<SpellInMemory>>> = flow {
        emit(DataState.Loading)
        try {
            val spellsInMemory: List<SpellInMemory> = spellDatabase.spellQueries.getSpellsSortedByName()
                .executeAsList().map { it.toSpellInMemory() }
            emit(DataState.Success(spellsInMemory))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }
/*    suspend fun insertAllSpells() {

        spellDatabase.spellQueries.insertSpell()
    }*/
}