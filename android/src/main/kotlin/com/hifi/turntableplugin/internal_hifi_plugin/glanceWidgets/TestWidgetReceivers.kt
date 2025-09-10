package com.hifi.turntableplugin.internal_hifi_plugin.glanceWidgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class TestWidgetReceivers : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TestWidget()
}