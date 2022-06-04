package com.example.apnodyn

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apnodyn.data.Preferences
import com.example.apnodyn.databinding.ActivitySettingsBinding
import com.example.apnodyn.picker.ColourPicker

class Settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.settings_title)

        var newNumber: Int
        var newColour: Int

        // setup colourpicker
        val cp = ColourPicker(this)
        val colorCalendar = cp.createPicker(androidx.fragment.R.id.fragment_container_view_tag,
            6, cp.SIZE_SMALL, Preferences.colour)

        //Implement listener to get selected color value
        colorCalendar.setOnColorSelectedListener {
            binding.note.setTextColor(it)
            newColour = it
        }
        // set to colour saved in Preferences
        newColour = Preferences.colour
        binding.note.setTextColor(newColour)

        // setup counter for widget notes to show
        val counter = binding.integerNumber
        newNumber = Preferences.noItems
        counter.text = newNumber.toString()

        binding.increase.setOnClickListener {
            newNumber += 1
            counter.text = newNumber.toString()
        }
        binding.decrease.setOnClickListener {
            if (newNumber > 1) {newNumber -= 1}
            counter.text = newNumber.toString()
        }

        // reset to defaults button (does not save values)
        binding.reset.setOnClickListener {
            newNumber = Preferences.defItems
            newColour = Preferences.defColour
            counter.text = newNumber.toString()
            binding.note.setTextColor(newColour)
            cp.check(newColour)
        }
        // save settings button (also updates widget if showing)
        binding.save.setOnClickListener {
            Preferences.colour = newColour
            Preferences.noItems = newNumber
            Toast.makeText(this, "Wedi cadw", Toast.LENGTH_SHORT).show()
            updateWidget(this)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        this.finish()
        return true
    }
}