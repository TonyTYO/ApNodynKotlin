package com.example.apnodyn.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.example.apnodyn.R

object Preferences {
    // Singleton to handle settings saved in SharedPreferences

    private const val mySettings = "myPref"
    private lateinit var mSharedPreferences: SharedPreferences

    fun init(context: Context) {
        // get reference to or create SharedPreferences
        // should be called once in onCreate of mainactivity
        // then can be used anywhere using Preferences.noItems etc.
        // if defaults do not exist, set them
        mSharedPreferences = context.getSharedPreferences(mySettings, Context.MODE_PRIVATE)
        if (!mSharedPreferences.contains("DefaultNumberItems") || !mSharedPreferences.contains("DefaultHighlight")) {
            with(mSharedPreferences.edit()) {
                putInt("DefaultHighlight", ContextCompat.getColor(context, R.color.red))
                putInt("DefaultNumberItems", 10)
                apply()
            }
        }

    }
    // get and set number of items shown on widget
    var noItems: Int
        get() {
            return if (mSharedPreferences.contains("NumberItems")) {
                mSharedPreferences.getInt("NumberItems", 0)
            } else {
                mSharedPreferences.getInt("DefaultNumberItems", 0)
            }
        }
        set(value) {
            mSharedPreferences.edit {
                putInt("NumberItems", value)
                apply()}
        }

    // get and set highlight colour for widget
    var colour: Int
        get() {
            return if (mSharedPreferences.contains("Highlight")) {
                mSharedPreferences.getInt("Highlight", 0)
            } else {
                mSharedPreferences.getInt("DefaultHighlight", 0)
            }
        }
        set(value) {
            mSharedPreferences.edit {
                putInt("Highlight", value)
                apply()}
        }

    // get default values
    val defItems: Int
        get() = mSharedPreferences.getInt("DefaultNumberItems", 0)


    val defColour: Int
        get() = mSharedPreferences.getInt("DefaultHighlight", 0)

}