package com.bradyaiello.fiveespells.repository

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.bradyaiello.fiveespells.Database
import com.bradyaiello.fiveespells.models.SpellInMemory
import com.bradyaiello.fiveespells.models.toSpell
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class SpellRepository constructor(
    @ApplicationContext private val context: Context,
    private val spellDatabase: Database,
    private val moshi: Moshi
){
    companion object {
        private const val TAG = "SpellRepository"
    }

    suspend fun getSpellsFromFile() {
        val assetManager: AssetManager = context.applicationContext.assets
        try {
            val inputStream: InputStream = assetManager
                .open("main_spell_table.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while(!line.isNullOrBlank()) {
                Log.d(TAG, "getSpellsFromFile: $line")
                line = reader.readLine()
                if (line != null) {
                    //val spellInMemory = Json.decodeFromString<SpellInMemory>(line)
                    val spellInMemory: SpellInMemory? = moshi.adapter(SpellInMemory::class.java)
                        .fromJson(line)
                    spellInMemory?.apply {
                        val spell = spellInMemory.toSpell()
                        spellDatabase.spellQueries.insertSpell(spell)
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "getSpellsFromFile: $e" )
        }

    }
/*    suspend fun insertAllSpells() {

        spellDatabase.spellQueries.insertSpell()
    }*/
}