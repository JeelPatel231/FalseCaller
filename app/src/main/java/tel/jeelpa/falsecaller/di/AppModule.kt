package tel.jeelpa.falsecaller.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import tel.jeelpa.falsecaller.repository.AndroidCallLogRepo
import tel.jeelpa.falsecaller.repository.CallLogRepo
import tel.jeelpa.falsecaller.repository.CallerInfoService
import tel.jeelpa.falsecaller.repository.EmptyCallLogRepo
import tel.jeelpa.falsecaller.repository.truecaller.TrueCallerInfoService
import tel.jeelpa.falsecaller.ui.screens.detail.DetailScreenViewModel
import tel.jeelpa.falsecaller.ui.screens.home.HomeScreenViewModel
import tel.jeelpa.falsecaller.ui.screens.settings.SettingsScreenViewModel
import tel.jeelpa.falsecaller.ui.screens.truecaller.TrueCallerOtpScreenViewModel

val AppModule = module {
    single<OkHttpClient> { OkHttpClient.Builder().build() }

    single<CallLogRepo>(named("default")) { AndroidCallLogRepo(get<Application>()) }
    single<CallLogRepo>(named("empty")) { EmptyCallLogRepo }

    single<SharedPreferences> { get<Context>().getSharedPreferences(
        "default",
        Context.MODE_PRIVATE,
    ) }
    single<CallerInfoService> {
        val sharedPrefs: SharedPreferences = get()
        TrueCallerInfoService(get()) { sharedPrefs.getString("TRUECALLER_TOKEN", null)!! }
    }

    viewModel { HomeScreenViewModel(get(named("default")), get(named("empty"))) }
    viewModel { DetailScreenViewModel(get(),get()) }
    viewModel { SettingsScreenViewModel() }
    viewModel { TrueCallerOtpScreenViewModel(get(), get()) }
}
