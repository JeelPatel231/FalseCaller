package tel.jeelpa.falsecaller.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import tel.jeelpa.falsecaller.repository.CallLogRepo
import tel.jeelpa.falsecaller.repository.CallerInfoService
import tel.jeelpa.falsecaller.repository.MockCallLogRepo
import tel.jeelpa.falsecaller.repository.MockCallerInfoService

val OverrideModule = module {
//    single<CallLogRepo>(named("default")) { MockCallLogRepo() }
    single<CallerInfoService> { MockCallerInfoService() }
}