package com.bradyaiello.fiveespells

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animate
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bradyaiello.fiveespells.ui.FiveESpellsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*        viewModel.dbPopulateProgress.observe(this){ progress ->
            Log.d(TAG, "onCreate: progress $progress")
        }*/
        setContent {
            val progress by viewModel.dbPopulateProgress.observeAsState()

            FiveESpellsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column(modifier = Modifier.fillMaxSize()) {

                        progress?.let {
                            val animatedProgress = animate(
                                target = it,
                                animSpec = ProgressIndicatorConstants.DefaultProgressAnimationSpec
                            )
                            if (it < 1.0F) {
                                LinearProgressIndicator(
                                    progress = animatedProgress,
                                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                )

                                Text("Initializing Database...",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                        .padding(8.dp)
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