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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()

    @ExperimentalAnimationApi
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val progress by viewModel.dbPopulateProgress.observeAsState()

            FiveESpellsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column(modifier = Modifier.fillMaxSize()) {

                        progress?.let {
                            if (it < 1.0F) {
                                SpellsProgressIndicator(
                                    progress = it,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            } else {
                                val spells by viewModel.spellStateFlow.collectAsState()

                                SpellsList(
                                    spells = spells,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun SpellCard(spell: SpellInMemory) {
    val opened = remember { mutableStateOf(false) }

    Card(
        elevation = 8.dp,
        modifier = Modifier.padding(6.dp)
            .clickable(onClick = { opened.value = !opened.value })
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
            val classesText = itemClasses.take(3).toMutableList()

            if (spell.classes.size > 3) {
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
                    asset = iconDamageInflict,
                    Modifier.wrapContentSize()
                        .padding(4.dp)
                        .constrainAs(iconDamageInflictsRef) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        },
                    Color.Unspecified
                )
            }
            val conditionInflictsList = spell.conditionInflicts
            if (conditionInflictsList.isNotEmpty()) {
                val iconConditionInflict =
                    vectorResource(id = conditionInflictsList[0].toVectorResource())

                Icon(
                    asset = iconConditionInflict,
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
                    Color.Unspecified
                )
            }
 /*           val enter =
                remember { fadeIn(animSpec = TweenSpec(300, easing = FastOutLinearInEasing)) }
            val exit = remember { fadeOut(animSpec = TweenSpec(100, easing = FastOutSlowInEasing)) }*/
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
                    visible = opened.value,
                    initiallyVisible = false,
                    enter = enter,
                    exit = exit,
                ) {
                    SpellDetails(spell = spell)
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
    spells: DataState<List<SpellInMemory>>,
    modifier: Modifier = Modifier
) {
    when (spells) {
        is DataState.Success -> {
            LazyColumnForIndexed(items = spells.data, modifier) { index, item ->
                SpellCard(spell = item)
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