package com.hulk.aidb

class NativeLib {

    /**
     * A native method that is implemented by the 'aidb' native library,
     * which is packaged with this application.
     */
//    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'aidb' library on application startup.
        init {
            System.loadLibrary("aidb")
        }
    }
}