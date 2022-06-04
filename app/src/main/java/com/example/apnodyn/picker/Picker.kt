package com.example.apnodyn.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.apnodyn.R
import com.example.apnodyn.picker.ColorPickerSwatch.OnColorSelectedListener


/**
 * A dialog which takes in as input an array of colors and creates a palette allowing the user to
 * select a specific color swatch, which invokes a listener.
 */
class ColorPickerDialog : Fragment(),
    OnColorSelectedListener {

    private var mTitleResId: Int = R.string.color_picker_default_title
    private var mColors: IntArray? = null
    private var mColorContentDescriptions: Array<String>? = null
    private var mSelectedColor = 0
    private var mColumns = 0
    private var mSize = 0
    private var mPalette: ColorPickerPalette? = null
    private var mListener: OnColorSelectedListener? = null

    fun initialize(titleResId: Int, colors: IntArray, selectedColor: Int, columns: Int, size: Int) {
        setArguments(titleResId, columns, size)
        setColors(colors, selectedColor)
    }

    private fun setArguments(titleResId: Int, columns: Int, size: Int) {
        val bundle = Bundle()
        bundle.putInt(KEY_TITLE_ID, titleResId)
        bundle.putInt(KEY_COLUMNS, columns)
        bundle.putInt(KEY_SIZE, size)
        arguments = bundle
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mTitleResId = requireArguments().getInt(KEY_TITLE_ID)
            mColumns = requireArguments().getInt(KEY_COLUMNS)
            mSize = requireArguments().getInt(KEY_SIZE)
        }
        if (savedInstanceState != null) {
            mColors = savedInstanceState.getIntArray(KEY_COLORS)
            mSelectedColor = (savedInstanceState.getSerializable(KEY_SELECTED_COLOR) as Int?)!!
            mColorContentDescriptions = savedInstanceState.getStringArray(
                KEY_COLOR_CONTENT_DESCRIPTIONS
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.color_picker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPalette = view.findViewById<View>(R.id.color_picker) as ColorPickerPalette
        mPalette!!.init(mSize, mColumns, this)
        if (mColors != null) {
            showPaletteView()
        }
    }

    override fun onColorSelected(color: Int) {
        mListener?.onColorSelected(color)
        if (color != mSelectedColor) {
            mSelectedColor = color
            // Redraw palette to show checkmark on newly selected color before dismissing.
            mPalette?.drawPalette(mColors, mSelectedColor)
        }
        //dismiss()
    }


    fun setOnColorSelectedListener(listener: OnColorSelectedListener) {
        mListener = listener
    }

    private fun showPaletteView() {
        if (mPalette != null) {
            refreshPalette()
            mPalette!!.visibility = View.VISIBLE
        }
    }

    private fun setColors(colors: IntArray, selectedColor: Int) {
        if (!this.mColors.contentEquals(colors) || mSelectedColor != selectedColor) {
            this.mColors = colors
            mSelectedColor = selectedColor
            refreshPalette()
        }
    }

    private fun refreshPalette() {
        if (mPalette != null && mColors != null) {
            mPalette!!.drawPalette(mColors, mSelectedColor, mColorContentDescriptions)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray(KEY_COLORS, mColors)
        outState.putSerializable(KEY_SELECTED_COLOR, mSelectedColor)
        outState.putStringArray(KEY_COLOR_CONTENT_DESCRIPTIONS, mColorContentDescriptions)
    }

    companion object {
        const val SIZE_LARGE = 1
        const val SIZE_SMALL = 2
        private const val KEY_TITLE_ID = "title_id"
        private const val KEY_COLORS = "colors"
        private const val KEY_COLOR_CONTENT_DESCRIPTIONS = "color_content_descriptions"
        private const val KEY_SELECTED_COLOR = "selected_color"
        private const val KEY_COLUMNS = "columns"
        private const val KEY_SIZE = "size"
        fun newInstance(
            titleResId: Int, colors: IntArray, selectedColor: Int,
            columns: Int, size: Int
        ): ColorPickerDialog {
            val ret = ColorPickerDialog()
            ret.initialize(titleResId, colors, selectedColor, columns, size)
            return ret
        }
    }
}