package com.bradyaiello.fiveespells

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
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.bradyaiello.fiveespells.models.SpellInMemory
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
                                val spells by viewModel.spellStateFlow.collectAsState(initial = DataState.Loading)

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

@Composable
fun SpellsList(spells: DataState<List<SpellInMemory>>, modifier: Modifier = Modifier) {
    when (spells) {
        is DataState.Success -> {
            LazyColumnForIndexed(items = spells.data, modifier) { index, item ->
                Card(elevation = 8.dp, modifier = Modifier.padding(6.dp)) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(item.name, modifier.fillMaxWidth().padding(8.dp), fontSize = 22.sp)
                        Text(
                            "Level ${item.level}",
                            modifier.fillMaxWidth().padding(8.dp),
                            fontSize = 16.sp
                        )
                    }

                }


            }
        }
/*        is DataState.Error -> TODO()
        DataState.Empty -> TODO()
        DataState.Loading -> TODO()*/
    }

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

    Text("Initializing Database...",
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