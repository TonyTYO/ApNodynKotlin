package com.example.apnodyn

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.apnodyn.data.Preferences
import com.example.apnodyn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Initialise singleton for accessing stored settings
        Preferences.init(applicationContext)
    }

    // Activate and operate menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Menu options. listType is used to select list in NotesLists
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_list, R.id.menu_main_list -> {
            // User chose the "Settings" item, show the app settings UI...
            val intent = Intent(this@MainActivity, NotesLists::class.java)
            intent.putExtra("listType", "All")
            startActivity(intent)
            true
        }
        R.id.action_add, R.id.menu_main_add -> {
            val intent = Intent(this@MainActivity, NoteEntry::class.java)
            intent.putExtra("listType", "Add")
            startActivity(intent)
            true
        }
        R.id.menu_main_active -> {
            val intent = Intent(this@MainActivity, NotesLists::class.java)
            intent.putExtra("listType", "Current")
            startActivity(intent)
            true
        }
        R.id.menu_main_visible -> {
            val intent = Intent(this@MainActivity, NotesLists::class.java)
            intent.putExtra("listType", "CurrentVisible")
            startActivity(intent)
            true
        }
        R.id.menu_main_widget -> {
            val intent = Intent(this@MainActivity, NotesLists::class.java)
            intent.putExtra("listType", "Widget")
            startActivity(intent)
            true
        }
        R.id.action_settings -> {
            val intent = Intent(this@MainActivity, Settings::class.java)
            intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            true
        }
        R.id.action_delete -> {
            val intent = Intent(this@MainActivity, BatchDelete::class.java)
            startActivity(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }

    }
}