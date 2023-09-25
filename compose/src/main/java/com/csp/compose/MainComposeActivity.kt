package com.csp.compose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.blankj.utilcode.util.LogUtils
import com.csp.compose.ui.theme.MyNewTheme

class MainComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyNewTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CustomList()
//                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
@Deprecated("Example with bug")
fun ListWithBug(myList: List<String>) {
    var items = 0

    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            for (item in myList) {
                Text("Item: $item")
                items++ // Avoid! Side-effect of the column recomposing.
            }

        }

        Text("Count: $items")
    }
}

@Composable
fun CustomList() {
    LazyColumn {
        items(50) {
            Greeting(name = "csp", 1)
        }
    }
}

@Composable
fun Greeting(name: String, other: Int) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    /* var foreground= remember {
         mutableStateOf(painterResource(id = R.drawable.ic_launcher_foreground))

     }*/


    val resource = LocalContext.current.resources
    val background by remember {
        mutableStateOf(
            ResourcesCompat.getDrawable(resource, R.drawable.ic_launcher_background, null)!!
                .toBitmap().asImageBitmap()
        )
    }

    Row(
        Modifier
            .clickable { isExpanded = !isExpanded }
            .fillMaxWidth()) {
        /*Box(
            Modifier
                .size(50.dp)
                .background(Color.Blue)
                .padding(5.dp)
                .background(Color.Cyan)
                .padding(5.dp)
                .background(Color.Red)
        )*/

        Image(

            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colors.secondary, CircleShape)
                .drawBehind {
                    drawImage(background)
                }
                /*.drawWithCache {
                    this.onDrawBehind {
                        drawImage(background)
                    }
                }*/
                .size(if (isExpanded) 100.dp else 50.dp)
//                .paint(painterResource(id = R.drawable.ic_launcher_background))
                .animateContentSize()
        )

        Spacer(modifier = Modifier.width(0.dp))

        Column(Modifier.padding(10.dp)) {
            Text(text = "Hello $name!", style = MaterialTheme.typography.subtitle2)

            Spacer(modifier = Modifier.width(5.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 2.dp,
            ) {
                Text(
                    text = "World $name!",
                    color = MaterialTheme.colors.secondaryVariant,
                    fontSize = 13.sp
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DefaultPreview() {
    MyNewTheme {
        Greeting("Android", 1)
    }
}