package com.example.apnodyn.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.Toast
import com.example.apnodyn.NotesLists
import com.example.apnodyn.R
import com.example.apnodyn.data.Preferences
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class StickyNote : AppWidgetProvider() {

    val EXTRA_ITEM: String = "com.example.apnodyn.EXTRA_ITEM"
    private val TOAST_ACTION: String = "com.example.apnodyn.TOAST_ACTION"
    private val SCHEDULED_UPDATE: String = "com.example.apnodyn.SCHEDULED_UPDATE"
    private val ALARM_REQUEST_CODE: Int = 999

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            //updateAppWidget(context, appWidgetManager, appWidgetId)

            // Load main Widget List if user clicks on widget top
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                /* context = */ context,
                /* requestCode = */  Random().nextInt(543254),
                /* intent = */ Intent(context, NotesLists::class.java).apply {
                    putExtra("listType", "Widget")
                },
                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            // Set up the intent that starts the StackViewService, which will
            // provide the views for this collection.
            val intent = Intent(context, NotesWidgetService::class.java).apply {
                // Add the widget ID to the intent extras.
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            // Instantiate the RemoteViews object for the widget layout.
            val views = RemoteViews(context.packageName, R.layout.sticky_note).apply {
                // Set up the RemoteViews object to use a RemoteViews adapter.
                // This adapter connects to a RemoteViewsService through the
                // specified intent.
                // This is how you populate the data.
                setRemoteAdapter(R.id.stack_view, intent)

                // The empty view is displayed when the collection has no items.
                // It should be in the same layout used to instantiate the
                // RemoteViews object.
                setEmptyView(R.id.stack_view, R.id.empty_view)
                // Set click for loading main widget list
                setOnClickPendingIntent(R.id.widgetTitleLabel, pendingIntent)
            }
            // This section makes it possible for items to have individualized behavior.
            // It does this by setting up a pending intent template. Individual items of a collection
            // cannot set up their own pending intents. Instead, the collection as a whole sets
            // up a pending intent template, and the individual items set a fillInIntent
            // to create unique behavior on an item-by-item basis.

            val toastPendingIntent: PendingIntent = Intent(
                context,
                StickyNote::class.java
            ).run {
                // Set the action for the intent.
                // When the user touches a particular view, it will have the effect of
                // broadcasting TOAST_ACTION.
                action = TOAST_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))

                PendingIntent.getBroadcast(
                    context, 0, this,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            // Do additional processing specific to this widget...
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)

    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        scheduleNextUpdate(context, 1)
        Preferences.init(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        clearAlarm(context)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }

    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is TOAST_ACTION. If it is, the
    // widget displays a Toast message for the current item.
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TOAST_ACTION) {
            val appWidgetId: Int = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            // EXTRA_ITEM represents a custom value provided by the Intent
            // passed to the setOnClickFillInIntent() method, to indicate which
            // position of the item was clicked. See StackRemoteViewsFactory in
            // Set the fill-in Intent for details.
            val description: String? = intent.getStringExtra(EXTRA_ITEM)
            Toast.makeText(context, "$description", Toast.LENGTH_LONG).show()

        } else if (intent.action == SCHEDULED_UPDATE) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, StickyNote::class.java))
            manager.notifyAppWidgetViewDataChanged(ids, R.id.stack_view)
            onUpdate(context, manager, ids)
            scheduleNextUpdate(context, 1)
        }
        super.onReceive(context, intent)
    }

    private fun scheduleNextUpdate(context: Context, day: Int) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, StickyNote::class.java)
        intent.action = SCHEDULED_UPDATE
        val pendingIntent = PendingIntent.getBroadcast(
            context, ALARM_REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, day)
        }
        alarmManager[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = pendingIntent
    }

    private fun clearAlarm(context: Context) {
        // Cancel alarm by setting up a similar pendingintent and then cancelling
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, StickyNote::class.java)
        intent.action = SCHEDULED_UPDATE
        val pendingIntent = PendingIntent.getBroadcast(
            context, ALARM_REQUEST_CODE, intent,
            PendingIntent.FLAG_NO_CREATE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }


}
