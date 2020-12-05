package com.bradyaiello.fiveespells.models

import com.bradyaiello.fiveespells.*
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SpellInMemory(
    val name: String,
    val source: String,
    val page: Long,
    val srd: Boolean,
    val level: Long,
    val school: String,
    val savingThrow: String?,
    val ritual: Boolean,
    val rangeType: String,
    val rangeDistanceUnit: String,
    val rangeDistanceAmt: String,
    val v: Boolean,
    val s: Boolean,
    val m: Boolean,
    val durationUnit: String,
    val durationAmt: Long,
    val durationConcentration: Boolean,
    val classes: String,
    val conditionInflicts: String?,
    val timeNumber: Long,
    val timeUnit: String,
    val damageInflicts: String?
)

fun Boolean.toLong(): Long = if (this) 1L else 0L

fun Long.toBoolean(): Boolean = this == 1L


fun SpellInMemory.toSpell() =
    Spell(
        name,
        source,
        page,
        srd.toLong(),
        level,
        school,
        savingThrow,
        ritual.toLong(),
        rangeType,
        rangeDistanceUnit,
        rangeDistanceAmt,
        v.toLong(),
        s.toLong(),
        m.toLong(),
        durationUnit,
        durationAmt,
        durationConcentration.toLong(),
        classes,
        conditionInflicts,
        timeNumber,
        timeUnit,
        damageInflicts
    )


fun Spell.toSpellInMemory() =
    SpellInMemory(
        name,
        source,
        page,
        srd.toBoolean(),
        level,
        school,
        savingThrow,
        ritual == 1L,
        rangeType,
        rangeDistanceUnit,
        rangeDistanceAmt,
        v.toBoolean(),
        s.toBoolean(),
        m.toBoolean(),
        durationUnit,
        durationAmt,
        durationConcentration.toBoolean(),
        classes,
        conditionInflicts,
        timeNumber,
        timeUnit,
        damageInflicts
    )

@JsonClass(generateAdapter = true)
data class MaterialInMemory(
    val name: String,
    val material: String
)

fun MaterialInMemory.toMaterial(): Material =
    Material(name, material)

@JsonClass(generateAdapter = true)
data class EntryInMemory(
    val name: String,
    val entryName: String?,
    val entry: String
)

fun EntryInMemory.toEntry(): Entry =
    Entry(name, entryName, entry)

@JsonClass(generateAdapter = true)
data class EntryHigherLevelInMemory(
    val name: String,
    val entryHigherLevelName: String,
    val entryHigherLevel: String,
)

fun EntryHigherLevelInMemory.toEntryHigherLevel() = EntryHigherLevel(
    name, entryHigherLevelName, entryHigherLevel
)

@JsonClass(generateAdapter = true)
data class BackgroundInMemory(
    val name: String,
    val background: String,
    val backgroundSource: String
)

fun BackgroundInMemory.toBackground() = Background(
    name, background, backgroundSource
)

@JsonClass(generateAdapter = true)
data class ClassInMemory(
    val name: String,
    val className: String,
    val classSource: String
)

fun ClassInMemory.toClass() = Class(
    name, className, classSource
)

@JsonClass(generateAdapter = true)
data class ConditionInflictsInMemory(
    val name: String,
    val conditionInflict: String
)

fun ConditionInflictsInMemory.toConditionInflicts() = ConditionInflicts(
    name, conditionInflict
)

@JsonClass(generateAdapter = true)
data class DamageInflictsInMemory(
    val name: String,
    val damageInflict: String
)

fun DamageInflictsInMemory.toDamageInflicts() = DamageInflicts(
    name, damageInflict
)

@JsonClass(generateAdapter = true)
data class SubclassInMemory(
    val name: String,
    val subclassBaseName: String,
    val subclassBaseSource: String,
    val subclassName: String,
    val subclassSource: String
)

fun SubclassInMemory.toSubclass() = Subclass(
    name, subclassBaseName, subclassBaseSource, subclassName, subclassSource
)

@JsonClass(generateAdapter = true)
data class RaceInMemory(
    val name: String,
    val race: String,
    val raceSource: String,
    val raceBaseName: String?,
    val raceBaseSource: String?
)

fun RaceInMemory.toRace() = Race(name, race, raceSource, raceBaseName, raceBaseSource)

fun SpellInMemory.getSchool(): String {
    return when (this.school) {
        "A" -> "abjuration"
        "C" -> "conjuration"
        "D" -> "divination"
        "E" -> "enchantment"
        "I" -> "illusion"
        "N" -> "necromancy"
        "T" -> "transmutation"
        "V" -> "evocation"
        else -> "no school"
    }
}
