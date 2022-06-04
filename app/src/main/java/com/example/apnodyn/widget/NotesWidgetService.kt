package com.example.apnodyn.widget

import android.content.Intent
import android.widget.RemoteViewsService

class NotesWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NotesRemoteViewsFactory(this.applicationContext, intent)
    }
}