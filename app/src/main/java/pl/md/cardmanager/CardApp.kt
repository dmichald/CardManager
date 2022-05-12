package pl.md.cardmanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import pl.md.cardmanager.di.AppModule

@HiltAndroidApp
class CardApp : Application() {
    val database by lazy { AppModule.provideCardDatabase(this) }
}