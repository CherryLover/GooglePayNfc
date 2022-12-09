package com.anviz.googlepaynfc

import android.app.Application
import com.anviz.logger.ConsoleLogger
import com.anviz.logger.Logger

class App : Application() {
  override fun onCreate() {
    super.onCreate()
    Logger.getInstance()
      .addLogger(ConsoleLogger())
  }
}