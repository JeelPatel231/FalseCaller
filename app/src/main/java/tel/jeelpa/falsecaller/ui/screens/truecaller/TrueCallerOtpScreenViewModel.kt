package tel.jeelpa.falsecaller.ui.screens.truecaller

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.right
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import tel.jeelpa.falsecaller.mvi.MVI
import tel.jeelpa.falsecaller.mvi.mvi
import tel.jeelpa.falsecaller.ui.screens.truecaller.TrueCallerOtpContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.truecaller.TrueCallerOtpContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.truecaller.TrueCallerOtpContract.UiState

typealias TokenString = String


private fun generateFakeAndroidId(): String {
    val chars = "0123456789abcdef"
    return (1..16)
        .map { chars.random() }
        .joinToString("")
}

@Serializable
data class SendOtpResponse(
    val status: Long,
    val message: String,
    val domain: String,
    val parsedPhoneNumber: Long,
    val parsedCountryCode: String,
    val requestId: String,
    val method: String,
    val tokenTtl: Long,
)

@Serializable
data class SendOtpRequest(
    val countryCode: String,
    val dialingCode: Long,
    val installationDetails: InstallationDetails,
    val phoneNumber: String,
    val region: String,
    val sequenceNo: Long,
) {
    @Serializable
    data class InstallationDetails(
        val app: App,
        val device: Device,
        val language: String,
    )

    @Serializable
    data class App(
        val buildVersion: Long,
        val majorVersion: Long,
        val minorVersion: Long,
        val store: String,
    )

    @Serializable
    data class Device(
        val deviceId: String,
        val language: String,
        val manufacturer: String,
        val model: String,
        val osName: String,
        val osVersion: String,
        val mobileServices: List<String>,
    )

    companion object {
        fun default(plainNumber: String): SendOtpRequest {
            return SendOtpRequest(
                countryCode = "IN",
                dialingCode = 91,
                installationDetails = InstallationDetails(
                    app = App(
                        buildVersion = 5,
                        majorVersion = 11,
                        minorVersion = 7,
                        store = "GOOGLE_PLAY"
                    ),
                    device = Device(
                        deviceId = generateFakeAndroidId(),
                        language = "en",
                        manufacturer = "Google",
                        model = "sdk_gphone64_x86_64",
                        osName = "Android",
                        osVersion = "10",
                        mobileServices = listOf("GMS"),
                    ),
                    language = "en",
                ),
                phoneNumber = plainNumber,
                region = "region-2",
                sequenceNo = 2
            )
        }
    }
}

@Serializable
data class VerificationRequest(
    val token: String,
    val requestId: String,
    val phoneNumber: String,
    val dialingCode: Int,
    val countryCode: String,
)

@Serializable
data class VerifiedResponse(
    val status: Long,
    val message: String,
    val installationId: String,
    val ttl: Long,
    val userId: Long,
    val suspended: Boolean,
    val phones: List<Phone>,
) {
    @Serializable
    data class Phone(
        val phoneNumber: Long,
        val countryCode: String,
        val priority: Long,
    )
}

class TrueCallerOtpScreenViewModel(
    private val okHttpClient: OkHttpClient,
    private val sharedPreferences: SharedPreferences
) : ViewModel(),
    MVI<UiState, UiAction, SideEffect> by mvi(UiState.default()) {

    private suspend fun sendOtp(phoneNumber: String): SendOtpResponse {
        val countryCode = "IN"
        val dialingCode = 91
        val data = SendOtpRequest.default(phoneNumber)

        val POST_URL = "https://account-asia-south1.truecaller.com/v2/sendOnboardingOtp"
        val JSON = "application/json; charset=UTF-8".toMediaType()
        val postBody = Json.encodeToString(data)
        println(postBody)

        val request = Request.Builder()
            .url(POST_URL)
            .post(postBody.toRequestBody(JSON))
            .addHeader("content-type", "application/json; charset=UTF-8")
            .addHeader("user-agent", "Truecaller/11.75.5 (Android;10)")
            .addHeader("clientsecret", "lvc22mp3l1sfv6ujg83rd17btt")
            .build()
        println("Sending OTP to $phoneNumber")

        val response = withContext(Dispatchers.IO) { okHttpClient.newCall(request).execute() }
        val string = response.body!!.string()
        if (!response.isSuccessful) throw IllegalStateException("Request Failed with ${response.code}")

        return Json.decodeFromString<SendOtpResponse>(string).also { println("OTP RESPONSE: $it") }
    }

    private suspend fun verifyOtp(
        plainNumber: String,
        otp: String,
        sendOtpResponse: SendOtpResponse
    ): TokenString {
        val POST_URL = "https://account-asia-south1.truecaller.com/v1/verifyOnboardingOtp"
        val JSON: MediaType = "application/json; charset=UTF-8".toMediaType()

        val data = VerificationRequest(
            otp,
            sendOtpResponse.requestId,
            plainNumber,
            91,
            sendOtpResponse.parsedCountryCode
        )
        val toString = Json.encodeToString(data).also { println("SENDING FOR VERIFICATION : $it") }
        val request = Request.Builder()
            .url(POST_URL)
            .post(toString.toRequestBody(JSON))
            .addHeader("content-type", "application/json; charset=UTF-8")
            .addHeader("user-agent", "Truecaller/11.75.5 (Android;10)")
            .addHeader("clientsecret", "lvc22mp3l1sfv6ujg83rd17btt")
            .build()

        val response = withContext(Dispatchers.IO) { okHttpClient.newCall(request).execute() }
        val responseText = response.body!!.string()

        if (!response.isSuccessful) throw IllegalStateException("Request Failed with ${response.code}")

        val decoded = Json.decodeFromString<VerifiedResponse>(responseText)
            .also { println("VERIFIED RESPONSE: $it") }

        return decoded.installationId
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit {
            putString("TRUECALLER_TOKEN", token)
        }
    }

    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.UpdateNumber -> updateUiState {
                UiState.number.set(this, uiAction.number)
            }

            is UiAction.UpdateOtp -> updateUiState {
                UiState.otp.set(this, uiAction.otp)
            }

            is UiAction.SendOtp -> viewModelScope.launch {
                Either.catch { sendOtp(uiAction.number) }
                    .onLeft { emitSideEffect(SideEffect.Toast(it.message ?: "Unknown Error")) }
                    .onRight { updateUiState { UiState.sendOtpResponse.set(this, it) } }
            }

            is UiAction.VerifyOtp -> viewModelScope.launch {
                either<Throwable, Unit> {
                    val token = Either.catch {
                        verifyOtp(
                            uiAction.plainNumber,
                            uiAction.otp,
                            uiAction.sendOtpResponse
                        )
                    }
                        .onLeft { emitSideEffect(SideEffect.Toast(it.message ?: "Error")) }
                        .bind()
                    Either.catch { saveToken(token) }
                        .onLeft { emitSideEffect(SideEffect.Toast(it.message ?: "Error")) }
                        .bind()
                    emitSideEffect(SideEffect.NavigateBack).right()
                }
            }
        }
    }
}