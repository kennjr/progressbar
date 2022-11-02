package com.ramanie.progressbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet

@Composable
fun CircularProgressbar( percentage: Float = 0f,
                         // the total that the %age is derived from
                         number: Int,
                         // size of the text in the circle
                         fontSize: TextUnit = 15.sp,
                         // radius of the circle
                         radius: Dp = 20.dp,
                         // the thickness of the circular bar
                         strokeWidth: Dp = 5.dp,
                         // how long should the animation run
                         animDuration: Int = 1000,
                         // how long b4 the animation begins
                         animDelay: Int = 0,
                         // color of the circular bar
                         color: Color = Color.Red
                         ){
    // this is what we'll use to know whether the animation has played,
    // we'll use the bool to start the animation for the first composition,
    // NOTE : for the 1st composition we'll set the bool's val to true
    var animationPlayed by remember {
        mutableStateOf(false)
    }

    // animateFloatAsState() will animate 0f towards @param percentage, using the animDuration and animDelay,
    // then assign that val. to the currentPercentage variable which we can use to display the animation
    var currentPercentage = animateFloatAsState(targetValue = if (animationPlayed) percentage else 0f,
        // we use the animationSpec to pass the curation and delay for the animation
        animationSpec = tween(animDuration, animDelay)
        )

    // since we wanna trigger the animation on initial compose, we're not gon set the animationPlayed var.
    // to true directly inside the composable bc then it'll be set to true every time the composable fun is recomposed
    // so we'll set it to true inside a LaunchedEffect handler
    // NOTE : the key1 = true simply states that the composable fun. has been launched
    LaunchedEffect(key1 = true, block = {
        animationPlayed = true
    })

    
    val circleConstraints = remember {
        ConstraintSet{
            val circle = createRefFor(id = "circle")
            val progressval = createRefFor(id = "progressval")

            constrain(circle) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
            
            constrain(progressval){
                top.linkTo(circle.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            createVerticalChain(circle, progressval, chainStyle = ChainStyle.Spread)
        }
    }
    ConstraintLayout(circleConstraints) {
        Canvas( modifier = Modifier
            .size(radius.times(2f))
            .layoutId("circle"), onDraw = {
            drawArc(color = color,
                startAngle = -90f,
                // the sweepAngle tells the canvas what fraction(out of 360) should the arc cover
                sweepAngle = 360.times(currentPercentage.value),
                // we set the val of useCenter to false so that the arc isn't connected to the centre, forming a pie
                useCenter = false,
                style = Stroke(strokeWidth.toPx(),
                    // the cap refers to how the ends of the ring will look
                    cap = StrokeCap.Round)
            )
        })
        Spacer(modifier = Modifier.fillMaxHeight(0.5f))
        Text(modifier = Modifier.layoutId("progressval"),
            text = (currentPercentage.value.times(number).toInt().toString()),
            color = Color.DarkGray)
    }

}