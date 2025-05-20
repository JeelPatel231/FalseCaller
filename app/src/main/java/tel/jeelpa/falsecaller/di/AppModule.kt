package tel.jeelpa.falsecaller.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import tel.jeelpa.falsecaller.repository.AndroidCallLogRepo
import tel.jeelpa.falsecaller.repository.CallLogRepo
import tel.jeelpa.falsecaller.repository.CallerInfoService
import tel.jeelpa.falsecaller.repository.EmptyCallLogRepo
import tel.jeelpa.falsecaller.repository.room.AppDatabase
import tel.jeelpa.falsecaller.repository.room.PhoneNumberConverter
import tel.jeelpa.falsecaller.repository.room.RoomCachedCallerInfoService
import tel.jeelpa.falsecaller.repository.truecaller.TrueCallerInfoService
import tel.jeelpa.falsecaller.ui.screens.detail.DetailScreenViewModel
import tel.jeelpa.falsecaller.ui.screens.floatingwindow.FloatingWindowViewModel
import tel.jeelpa.falsecaller.ui.screens.home.HomeScreenViewModel
import tel.jeelpa.falsecaller.ui.screens.settings.SettingsScreenViewModel

val AppModule = module {
    single<OkHttpClient> { OkHttpClient.Builder().build() }
    single<PhoneNumberUtil> { PhoneNumberUtil.createInstance(get<Context>()) }

    single<AppDatabase> {
        val phoneNumberConverter = PhoneNumberConverter(get())
        Room.databaseBuilder(get<Context>(), AppDatabase::class.java, "caller_db")
            .addTypeConverter(phoneNumberConverter)
            .build()
    }

    single<CallLogRepo>(named("default")) { AndroidCallLogRepo(get<Application>()) }
    single<CallLogRepo>(named("empty")) { EmptyCallLogRepo }

    // TODO: migrate from shared preferences to data store (honestly, useless and not worth it.)
    single<SharedPreferences> { get<Context>().getSharedPreferences(
        "default",
        Context.MODE_PRIVATE,
    ) }

    // TODO: fix getting latest token of trucaller
    single<CallerInfoService> {
        val sharedPrefs: SharedPreferences = get()
        val remoteRepo = TrueCallerInfoService(get(), get()) {
            sharedPrefs.getString("TRUECALLER_TOKEN", null)
                ?: error("Truecaller Token not available.")
        }
        val db: AppDatabase = get()
        RoomCachedCallerInfoService(db.callerDao(), remoteRepo)
    }

    viewModel { HomeScreenViewModel(get(named("default")), get(named("empty")), get()) }
    viewModel { DetailScreenViewModel(get(),get()) }
    viewModel { SettingsScreenViewModel() }
    viewModel { FloatingWindowViewModel(get(), get()) }
}
