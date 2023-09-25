package com.csp.compose.qalist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csp.compose.ui.theme.MyNewTheme
import kotlinx.parcelize.Parcelize

class QAListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyNewTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val list = remember {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableArrayExtra("qalist", QAItem::class.java)
                                ?: arrayOf()
                        } else {
                            arrayOf()
                        }
                    }
                    QAList(itemList = list)
                }
            }
        }
    }
}

@Composable
fun QAList(modifier: Modifier = Modifier, itemList: Array<QAItem>) {
    LazyColumn(modifier) {
        items(itemList) {
            Column {
                val annotatedLinkString = remember {
                    buildAnnotatedString {
                        append(it.name)
                        addStyle(
                            style = SpanStyle(
                                color = Color(0xff64B5F6),
                                fontSize = 16.sp
                            ), start = 0, end = it.name.length
                        )

                        // attach a string annotation that stores a URL to the text "link"
                        addStringAnnotation(
                            tag = "URL",
                            annotation = it.url,
                            start = 0,
                            end = it.name.length
                        )
                    }
                }

                //  Clickable text returns position of text that is clicked in onClick callback
                val handler = LocalUriHandler.current
                ClickableText(
                    text = annotatedLinkString,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 45.dp)
                        .padding(10.dp)
                ) {
                    annotatedLinkString
                        .getStringAnnotations("URL", it, it)
                        .firstOrNull()?.let { stringAnnotation ->
                            // UriHandler parse and opens URI inside AnnotatedString Item in Browse
                            handler.openUri(stringAnnotation.item)
                        }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                ) {
                    drawLine(Color.LightGray, Offset.Zero, Offset(size.width, 0F), 0.5.dp.toPx())
                }
            }

        }
    }
}

@Parcelize
data class QAItem(val name: String, val url: String) : Parcelable