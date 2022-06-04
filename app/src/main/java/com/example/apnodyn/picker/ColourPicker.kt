package com.example.apnodyn.picker

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.apnodyn.R

class ColourPicker(private val activity: Activity) {

    val SIZE_LARGE = ColorPickerDialog.SIZE_LARGE
    val SIZE_SMALL = ColorPickerDialog.SIZE_SMALL
    private lateinit var colorCalendar: ColorPickerDialog

    fun createPicker(container: Int, columns: Int, size: Int, colour: Int)
            : ColorPickerDialog{

        val mColor = colorChoice(activity)

        colorCalendar = ColorPickerDialog.newInstance(
            R.string.color_picker_default_title, mColor, colour, columns, size)
        val fragmentManager: FragmentManager = (activity as FragmentActivity).supportFragmentManager
        fragmentManager
            .beginTransaction()
            .replace(container, colorCalendar)
            .commit()

        return colorCalendar
    }

    fun check(colour: Int) {
        colorCalendar.onColorSelected(colour)
    }

    fun showColour(context: Context, color: Int): String {
        val colorArray: Array<String> =
            context.resources.getStringArray(R.array.default_color_choice_values)
        val nameArray: Array<String> =
            context.resources.getStringArray(R.array.default_color_choice_names)

        if (colorArray.isNotEmpty() && nameArray.isNotEmpty()) {
            for (i in colorArray.indices) {
                if (Color.parseColor(colorArray[i]) == color)
                    return nameArray[i]
            }
        }
        return ""
    }

    private fun colorChoice(context: Context): IntArray {

        val colorArray: Array<String> =
            context.resources.getStringArray(R.array.default_color_choice_values)

        val mColorChoices = IntArray(colorArray.size)
        if (colorArray.isNotEmpty()) {
            for (i in colorArray.indices) {
                mColorChoices[i] = Color.parseColor(colorArray[i])
            }
        }
        return mColorChoices
    }
}