package com.csp.compose.ndk

import com.example.native_plasma.NativePlasmaActivity
import com.example.native_activity.NativeSampleActivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.accelerometergraph.AccelerometerGraphActivity
import com.android.gl2jni.GL2JNIActivity
import com.android.gles3jni.GLES3JNIActivity
import com.csp.compose.ui.theme.MyNewTheme
import com.example.SanAngeles.DemoActivity
import com.example.helloneon.HelloNeon
import com.example.nativeaudio.NativeAudio
import com.example.nativecodec.NativeCodec
import com.example.nativemedia.NativeMedia
import com.example.plasma.Plasma
import com.example.webp_view.NativeWebpActivity
import com.example.widecolor.WideColorActivity
import com.google.sample.tunnel.EndlessTunnelActivity
import com.sample.camera.basic.CameraActivity
import com.sample.choreographer.ChoreographerNativeActivity
import com.sample.textureview.ViewActivity

class NdkSampleActivity : ComponentActivity() {
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
                        itemList
                    }
                    MediaPipeList(itemList = list)
                }
            }
        }
    }
}

@Composable
fun MediaPipeList(modifier: Modifier = Modifier, itemList: Array<NdkItem>) {
    val context = LocalContext.current as Activity

    LazyColumn(modifier) {
        items(itemList) {
            Text(
                text = it.name,
                modifier = Modifier
                    .clickable {
                        context.startActivity(Intent(context, it.targetClass))
                    }
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 45.dp)
                    .padding(10.dp)

            )
        }
    }
}

data class NdkItem(val name: String, val targetClass: Class<*>)

val itemList = arrayOf(
    NdkItem(
        "audio-echo",
        com.google.sample.echo.MainActivity::class.java
    ),
    NdkItem(
        "bitmap-plasma",
        Plasma::class.java
    ),
    NdkItem(
        "camera-basic",
        CameraActivity::class.java
    ),
    NdkItem(
        "camera-basic-texture-view",
        ViewActivity::class.java
    ),
    NdkItem(
        "display-v3-image-view",
        WideColorActivity::class.java
    ),
    NdkItem(
        "endless-tunnel",
        EndlessTunnelActivity::class.java
    ),
    NdkItem(
        "exceptions",
        com.example.exceptions.MainActivity::class.java
    ),
    NdkItem(
        "gles3jni",
        GLES3JNIActivity::class.java
    ),
    NdkItem(
        "hello-gl2",
        GL2JNIActivity::class.java
    ),
//    NdkItem(
//        "hello-jni",
//        HelloJni::class.java
//    ),
    NdkItem(
        "hello-jniCallback",
        com.example.hellojnicallback.MainActivity::class.java
    ),
    NdkItem(
        "hello-libs",
        com.example.hellolibs.MainActivity::class.java
    ),
    NdkItem(
        "hello-neon",
        HelloNeon::class.java
    ),
//    NdkItem(
//        "hello-oboe",
//        com.google.example.hellooboe.MainActivity::class.java
//    ),
//    NdkItem(
//        "hello-vulkan",
//        VulkanActivity::class.java
//    ),
    NdkItem(
        "native-activity",
        NativeSampleActivity::class.java
    ),
    NdkItem(
        "native-audio",
        NativeAudio::class.java
    ),
    NdkItem(
        "native-codec",
        NativeCodec::class.java
    ),
    NdkItem(
        "native-media",
        NativeMedia::class.java
    ),
    NdkItem(
        "native-midi",
        com.example.nativemidi.MainActivity::class.java
    ),
    NdkItem(
        "native-plasma",
        NativePlasmaActivity::class.java
    ),
    NdkItem(
        "nn-samples",
        com.example.android.sequence.MainActivity::class.java
    ),
    NdkItem(
        "orderfile",
        com.example.orderfiledemo.MainActivity::class.java
    ),
//    NdkItem(
//        "prefab-curl-ssl",
//        com.example.curlssl.MainActivity::class.java
//    ),
//    NdkItem(
//        "prefab-dependency",
//        com.example.prefabdependency.MainActivity::class.java
//    ),
    NdkItem(
        "san-angeles",
        DemoActivity::class.java
    ),
    NdkItem(
        "sanitizers",
        com.example.sanitizers.MainActivity::class.java
    ),
    NdkItem(
        "sensor-graph-accelerometer",
        AccelerometerGraphActivity::class.java
    ),
    NdkItem(
        "teapots-choreographer-30fps",
        ChoreographerNativeActivity::class.java
    ),
    NdkItem(
        "teapots-classic-teapot",
        com.sample.teapot.TeapotNativeActivity::class.java
    ),
    NdkItem(
        "teapots-image-decoder",
        com.sample.imagedecoder.TeapotNativeActivity::class.java
    ),
    NdkItem(
        "teapots-more-teapots",
        com.sample.moreteapots.MoreTeapotsNativeActivity::class.java
    ),
    NdkItem(
        "teapots-textured-teapot",
        com.sample.texturedteapot.TeapotNativeActivity::class.java
    ),
    NdkItem(
        "unit-test",
        com.example.unittest.MainActivity::class.java
    ),
    NdkItem(
        "webp",
        NativeWebpActivity::class.java
    ),

)