package marcus.GlassMorphism

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


@Composable
fun GlassmorphicRow(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    propagateMinConstraints: Boolean = false,
    scrollState: ScrollState,
    childMeasures: ImmutableList<Place>,
    targetBitmap: ImageBitmap,
    isAlreadyBlurred: Boolean = false,// providing already blurred bitmap consumes less resources
    dividerSpace: Int = 10,
    blurRadius: Int = 100,
    childCornerRadius: Int = 10,
    drawOnTop: DrawScope.(Path) -> Unit = {},
    content: @Composable @ExtensionFunctionType() (RowScope.() -> Unit),
) {

    if (childMeasures.isEmpty()) return
    val blurredBg = remember {
        if (isAlreadyBlurred) {
            targetBitmap
        } else {
            fastblur(targetBitmap.asAndroidBitmap(), 1f, blurRadius)?.asImageBitmap()
        }
    }


    var containerMeasures by remember { mutableStateOf(Place()) }
    val calculatedHeight = containerMeasures.sizeX.dp.let { parentDp ->
        containerMeasures.offsetY.toInt().dp.let { childDp ->
            parentDp + childDp
        }
    }


    Canvas(
        modifier = modifier
            .horizontalScroll(scrollState)
            .width(containerMeasures.sizeX.dp)
            .height(calculatedHeight)
    ) {
        for (i in childMeasures.indices) {
            val path = Path()

            path.addRoundRect(
                RoundRect(
                    Rect(
                        offset = Offset(childMeasures[i].offsetX, childMeasures[i].offsetY),
                        size = Size(
                            childMeasures[i].sizeX.toFloat(),
                            childMeasures[i].sizeY.toFloat()
                        ),
                    ),
                    CornerRadius(childCornerRadius.dp.toPx())
                )
            )

            clipPath(path) {
                if (blurredBg != null) {
                    drawImage(
                        blurredBg,
                        Offset(
                            scrollState.value.toFloat() - containerMeasures.offsetX,
                            -containerMeasures.offsetY
                        )
                    )
                }
            }
            drawOnTop(path)
        }

    }

    Box(modifier = modifier
        .fillMaxSize()
        .clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() }) {
        })
    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .onGloballyPositioned {
                if (containerMeasures.sizeX == 0 && containerMeasures.sizeY == 0) {
                    containerMeasures = Place(
                        it.size.width,
                        it.size.height,
                        it.positionInParent().x,
                        it.positionInParent().y
                    )
                }
            },
        horizontalArrangement = Arrangement.spacedBy(dividerSpace.dp),
    ) {
        content()
    }

}


