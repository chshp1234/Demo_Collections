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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    val context = LocalContext.current as Activity

    LazyColumn(modifier) {
        items(itemList) {
            Column {
                Text(
                    text = it.name,
                    modifier = Modifier
                        .clickable {

                            /**
                             * 调用第三方浏览器打开
                             * @param context
                             * @param url 要浏览的资源地址
                             */
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW;
                            intent.data = Uri.parse(it.url)
                            // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
                            // 官方解释 : Name of the component implementing an activity that can display the intent
                            if (intent.resolveActivity(context.packageManager) != null) {
                                val componentName =
                                    intent.resolveActivity(context.packageManager); // 打印Log   ComponentName到底是什么 L.d("componentName = " + componentName.getClassName());
                                context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
                            } else {
                                Toast
                                    .makeText(
                                        context.applicationContext,
                                        "请下载浏览器",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 45.dp)
                        .padding(10.dp)
                )
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