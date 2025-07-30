package com.ardrawing.sketch.anime.drawing.sharePreferent;

import android.content.Context


object SharePrefUtilsKotlin {
    fun isTutorial(context: Context): Boolean {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return pre.getBoolean("Tutorial", true)
    }

    fun forceTutorial(context: Context,ischeck:Boolean) {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = pre.edit()
        editor.putBoolean("Tutorial", ischeck)
        editor.apply()
    }
}