package tel.jeelpa.falsecaller.repository.truecaller

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import tel.jeelpa.falsecaller.constants.Constants
import tel.jeelpa.falsecaller.models.CallerInfo
import tel.jeelpa.falsecaller.repository.CallerInfoService
import tel.jeelpa.falsecaller.utils.e164Format

class TrueCallerInfoService(
    private val phoneNumberUtil: PhoneNumberUtil,
    private val httpClient: OkHttpClient,
    private val getToken: () -> String,
) : CallerInfoService {
    companion object {
        val JsonDecoder = Json { ignoreUnknownKeys = true }
        const val BASE_URL = "https://search5-noneu.truecaller.com"
    }

    private val client = httpClient.newBuilder().addInterceptor {
        val token = getToken()
        val request = it.request()
        val newRequest = request.newBuilder().header("Accept", "application/json")
            .header("User-Agent", Constants.Truecaller.UserAgent)
            .header("Authorization", "Bearer $token").build()
        it.proceed(newRequest)
    }.build()

    override suspend fun getDetailsFromNumber(number: PhoneNumber): CallerInfo {
        val query = phoneNumberUtil.e164Format(number)
        val request =
            Request.Builder().url("$BASE_URL/v2/search?q=$query&type=4")
                .get().build()
        return withContext(Dispatchers.IO) { 
            client.newCall(request).execute().use { response ->
                val responseText = response.body!!.string()
                if (!response.isSuccessful) throw IllegalStateException(responseText)
                val searchResponse: SearchResponse = JsonDecoder.decodeFromString(responseText)

                val bestMatch = searchResponse.data!!.first()

                CallerInfo(
                    name = bestMatch.name ?: bestMatch.altName ?: "Unknown Name",
                    score = bestMatch.score ?: 0.0,
                    spamScore = bestMatch.spamInfo?.spamScore?.toDouble() ?: 0.0,
                )
            }
        }
    }
}
