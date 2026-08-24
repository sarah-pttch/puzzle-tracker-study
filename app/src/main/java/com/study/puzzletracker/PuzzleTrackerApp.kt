package com.study.puzzletracker

import android.app.Application

class PuzzleTrackerApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}