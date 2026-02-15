package tel.jeelpa.falsecaller.ui.screens.otplogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tel.jeelpa.falsecaller.mvi.MVI
import tel.jeelpa.falsecaller.mvi.mvi
import tel.jeelpa.falsecaller.ui.screens.otplogin.OtpLoginContract.SideEffect
import tel.jeelpa.falsecaller.ui.screens.otplogin.OtpLoginContract.UiAction
import tel.jeelpa.falsecaller.ui.screens.otplogin.OtpLoginContract.UiState
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import tel.jeelpa.falsecaller.constants.Constants


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


class OtpLoginViewModel(
    private val okHttpClient: OkHttpClient,
): ViewModel(),
    MVI<UiState, UiAction, SideEffect> by mvi(UiState.default()) {

    private val laxJson = Json { ignoreUnknownKeys = true }

    private fun generateFakeAndroidId(): String {
        val chars = "0123456789abcdef"
        return (1..16)
            .map { chars.random() }
            .joinToString("")
    }

    private suspend fun sendOtpToNumber(phoneNumber: String): SendOtpResponse {
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
            .addHeader("user-agent", Constants.Truecaller.UserAgent)
            .addHeader("clientsecret", Constants.Truecaller.ClientSecret)
            .build()
        println("Sending OTP to $phoneNumber")

        val response = withContext(Dispatchers.IO) { okHttpClient.newCall(request).execute() }

        val string = response.body!!.string()
        println(string)
        if (!response.isSuccessful) throw IllegalStateException(string)

        return laxJson.decodeFromString<SendOtpResponse>(string).also { println("OTP RESPONSE: $it") }
    }

    private suspend fun exchangeOtpForToken(
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
            .addHeader("user-agent", Constants.Truecaller.UserAgent)
            .addHeader("clientsecret", Constants.Truecaller.ClientSecret)
            .build()

        val response = withContext(Dispatchers.IO) { okHttpClient.newCall(request).execute() }
        val responseText = response.body!!.string()
        println(responseText)
        if (!response.isSuccessful) throw IllegalStateException(responseText)

        val decoded = laxJson.decodeFromString<VerifiedResponse>(responseText)
            .also { println("VERIFIED RESPONSE: $it") }

        return decoded.installationId
    }

    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.PhoneNumberFieldChanged -> updateUiState {
                UiState.number.set(this, uiAction.number)
            }

            is UiAction.OtpFieldChanged -> updateUiState {
                UiState.otp.set(this, uiAction.otp)
            }

            is UiAction.SendOtpToNumber -> viewModelScope.launch {
                try {
                    // side effect, but does not need to be consumed in UI
                    val response = sendOtpToNumber(uiAction.number)
                    // advance the step to 2
                    updateUiState {
                        copy {
                            UiState.sendOtpResponse set response
                            UiState.step set OtpLoginContract.Step.Otp
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    emitSideEffect(SideEffect.Toast(e.message ?: "Unknown Error Occurred"))
                    // Otp send Failed, revert back to Phone number
                    updateUiState { UiState.step.set(this, OtpLoginContract.Step.PhoneNumber) }
                }
            }

            is UiAction.VerifyOtp -> viewModelScope.launch {
                updateUiState { UiState.step.set(this, OtpLoginContract.Step.Verification) }
                try {
                    val token = exchangeOtpForToken(uiAction.number, uiAction.otp, uiAction.sendOtpResponse)
                    emitSideEffect(SideEffect.StoreTokenInSharedPrefs(token))
                } catch (e: Throwable) {
                    e.printStackTrace()
                    emitSideEffect(SideEffect.Toast(e.message ?: "Unknown Error Occurred"))
                    // revert back to OTP state.
                    updateUiState { UiState.step.set(this, OtpLoginContract.Step.Otp) }
                }
            }
        }
    }
}
