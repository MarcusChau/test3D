package com.mygdx.game

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.mygdx.game.ui.theme.GlassMorphicDesignTheme
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import marcus.GlassMorphism.GlassmorphicColumn
import marcus.GlassMorphism.Place
import marcus.GlassMorphism.fastblur


const val BLURRED_BG_KEY = "BLURRED_BG_KEY"
const val BLUR_RADIUS = 50


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var temp = AndroidApplication()
            temp.onCreate(savedInstanceState)
            val config = AndroidApplicationConfiguration()
            temp.initialize(LibGDX3DTest(), config)
//            GlassMorphicDesignTheme{
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
////                    TestThree()
//                    Testingbackground()
//                }
//            }
        }
    }
}

@Composable
fun TestThree() {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val cardWidthDp = screenWidthDp/2


    var capturedBitmap by remember {
        mutableStateOf<Bitmap?>(App.getInstance().memoryCache[BLURRED_BG_KEY])
    }

    val scrollState = rememberScrollState()
    val items = arrayListOf<Int>()

    for (i in 0 until 5) {
        items.add(
            i
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val captureController = rememberCaptureController()
        Capturable(
            controller = captureController,
            onCaptured = { bitmap, _ ->
                // This is captured bitmap of a content inside Capturable Composable.
                bitmap?.let {
                    fastblur(it.asAndroidBitmap(), 1f, BLUR_RADIUS)?.let { fastBlurred ->
                        // Bitmap is captured successfully. Do something with it!
                        App.getInstance().memoryCache.put(BLURRED_BG_KEY, fastBlurred)
                        capturedBitmap = fastBlurred
                    }
                }
            }

        ) {
//            LibGDX3DTest()
            Testingbackground()
        }


        LaunchedEffect(key1 = true, block = {
            withContext(Main) {
                if (capturedBitmap == null) captureController.capture()
            }
        })


        val childMeasures = remember { items.map { Place() }.toImmutableList() }

        capturedBitmap?.let { capturedImage ->
            GlassmorphicColumn(
                modifier = Modifier
                    .padding(start = 0.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
                scrollState = scrollState,
                childMeasures = childMeasures,
                targetBitmap = capturedImage.asImageBitmap(),
                dividerSpace = 10,
                blurRadius = BLUR_RADIUS,
                drawOnTop = { path ->
                    val strokeColor = Color(0x80ffffff)
                    val transparent = Color.Transparent
                    drawPath(
                        path = path,
                        color = strokeColor,
                        style = Stroke(1f),
                    )
                    drawPath(
                        path = path,

                        brush = Brush.verticalGradient(listOf(strokeColor, transparent)),
                        blendMode = BlendMode.Overlay,
//                        blendMode = BlendMode.Plus,
//                        blendMode = BlendMode.Screen
//                        blendMode = BlendMode.Luminosity
                    )
                },
                content = {
                    items.forEachIndexed { index, it ->
                        Box(
                            modifier = Modifier
//                                .background(Color(0x80FF0000))
                                .onGloballyPositioned {
                                    childMeasures[index].apply {
                                        sizeX = it.size.width
                                        sizeY = it.size.height
                                        offsetX = it.positionInParent().x
                                        offsetY = it.positionInParent().y
                                    }
                                }
                                .width(300.dp)
                                .height(100.dp)
                                .padding(10.dp)
                        ) {
                            Text(
                                "Join this!",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                },
            )
        }
    }
}

@Composable
fun Testingbackground() {
//    GlassMorphicDesignTheme {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight()
//                .background(color = Color(0xFFEAE4D9)),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Helllo this is thelksflk",
//                fontSize = 30.sp,
////                horizontalAlignment = Alignment.CenterHorizontally,
//                fontWeight = FontWeight.Bold
//            )
//            Image(
//                painter = painterResource(id = R.drawable.bubble),
//                contentDescription = "Background Image",
//                modifier = Modifier.size(150.dp)
//            )
//        }
//    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GlassMorphicDesignTheme {
        TestThree()
    }
}