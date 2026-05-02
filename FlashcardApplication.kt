package com.example.flashcardproto1

import android.app.Application

class FlashcardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FlashcardRepository.initialize(this)
    }
}

