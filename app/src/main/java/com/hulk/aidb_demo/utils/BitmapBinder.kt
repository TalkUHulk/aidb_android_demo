package com.hulk.aidb_demo.utils

import android.graphics.Bitmap

import android.os.Binder


class BitmapBinder internal constructor(val bitmap: Bitmap) : Binder()