package com.bradyaiello.fiveespells.models

import androidx.compose.ui.res.vectorResource
import com.bradyaiello.fiveespells.*
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class TextSpellInMemory(
    val name: String,
    val source: String,
    val page: Long,
    val srd: Boolean,
    val level: Long,
    val school: String,
    val savingThrow: String?,
    val ritual: Boolean,
    val timeNumber: Long,
    val timeUnit: String,
    val rangeType: String,
    val rangeDistanceUnit: String,
    val rangeDistanceAmt: String,
    val v: Boolean,
    val s: Boolean,
    val durationUnit: String,
    val durationAmt: Long,
    val durationConcentration: Boolean,
    val classes: String,
    val conditionInflicts: String?,
    val damageInflicts: String?,
    val entries: String,
    val materials: String?,
    val materialsCost: Long,
    val materialsConsumed: Boolean,
    val races: String?,
    val entriesHigherLevels: String?
)

data class SpellInMemory(
    val name: String,
    val source: String,
    val page: Long,
    val srd: Boolean,
    val level: Long,
    val school: String,
    val savingThrow: String?,
    val ritual: Boolean,
    val timeNumber: Long,
    val timeUnit: String,
    val rangeType: String,
    val rangeDistanceUnit: String,
    val rangeDistanceAmt: String,
    val v: Boolean,
    val s: Boolean,
    val durationUnit: String,
    val durationAmt: Long,
    val durationConcentration: Boolean,
    val classes: List<String>,
    val conditionInflicts: List<ConditionInflict> = listOf(),
    val damageInflicts: List<DamageInflict> = listOf(),
    val entries: String,
    val materials: String?,
    val materialsCost: Long,
    val materialsConsumed: Boolean,
    val races: List<String> = listOf(),
    val entriesHigherLevels: String?
)

fun Boolean.toLong(): Long = if (this) 1L else 0L

fun Long.toBoolean(): Boolean = this == 1L


fun TextSpellInMemory.toSpell() =
    Spell(
        name,
        source,
        page,
        srd.toLong(),
        level,
        school,
        savingThrow,
        ritual.toLong(),
        timeNumber,
        timeUnit,
        rangeType,
        rangeDistanceUnit,
        rangeDistanceAmt,
        v.toLong(),
        s.toLong(),
        durationUnit,
        durationAmt,
        durationConcentration.toLong(),
        classes,
        conditionInflicts,
        damageInflicts,
        entries,
        materials,
        materialsCost,
        materialsConsumed.toLong(),
        races,
        entriesHigherLevels
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
        timeNumber,
        timeUnit,
        rangeType,
        rangeDistanceUnit,
        rangeDistanceAmt,
        v.toBoolean(),
        s.toBoolean(),
        durationUnit,
        durationAmt,
        durationConcentration.toBoolean(),
        classes.split(", "),
        conditionInflicts?.split(", ")?.map { it.toConditionInflict() } ?: listOf(),
        damageInflicts?.split(", ")?.map { it.toDamageInflict() } ?: listOf(),
        entries,
        materials,
        materialsCost,
        materialsConsumed == 1L,
        races?.split(", ") ?: listOf(),
        entriesHigherLevels
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

fun String.toDamageInflict(): DamageInflict =
    when(this) {
        "acid" -> DamageInflict.Acid
        "bludgeoning" -> DamageInflict.Bludgeoning
        "cold" -> DamageInflict.Cold
        "fire" -> DamageInflict.Fire
        "force" -> DamageInflict.Force
        "lightning" -> DamageInflict.Lightning
        "necrotic" -> DamageInflict.Necrotic
        "piercing" -> DamageInflict.Piercing
        "poison" -> DamageInflict.Poison
        "psychic" -> DamageInflict.Psychic
        "radiant" -> DamageInflict.Radiant
        "slashing" -> DamageInflict.Slashing
        "thunder" -> DamageInflict.Thunder
        else -> {
            DamageInflict.Acid
        }
    }

sealed class DamageInflict(val type: String) {
    object Acid: DamageInflict("acid") // Sword / Skull eating away
    object Bludgeoning: DamageInflict("bludgeoning")
    object Cold: DamageInflict("cold")
    object Fire: DamageInflict("fire")
    object Force: DamageInflict("force")
    object Lightning: DamageInflict("lightning")
    object Necrotic: DamageInflict("necrotic")
    object Piercing: DamageInflict("piercing")
    object Poison: DamageInflict("poison")
    object Psychic: DamageInflict("psychic")
    object Radiant: DamageInflict("radiant")
    object Slashing: DamageInflict("slashing")
    object Thunder: DamageInflict("thunder")
}

fun DamageInflict.toVectorResource(): Int =
    when(this) {
        DamageInflict.Acid -> R.drawable.acid
        DamageInflict.Bludgeoning -> R.drawable.bludgeoning
        DamageInflict.Cold -> R.drawable.cold
        DamageInflict.Fire -> R.drawable.fire
        DamageInflict.Force -> R.drawable.force
        DamageInflict.Lightning -> R.drawable.lightning
        DamageInflict.Necrotic -> R.drawable.necrotic
        DamageInflict.Piercing -> R.drawable.piercing
        DamageInflict.Poison -> R.drawable.poison
        DamageInflict.Psychic -> R.drawable.psychic
        DamageInflict.Radiant -> R.drawable.radiant
        DamageInflict.Slashing -> R.drawable.slashing
        DamageInflict.Thunder -> R.drawable.thunder
    }

fun ConditionInflict.toVectorResource(): Int =
    when(this) {
        ConditionInflict.Blinded -> R.drawable.blinded
        ConditionInflict.Charmed -> R.drawable.charmed
        ConditionInflict.Deafened -> R.drawable.deafened
        ConditionInflict.Frightened -> R.drawable.frightened
        ConditionInflict.Incapacitated -> R.drawable.turtle
        ConditionInflict.Invisible -> R.drawable.invisible
        ConditionInflict.Paralyzed -> R.drawable.paralyzed
        ConditionInflict.Petrified -> R.drawable.petrified
        ConditionInflict.Poisoned -> R.drawable.poisoned
        ConditionInflict.Prone -> R.drawable.prone
        ConditionInflict.Restrained -> R.drawable.restrained
        ConditionInflict.Stunned -> R.drawable.stunned
        ConditionInflict.Unconscious -> R.drawable.unconscious
    }

fun String.toConditionInflict(): ConditionInflict =
    when(this) {
        "blinded" -> ConditionInflict.Blinded
        "charmed" -> ConditionInflict.Charmed
        "deafened" -> ConditionInflict.Deafened
        "frightened" -> ConditionInflict.Frightened
        "incapacitated" -> ConditionInflict.Incapacitated
        "invisible" -> ConditionInflict.Invisible
        "paralyzed" -> ConditionInflict.Paralyzed
        "petrified" -> ConditionInflict.Petrified
        "poisoned" -> ConditionInflict.Poisoned
        "prone" -> ConditionInflict.Prone
        "restrained" -> ConditionInflict.Restrained
        "stunned" -> ConditionInflict.Stunned
        "unconscious" -> ConditionInflict.Unconscious
        else -> {
            ConditionInflict.Blinded
        }
    }

sealed class ConditionInflict(val type: String) {
    object Blinded: ConditionInflict("blinded")
    object Charmed: ConditionInflict("charmed")
    object Deafened: ConditionInflict("deafened")
    object Frightened: ConditionInflict("frightened")
    object Incapacitated: ConditionInflict("incapacitated")
    object Invisible: ConditionInflict("invisible")
    object Paralyzed: ConditionInflict("paralyzed")
    object Petrified: ConditionInflict("petrified")
    object Poisoned: ConditionInflict("poisoned")
    object Prone: ConditionInflict("prone")
    object Restrained: ConditionInflict("restrained")
    object Stunned: ConditionInflict("stunned")
    object Unconscious: ConditionInflict("unconscious")
}


