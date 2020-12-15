package com.bradyaiello.fiveespells

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.bradyaiello.fiveespells.models.SpellInMemory
import com.bradyaiello.fiveespells.models.getSchool
import com.bradyaiello.fiveespells.models.toVectorResource
import com.bradyaiello.fiveespells.ui.FiveESpellsTheme
import com.bradyaiello.fiveespells.utils.observeAsStateWithPolicy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    @ExperimentalCoroutinesApi
    private val viewModel: MainViewModel by viewModels()

    @ExperimentalAnimationApi
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val progress by viewModel.dbPopulateProgress.observeAsState()

            val spellsState: State<DataState<List<SpellInMemory>>> =
                viewModel.spellLiveData.observeAsStateWithPolicy(DataState.Loading,
                        referentialEqualityPolicy()
                )

            val spellsExpandedState: State<DataState<List<Boolean>>> =
                viewModel.spellsExpanded.observeAsStateWithPolicy(DataState.Loading)

            FiveESpellsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column(modifier = Modifier.fillMaxSize()) {
                       TopAppBar(title = { Text(text = "5e Spells") })
                        progress?.let {
                            if (it < 1.0F) {
                                SpellsProgressIndicator(
                                    progress = it,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        SpellsList(
                                spellsState = spellsState,
                                spellsExpandedState = spellsExpandedState,
                                modifier = Modifier.fillMaxSize(),
                                expandToggle =  viewModel::expandToggle
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun SpellCard(spell: SpellInMemory, expanded: Boolean = false, ndx: Int, expandToggle: (Int) -> Unit) { // onClickExpander: () -> Unit) {

    Card(
        elevation = 8.dp,
        modifier = Modifier.padding(6.dp)
        .clickable(onClick =  {
            expandToggle(ndx)
        } )
        //.clickable(onClick =  { expandToggle(spell.name) } )
    ) {
        ConstraintLayout(modifier = Modifier.padding(6.dp)) {
            val (
                name,
                level,
                school,
                classesRef,
                iconConditionInflictsRef,
                iconDamageInflictsRef,
                entriesRef
            ) = createRefs()

            Text(
                spell.name,
                Modifier.fillMaxWidth()
                    .padding(8.dp)
                    .constrainAs(name) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                fontSize = 22.sp
            )
            Text(
                "Level ${spell.level}",
                Modifier.wrapContentSize()
                    .padding(8.dp, 4.dp, 0.dp, 8.dp)
                    .constrainAs(level) {
                        top.linkTo(name.bottom)
                        start.linkTo(parent.start)
                    },
                fontSize = 16.sp
            )
            Text(
                spell.getSchool(),
                Modifier.wrapContentSize()
                    .padding(4.dp, 4.dp, 0.dp, 8.dp)
                    .constrainAs(school) {
                        bottom.linkTo(level.bottom)
                        start.linkTo(level.end)
                    },
                fontSize = 16.sp
            )
            val itemClasses = spell.classes
            val classesText = itemClasses.take(2).toMutableList()

            if (spell.classes.size > 2) {
                classesText += "..."
            }
            val classesString = classesText.toString()

            Text(
                classesString.substring(1, classesString.length - 1),
                Modifier.wrapContentSize()
                    .padding(0.dp, 4.dp, 4.dp, 8.dp)
                    .constrainAs(classesRef) {
                        bottom.linkTo(level.bottom)
                        end.linkTo(parent.end)
                    },
                fontSize = 16.sp,
                overflow = TextOverflow.Clip,
                maxLines = 2,
                softWrap = true
            )

            val damageInflictsList = spell.damageInflicts
            if (damageInflictsList.isNotEmpty()) {
                val iconDamageInflict =
                    vectorResource(id = damageInflictsList[0].toVectorResource())
                Icon(
                    imageVector = iconDamageInflict,
                    modifier = Modifier.wrapContentSize()
                    .padding(4.dp)
                    .constrainAs(iconDamageInflictsRef) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                    tint = Color.Unspecified
                )
            }
            val conditionInflictsList = spell.conditionInflicts
            if (conditionInflictsList.isNotEmpty()) {
                val iconConditionInflict =
                    vectorResource(id = conditionInflictsList[0].toVectorResource())
                Icon(
                    imageVector = iconConditionInflict,
                    Modifier.wrapContentSize()
                        .padding(4.dp)
                        .constrainAs(iconConditionInflictsRef) {
                            top.linkTo(parent.top)

                            if (damageInflictsList.isEmpty()) {
                                end.linkTo(parent.end)
                            } else {
                                end.linkTo(iconDamageInflictsRef.start)
                            }
                        },
                    tint = Color.Unspecified
                )
            }


            val enter =
                remember { expandVertically(animSpec = TweenSpec(200, easing = FastOutLinearInEasing)) }
            val exit = remember { shrinkVertically(animSpec = TweenSpec(200, easing = FastOutSlowInEasing)) }
            Box(
                Modifier
                    .constrainAs(entriesRef) {
                        top.linkTo(level.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                AnimatedVisibility(
                    visible = expanded,
                    initiallyVisible = expanded,
                    enter = enter,
                    exit = exit,
                ) {
                    if (expanded) {
                        SpellDetails(spell = spell)
                    }
                }
            }
        }
    }
}

@Composable
fun SpellDetails(spell: SpellInMemory) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            spell.entries,
            Modifier.fillMaxWidth()
                .padding(4.dp, 4.dp, 4.dp, 4.dp),
            fontSize = 16.sp,
            softWrap = true
        )
        val entriesHigherLevel = spell.entriesHigherLevels

        entriesHigherLevel?.let {
            Text(
                it,
                Modifier.fillMaxWidth()
                    .padding(4.dp, 4.dp, 4.dp, 4.dp),
                fontSize = 16.sp,
                softWrap = true
            )
        }
    }

}


@ExperimentalAnimationApi
@Composable
fun SpellsList(
    spellsState: State<DataState<List<SpellInMemory>>>,
    spellsExpandedState: State<DataState<List<Boolean>>>,
    modifier: Modifier = Modifier,
    expandToggle: (Int) -> Unit
    ) {
    when (val spellsDataStateValue = spellsState.value) {
        is DataState.Success -> {
            val spellsExpandedStateValue = spellsExpandedState.value
            if (spellsExpandedStateValue is DataState.Success) {
                val expandedList = spellsExpandedStateValue.data
                LazyColumnForIndexed(items = spellsDataStateValue.data, modifier) { ndx, item ->
                        SpellCard(spell = item, expandedList[ndx], ndx) {
                            expandToggle(ndx)
                        }
                }
            }
        }
    }
/*        is DataState.Error -> TODO()
        DataState.Empty -> TODO()
        DataState.Loading -> TODO()*/
}


@Composable
fun SpellsProgressIndicator(progress: Float, modifier: Modifier = Modifier) {
    val animatedProgress = animate(
        target = progress,
        animSpec = ProgressIndicatorConstants.DefaultProgressAnimationSpec
    )
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    )

    Text(
        "Initializing Database...",
        modifier = modifier
            .padding(8.dp)
    )
}

@Preview
@Composable
fun PreviewSpellsProgressIndicator() =
    SpellsProgressIndicator(progress = 0.40F)


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FiveESpellsTheme {
        Greeting("Android")
    }
}