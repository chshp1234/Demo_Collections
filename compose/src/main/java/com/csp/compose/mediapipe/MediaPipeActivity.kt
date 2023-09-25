package com.csp.compose.mediapipe

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
import com.csp.compose.ui.theme.MyNewTheme

class MediaPipeActivity : ComponentActivity() {
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
fun MediaPipeList(modifier: Modifier = Modifier, itemList: Array<MediaItem>) {
    val context = LocalContext.current as Activity

    LazyColumn(modifier) {
        items(itemList) {
            Text(
                text = it.name,
                modifier = Modifier
                    .clickable {
                        context.startActivity(Intent(context, it.targetClass))
                    }
                    .defaultMinSize(minHeight = 45.dp)
                    .padding(10.dp)

            )
        }
    }
}

data class MediaItem(val name: String, val targetClass: Class<*>)

val itemList = arrayOf(
    MediaItem(
        "audio_classifier",
        com.google.mediapipe.examples.audioclassifier.MainActivity::class.java
    ),
    MediaItem(
        "face_detector",
        com.google.mediapipe.examples.facedetection.MainActivity::class.java
    ),
    MediaItem(
        "face_landmarker",
        com.google.mediapipe.examples.facelandmarker.MainActivity::class.java
    ),
    MediaItem(
        "gesture_recognizer",
        com.google.mediapipe.examples.gesturerecognizer.MainActivity::class.java
    ),
    MediaItem(
        "hand_landmarker",
        com.google.mediapipe.examples.handlandmarker.MainActivity::class.java
    ),
    MediaItem(
        "image_classification",
        com.google.mediapipe.examples.imageclassification.MainActivity::class.java
    ),
    MediaItem(
        "image_embedder",
        com.google.mediapipe.examples.imageembedder.MainActivity::class.java
    ),
    MediaItem(
        "image_segmentation",
        com.google.mediapipe.examples.imagesegmenter.MainActivity::class.java
    ),
    MediaItem(
        "interactive_segmentation",
        com.mediapipe.example.interactivesegmentation.MainActivity::class.java
    ),
    MediaItem(
        "language_detector",
        com.google.mediapipe.examples.languagedetector.MainActivity::class.java
    ),
    MediaItem(
        "object_detection",
        com.google.mediapipe.examples.objectdetection.MainActivity::class.java
    ),
    MediaItem(
        "pose_landmarker",
        com.google.mediapipe.examples.poselandmarker.MainActivity::class.java
    ),
    MediaItem(
        "text_classification",
        com.google.mediapipe.examples.textclassifier.MainActivity::class.java
    ),
    MediaItem(
        "text_embedder",
        com.google.mediapipe.examples.textembedder.MainActivity::class.java
    ),
)