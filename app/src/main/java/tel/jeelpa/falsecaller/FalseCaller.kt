package tel.jeelpa.falsecaller

import android.app.Application
import android.provider.Settings
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import tel.jeelpa.falsecaller.di.AppModule
import tel.jeelpa.falsecaller.di.OverrideModule

class FalseCaller: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FalseCaller)
            modules(AppModule, OverrideModule)
        }
    }
}