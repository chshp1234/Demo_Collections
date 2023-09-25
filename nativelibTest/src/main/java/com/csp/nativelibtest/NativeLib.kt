package com.csp.nativelibtest

object NativeLib {

    // Used to load the 'nativelibtest' library on application startup.
    init {
        System.loadLibrary("nativelibtest")
    }


    /**
     * A native method that is implemented by the 'nativelibtest' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
    external fun stringFromJNI(msg: String): String
}

external fun stringFromJNI(): String