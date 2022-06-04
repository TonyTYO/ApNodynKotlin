package com.example.apnodyn.picker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.example.apnodyn.R

/**
 * Creates a circular swatch of a specified color.  Adds a checkmark if marked as checked.
 */
class ColorPickerSwatch(
    context: Context?, private val mColor: Int, checked: Boolean,
    private val mOnColorSelectedListener: OnColorSelectedListener?,
) : FrameLayout(context!!), View.OnClickListener {

    private val mSwatchImage: ImageView
    private val mCheckmarkImage: ImageView

    /**
     * Interface for a callback when a color square is selected.
     */
    fun interface OnColorSelectedListener {
        /**
         * Called when a specific color square has been selected.
         */
        fun onColorSelected(color: Int)
    }

    private fun setColor(color: Int) {
        val colorDrawable = arrayOf(ResourcesCompat.getDrawable(resources, R.drawable.color_picker_swatch, null))
        mSwatchImage.setImageDrawable(ColorStateDrawable(colorDrawable, color))
    }

    private fun setChecked(checked: Boolean) {
        if (checked) {
            mCheckmarkImage.visibility = VISIBLE
        } else {
            mCheckmarkImage.visibility = GONE
        }
    }

    override fun onClick(v: View) {
        mOnColorSelectedListener?.onColorSelected(mColor)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.color_picker_swatch, this)
        mSwatchImage = findViewById<View>(R.id.color_picker_swatch) as ImageView
        mCheckmarkImage = findViewById<View>(R.id.color_picker_checkmark) as ImageView
        setColor(mColor)
        setChecked(checked)
        setOnClickListener(this)
    }
}


/**
 * A drawable which sets its color filter to a color specified by the user, and changes to a
 * slightly darker color when pressed or focused.
 */
class ColorStateDrawable(layers: Array<Drawable?>?, private val mColor: Int) :
    LayerDrawable(layers!!) {
    override fun onStateChange(states: IntArray): Boolean {
        var pressedOrFocused = false
        for (state in states) {
            if (state == android.R.attr.state_pressed || state == android.R.attr.state_focused) {
                pressedOrFocused = true
                break
            }
        }
        colorFilter = if (pressedOrFocused) {
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                getPressedColor(mColor),
                BlendModeCompat.SRC_ATOP
            )
        } else {
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                mColor,
                BlendModeCompat.SRC_ATOP
            )
        }
        return super.onStateChange(states)
    }

    override fun isStateful(): Boolean {
        return true
    }

    companion object {
        private const val PRESSED_STATE_MULTIPLIER = 0.70f

        /**
         * Given a particular color, adjusts its value by a multiplier.
         */
        private fun getPressedColor(color: Int): Int {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            hsv[2] = hsv[2] * PRESSED_STATE_MULTIPLIER
            return Color.HSVToColor(hsv)
        }
    }
}