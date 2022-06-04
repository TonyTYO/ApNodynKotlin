package com.example.apnodyn.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.apnodyn.R
import com.example.apnodyn.data.Note
import com.example.apnodyn.data.NotesDao
import com.example.apnodyn.data.NotesDatabase
import com.example.apnodyn.getStartNextDateTime


internal class NotesRemoteViewsFactory(private val mContext: Context, intent: Intent) :
    RemoteViewsFactory {
    private val mAppWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )
    private val dao: NotesDao = NotesDatabase.getDatabase(mContext, null).notesDao()
    private var reminders: MutableList<Note> = ArrayList()
    private val colorNormal: Int = ContextCompat.getColor(mContext, R.color.black)
    private val mPreferences = mContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    private var noWidget = 0
    private var colorHighlight = 0

    override fun onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
    }

    override fun onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        reminders.clear()
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(StickyNote())
    }

    override fun getCount(): Int {
        return reminders.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        val rv = RemoteViews(mContext.packageName, R.layout.sticky_note_item)
        val no = position + 1
        rv.setTextViewText(R.id.widget_count, "$no o $count")
        if (reminders[position].Highlight) {
            rv.setTextColor(R.id.widget_item, colorHighlight)
            val msg = SpannableString(reminders[position].Text)
            msg.setSpan(StyleSpan(Typeface.BOLD), 0, reminders[position].Text.length - 1, 0)
            rv.setTextViewText(R.id.widget_item, msg)
        } else {
            rv.setTextColor(R.id.widget_item, colorNormal)
            val msg = SpannableString(reminders[position].Text)
            msg.setSpan(StyleSpan(Typeface.NORMAL), 0, reminders[position].Text.length - 1, 0)
            rv.setTextViewText(R.id.widget_item, reminders[position].Text)
        }
        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        val extras = Bundle()
        extras.putString(StickyNote().EXTRA_ITEM, reminders[position].Extra)
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        // Make it possible to distinguish the individual on-click
        // action of a given item
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent)
        // Return the remote views object.
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
        noWidget = if (mPreferences.contains("NumberItems")) {
            mPreferences.getInt("NumberItems", 0)
        } else {
            mPreferences.getInt("DefaultNumberItems", 0)
        }
        colorHighlight = if (mPreferences.contains("Highlight")) {
            mPreferences.getInt("Highlight", 0)
        } else {
            mPreferences.getInt("DefaultHighlight", 0)
        }
        reminders = dao.loadForWidget(getStartNextDateTime(), noWidget)
    }

}
