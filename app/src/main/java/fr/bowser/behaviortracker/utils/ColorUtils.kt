package fr.bowser.behaviortracker.utils

import android.content.Context
import android.support.annotation.IntDef
import android.support.v4.content.ContextCompat


import fr.bowser.behaviortracker.R


object ColorUtils {

    const val COLOR_DEEP_ORANGE = 0
    const val COLOR_BROWN = 1
    const val COLOR_GREY = 2
    const val COLOR_BLUE_GREY = 3
    const val COLOR_YELLOW = 4
    const val COLOR_AMBER = 5
    const val COLOR_ORANGE = 6
    const val COLOR_GREEN = 7
    const val COLOR_LIGHT_GREEN = 8
    const val COLOR_LIME = 9
    const val COLOR_LIGHT_BLUE = 10
    const val COLOR_CYAN = 11
    const val COLOR_TEAL = 12
    const val COLOR_DEEP_PURPLE = 13
    const val COLOR_INDIGO = 14
    const val COLOR_BLUE = 15
    const val COLOR_RED = 16
    const val COLOR_PINK = 17
    const val COLOR_PURPLE = 18

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(COLOR_DEEP_ORANGE.toLong(),
            COLOR_BROWN.toLong(),
            COLOR_GREY.toLong(),
            COLOR_BLUE_GREY.toLong(),
            COLOR_YELLOW.toLong(),
            COLOR_AMBER.toLong(),
            COLOR_ORANGE.toLong(),
            COLOR_GREEN.toLong(),
            COLOR_LIGHT_GREEN.toLong(),
            COLOR_LIME.toLong(),
            COLOR_LIGHT_BLUE.toLong(),
            COLOR_CYAN.toLong(),
            COLOR_TEAL.toLong(),
            COLOR_DEEP_PURPLE.toLong(),
            COLOR_INDIGO.toLong(),
            COLOR_BLUE.toLong(),
            COLOR_RED.toLong(),
            COLOR_PINK.toLong(),
            COLOR_PURPLE.toLong())
    annotation class Color

    fun getColor(context: Context, @Color colorValue: Int): Int {
        val colorRes: Int
        when (colorValue) {
            COLOR_DEEP_ORANGE -> colorRes = R.color.deep_orange
            COLOR_BROWN -> colorRes = R.color.brown
            COLOR_GREY -> colorRes = R.color.grey
            COLOR_BLUE_GREY -> colorRes = R.color.blue_grey
            COLOR_YELLOW -> colorRes = R.color.yellow
            COLOR_AMBER -> colorRes = R.color.amber
            COLOR_ORANGE -> colorRes = R.color.orange
            COLOR_GREEN -> colorRes = R.color.gren
            COLOR_LIGHT_GREEN -> colorRes = R.color.light_green
            COLOR_LIME -> colorRes = R.color.lime
            COLOR_LIGHT_BLUE -> colorRes = R.color.light_blue
            COLOR_CYAN -> colorRes = R.color.cyan
            COLOR_TEAL -> colorRes = R.color.teal
            COLOR_DEEP_PURPLE -> colorRes = R.color.deep_purple
            COLOR_INDIGO -> colorRes = R.color.indigo
            COLOR_BLUE -> colorRes = R.color.blue
            COLOR_RED -> colorRes = R.color.red
            COLOR_PINK -> colorRes = R.color.pink
            COLOR_PURPLE -> colorRes = R.color.purple
            else -> throw IllegalStateException("This color value doesn't exist : " + colorValue)
        }
        return ContextCompat.getColor(context, colorRes)
    }
}
