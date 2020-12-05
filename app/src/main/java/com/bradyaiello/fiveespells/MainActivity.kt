package com.bradyaiello.fiveespells

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animate
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.bradyaiello.fiveespells.ui.FiveESpellsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()

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
                                    modifier = Modifier.fillMaxSize(),
                                    this@MainActivity,
                                    theme
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpellsList(
    spells: DataState<List<SpellInMemory>>,
    modifier: Modifier = Modifier,
    context: Context,
    theme: Resources.Theme
) {
    when (spells) {
        is DataState.Success -> {
            LazyColumnForIndexed(items = spells.data, modifier) { index, item ->
                Card(elevation = 8.dp, modifier = Modifier.padding(6.dp)) {
                    ConstraintLayout(modifier = Modifier.padding(6.dp)) {
                        val (
                            name,
                            level,
                            school,
                            classesRef,
                            iconConditionInflictsRef,
                            iconDamageInflictsRef
                        ) = createRefs()

                        Text(
                            item.name,
                            Modifier.fillMaxWidth()
                                .padding(8.dp)
                                .constrainAs(name) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                },
                            fontSize = 22.sp
                        )
                        Text(
                            "Level ${item.level}",
                            Modifier.wrapContentSize()
                                .padding(8.dp, 4.dp, 0.dp, 8.dp)
                                .constrainAs(level) {
                                    top.linkTo(name.bottom)
                                    start.linkTo(parent.start)
                                },
                            fontSize = 16.sp
                        )
                        Text(
                            item.getSchool(),
                            Modifier.wrapContentSize()
                                .padding(4.dp, 4.dp, 0.dp, 8.dp)
                                .constrainAs(school) {
                                    bottom.linkTo(level.bottom)
                                    start.linkTo(level.end)
                                },
                            fontSize = 16.sp
                        )
                        Text(
                            item.classes,
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
                        val iconConditionInflict = vectorResource(id = R.drawable.charmed)
                        val iconDamageInflict = vectorResource(id = R.drawable.blinded)
                        Icon(
                            asset = iconDamageInflict,
                            Modifier.wrapContentSize()
                                .padding(4.dp)
                                .constrainAs(iconDamageInflictsRef) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                },
                            Color.Blue
                        )
                        Icon(
                            asset = iconConditionInflict,
                            Modifier.wrapContentSize()
                                .padding(4.dp)
                                .constrainAs(iconConditionInflictsRef) {
                                    top.linkTo(parent.top)
                                    end.linkTo(iconDamageInflictsRef.start)
                                },
                            Color.Unspecified
                        )
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