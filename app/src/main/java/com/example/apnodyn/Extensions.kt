package com.example.apnodyn

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import com.example.apnodyn.widget.StickyNote
import java.text.SimpleDateFormat
import java.util.*


private const val SPINNER_COUNT = 3

// Update Widget if running
fun updateWidget(context: Context) {
    val manager = AppWidgetManager.getInstance(context)
    val ids = manager.getAppWidgetIds(ComponentName(context, StickyNote::class.java))
    manager.notifyAppWidgetViewDataChanged(ids, R.id.stack_view)
    if (ids.isNotEmpty()) {
        manager.notifyAppWidgetViewDataChanged(ids, R.id.stack_view)
    }
}

/**
 * Changes the [DatePicker] date format.
 * Example: dmy will show the date picker in day, month, year order
 * */
fun DatePicker.formatDate(ymdOrder: String) {
    val system = Resources.getSystem()
    val idYear = system.getIdentifier("year", "id", "android")
    val idMonth = system.getIdentifier("month", "id", "android")
    val idDay = system.getIdentifier("day", "id", "android")
    val idLayout = system.getIdentifier("pickers", "id", "android")
    val spinnerYear = findViewById<View>(idYear) as NumberPicker
    val spinnerMonth = findViewById<View>(idMonth) as NumberPicker
    val spinnerDay = findViewById<View>(idDay) as NumberPicker
    val layout = findViewById<View>(idLayout) as LinearLayout
    layout.removeAllViews()
    for (i in 0 until SPINNER_COUNT) {
        when (ymdOrder[i]) {
            'y' -> {
                layout.addView(spinnerYear)
                setImeOptions(spinnerYear, i)
            }
            'm' -> {
                layout.addView(spinnerMonth)
                setImeOptions(spinnerMonth, i)
            }
            'd' -> {
                layout.addView(spinnerDay)
                setImeOptions(spinnerDay, i)
            }
            else -> throw IllegalArgumentException("Invalid char[] ymdOrder")
        }
    }
}

// Set Ime spinner options
private fun setImeOptions(spinner: NumberPicker, spinnerIndex: Int) {
    val imeOptions: Int = if (spinnerIndex < SPINNER_COUNT - 1) {
        EditorInfo.IME_ACTION_NEXT
    } else {
        EditorInfo.IME_ACTION_DONE
    }
    val idPickerInput: Int =
        Resources.getSystem().getIdentifier("numberpicker_input", "id", "android")
    val input = spinner.findViewById<View>(idPickerInput) as TextView
    input.imeOptions = imeOptions
}

// Get date from datepicker
fun DatePicker.getDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    return calendar.time
}

// Get current date
fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}
// Get date of next day
fun getStartNextDateTime(): Date {
    val calendar: Calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, 1)
    }
    return Date(calendar.timeInMillis)
}

// Return date object as string dd-mm-yyyy
fun dateToString(date: Date): String {
    val pattern = "dd-MM-yyyy"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.UK)
    return simpleDateFormat.format(date)
}

fun dateToLongString(date: Date): String {
    val pattern = "yyyy-MM-dd HH:mm:ss.SSS"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.UK)
    return simpleDateFormat.format(date)
}