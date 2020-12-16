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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.bradyaiello.fiveespells.models.*
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
                        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
                        Scaffold(
                                scaffoldState = scaffoldState,
                                topBar = {
                                    TopAppBar(
                                            title = { Text("5e Spells") },
                                            navigationIcon = {
                                                Icon(
                                                        Icons.Default.Menu,
                                                        modifier = Modifier.clickable(onClick = {
                                                            scaffoldState.drawerState.open()
                                                        })
                                                )
                                            }
                                    )
                                },
                                drawerContent = {
                                    Column(modifier = Modifier.wrapContentSize()) {
                                        Text(text = "Filters", fontSize = 24.sp,
                                                modifier = Modifier.padding(8.dp))
                                        var classSelected by remember { mutableStateOf("Any") }
                                        var backgroundSelected by remember { mutableStateOf("Any") }

                                        var lowestLevelsList by remember { mutableStateOf(levels) }
                                        var highestLevelsList by remember { mutableStateOf(levels) }

                                        var lowestLevelSelected by remember { mutableStateOf("0") }
                                        var highestLevelSelected by remember { mutableStateOf("10") }

                                        FilterDropDownMenu(
                                                title = "Class",
                                                menuItems = classes,
                                                selectedItem = classSelected,
                                                onFilterItemChanged = {
                                                    classSelected = it
                                                    var classSelectedArg = classSelected
                                                    if (classSelected == "Any") {
                                                        classSelectedArg = ""
                                                    }
                                                    viewModel.setStateEvent(
                                                            StateEvent.FilterSpells(
                                                                    classSelectedArg,
                                                                    lowestLevelSelected.toInt(),
                                                                    highestLevelSelected.toInt()
                                                            )
                                                    )
                                                })

                                        FilterDropDownMenu(
                                                title = "Background",
                                                menuItems = backgrounds,
                                                selectedItem = backgroundSelected,
                                                onFilterItemChanged = {
                                                    backgroundSelected = it
                                                })

                                        FilterDropDownMenu(
                                                title = "Lowest Level",
                                                menuItems = lowestLevelsList,
                                                selectedItem = lowestLevelSelected,
                                                onFilterItemChanged = { lowestLevel ->
                                                    lowestLevelSelected = lowestLevel
                                                    val lowestLevelInt = lowestLevel.toInt()

                                                    highestLevelsList = (lowestLevelInt..10)
                                                            .toList().map { it.toString() }
                                                    var classSelectedArg = classSelected
                                                    if (classSelected == "Any") {
                                                        classSelectedArg = ""
                                                    }
                                                    viewModel.setStateEvent(
                                                            StateEvent.FilterSpells(
                                                                    classSelectedArg,
                                                                    lowestLevelSelected.toInt(),
                                                                    highestLevelSelected.toInt()
                                                            )
                                                    )
                                                })

                                        FilterDropDownMenu(
                                                title = "Highest Level",
                                                menuItems = highestLevelsList,
                                                selectedItem = highestLevelSelected,
                                                onFilterItemChanged = { highestLevel ->
                                                    highestLevelSelected = highestLevel
                                                    val highestLevelInt = highestLevelSelected.toInt()

                                                    lowestLevelsList = (0..highestLevelInt)
                                                            .toList()
                                                            .map{ it.toString() }
                                                    var classSelectedArg = classSelected
                                                    if (classSelected == "Any") {
                                                        classSelectedArg = ""
                                                    }
                                                    viewModel.setStateEvent(
                                                            StateEvent.FilterSpells(
                                                                    classSelectedArg,
                                                                    lowestLevelSelected.toInt(),
                                                                    highestLevelSelected.toInt()
                                                            )
                                                    )
                                                })
                                    }
                                },
                                bodyContent = {
                                    Column(modifier = Modifier.fillMaxSize()) {

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
                                                expandToggle = viewModel::expandToggle
                                        )
                                    }
                                }//,
                                //bottomBar = { BottomAppBar { Text("BottomAppBar") } }
                        )

                }
            }
        }
    }
}

@Composable fun FilterDropDownMenu(title: String,
                                   selectedItem: String,
                                   menuItems: List<String>,
                                   onFilterItemChanged: (String)-> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
//    var selectedIndex by remember { mutableStateOf(0) }
    Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                    .padding(12.dp, 4.dp)
                    .clickable(onClick = { showMenu = true })
    ) {
        Text(text = title, fontSize = 18.sp)
        DropdownMenu(toggle = {
            Text(text = selectedItem,
                    textAlign = TextAlign.End,
                    fontSize = 18.sp,
                    modifier = Modifier.wrapContentSize()
                            .padding(8.dp))

        },
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
        ) {
            menuItems.forEach { className ->
                DropdownMenuItem(onClick = {
                    showMenu = false
                    onFilterItemChanged(className)
                }) {
                    Text(text = className, fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth())

                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun SpellCard(spell: SpellInMemory,
              expanded: Boolean = false,
              ndx: Int, expandToggle: (Int) -> Unit) { // onClickExpander: () -> Unit) {
    val topModifier = Modifier.padding(6.dp)
    val defaultPadding = Modifier.padding(6.dp, 0.dp, 6.dp, 6.dp)
    val paddingMod = if (ndx == 0) topModifier else defaultPadding
    Card(
        elevation = 8.dp,
        modifier = paddingMod
        .clickable(onClick =  {
            expandToggle(ndx)
        } )
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

/*
Row(horizontalArrangement = Arrangement.SpaceBetween,
modifier = Modifier.fillMaxWidth()
.padding(8.dp)) {
    Text(text = "Levels")
    Checkbox(
            checked = false,
            onCheckedChange = { },
    )
}*/
