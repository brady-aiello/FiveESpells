package com.bradyaiello.fiveespells.work

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.bradyaiello.fiveespells.Database
import com.bradyaiello.fiveespells.models.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class PopulateDBWorker @WorkerInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val moshi: Moshi,
    private val spellDatabase: Database
) : CoroutineWorker(appContext, params) {

    private var totalRowsInserted = 0

    companion object {
        private const val totalRows = 3485
        const val TAG = "PopulateDBWorker"
        const val PROGRESS = "ProgressWholeTable"
    }

    sealed class TableType {
        object Background : TableType()
        object Class : TableType()
        object ConditionInflict : TableType()
        object DamageInflict : TableType()
        object Entry : TableType()
        object EntryHigherLevel : TableType()
        object Material : TableType()
        object Race : TableType()
        object Subclass : TableType()
        object Spell : TableType()
    }

    private fun getInputStream(assetManager: AssetManager, tableType: TableType): InputStream {
        val filePath = when (tableType) {
            TableType.Spell -> "main_spell_table.json"
            TableType.Entry -> "entries.json"
            TableType.Material -> "materials.json"
            TableType.Background -> "backgrounds.json"
            TableType.Class -> "classes.json"
            TableType.ConditionInflict -> "condition_inflicts.json"
            TableType.DamageInflict -> "damage_inflict.json"
            TableType.EntryHigherLevel -> "entries_higher_level.json"
            TableType.Race -> "races.json"
            TableType.Subclass -> "subclasses.json"
        }
        return assetManager.open(filePath)
    }

    private suspend fun getJsonAndInsert(tableType: TableType, line: String) {
        try {

            when (tableType) {
                TableType.Spell -> {
                    val textSpellInMemory: TextSpellInMemory? =
                        moshi.adapter(TextSpellInMemory::class.java)
                            .fromJson(line)
                    textSpellInMemory?.apply {
                        val spell = textSpellInMemory.toSpell()
                        spellDatabase.spellQueries.insertSpell(spell)
                    }
                }
                TableType.Entry -> {
                    val entryInMemory: EntryInMemory? =
                        moshi.adapter(EntryInMemory::class.java)
                            .fromJson(line)
                    entryInMemory?.apply {
                        val entry = entryInMemory.toEntry()
                        spellDatabase.spellQueries.insertEntry(entry)
                    }
                }
                TableType.Material -> {
                    val materialInMemory: MaterialInMemory? =
                        moshi.adapter(MaterialInMemory::class.java)
                            .fromJson(line)
                    materialInMemory?.apply {
                        val material = materialInMemory.toMaterial()
                        spellDatabase.spellQueries.insertMaterial(material)
                    }
                }
                TableType.Background -> {
                    val backgroundInMemory: BackgroundInMemory? =
                        moshi.adapter(BackgroundInMemory::class.java)
                            .fromJson(line)
                    backgroundInMemory?.apply {
                        val background = backgroundInMemory.toBackground()
                        spellDatabase.spellQueries.insertBackground(background)
                    }
                }
                TableType.Class -> {
                    val classInMemory: ClassInMemory? =
                        moshi.adapter(ClassInMemory::class.java)
                            .fromJson(line)
                    classInMemory?.apply {
                        val classDB = classInMemory.toClass()
                        spellDatabase.spellQueries.insertClass(classDB)
                    }
                }
                TableType.ConditionInflict -> {
                    val conditionInflictsInMemory: ConditionInflictsInMemory? =
                        moshi.adapter(ConditionInflictsInMemory::class.java)
                            .fromJson(line)
                    conditionInflictsInMemory?.apply {
                        val conditionInflicts = conditionInflictsInMemory.toConditionInflicts()
                        spellDatabase.spellQueries.insertConditionInflicts(conditionInflicts)
                    }
                }
                TableType.DamageInflict -> {
                    val damageInflictsInMemory: DamageInflictsInMemory? =
                        moshi.adapter(DamageInflictsInMemory::class.java)
                            .fromJson(line)
                    damageInflictsInMemory?.apply {
                        val damageInflicts = damageInflictsInMemory.toDamageInflicts()
                        spellDatabase.spellQueries.insertDamageInflicts(damageInflicts)
                    }
                }
                TableType.EntryHigherLevel -> {
                    val entryHigherLevelInMemory: EntryHigherLevelInMemory? =
                        moshi.adapter(EntryHigherLevelInMemory::class.java)
                            .fromJson(line)
                    entryHigherLevelInMemory?.apply {
                        val entryHigherLevel = entryHigherLevelInMemory.toEntryHigherLevel()
                        spellDatabase.spellQueries.insertEntryHigherLevel(entryHigherLevel)
                    }
                }
                TableType.Race -> {
                    val raceInMemory: RaceInMemory? =
                        moshi.adapter(RaceInMemory::class.java)
                            .fromJson(line)
                    raceInMemory?.apply {
                        val race = raceInMemory.toRace()
                        spellDatabase.spellQueries.insertRace(race)
                    }
                }
                TableType.Subclass -> {
                    val subclassInMemory: SubclassInMemory? =
                        moshi.adapter(SubclassInMemory::class.java)
                            .fromJson(line)
                    subclassInMemory?.apply {
                        val subclass = subclassInMemory.toSubclass()
                        spellDatabase.spellQueries.insertSubclass(subclass)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getJsonAndInsert: $e")
        }
    }

    private suspend fun populateTable(
        assetManager: AssetManager,
        tableType: TableType
    ): Result {

        try {
            val inputStream = getInputStream(assetManager, tableType)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (!line.isNullOrBlank()) {
                Log.d(TAG, "populateTable: $tableType: $line")
                getJsonAndInsert(tableType, line)
                totalRowsInserted++
                if (totalRowsInserted % 60 == 0) {
                    val progress = totalRowsInserted.toFloat() / totalRows.toFloat()
                    setProgress(workDataOf(PROGRESS to progress))
                }
                line = reader.readLine()
            }
            val progress = totalRowsInserted.toFloat() / totalRows.toFloat()
            val data = workDataOf(PROGRESS to progress)
            setProgress(data)
            return Result.success()
        } catch (e: Exception) {
            Log.d(TAG, "populateTable: $tableType: $e")
        }

        return Result.failure()
    }

    override suspend fun doWork(): Result =
        withContext(IO) {
            val assetManager: AssetManager = appContext.applicationContext.assets
            //var result: Result = Result.success()
            val tableTypes = listOf(TableType.Spell, TableType.Material, TableType.Entry,
                TableType.EntryHigherLevel, TableType.Race, TableType.Background,
                TableType.Class, TableType.ConditionInflict, TableType.DamageInflict,
                TableType.Subclass
            )
            val jobs = mutableListOf<Deferred<Result>>()
            val numSpells = spellDatabase.spellQueries.getSpellsCount().executeAsOne()
            if (numSpells < 302L) {
                tableTypes.forEach {
                    jobs.add(async(IO) { populateTable(assetManager, it) })
                }
                val results = jobs.awaitAll()
                results.forEach { result ->
                    if (result != Result.success()) return@withContext result
                }
            }
            Result.success()

        }
}