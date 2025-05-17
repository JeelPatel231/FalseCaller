package tel.jeelpa.falsecaller.repository.truecaller

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class SearchResponse(
    val data: List<Datum>? = null
) {

    @Serializable
    data class Datum(
        val id: String? = null,
        val name: String? = null,
        val altName: String? = null,
        val score: Double? = null,
        val access: String? = null,
        val enhanced: Boolean? = null,
        val phones: List<Phone>? = null,
        val addresses: List<Address>? = null,
        val internetAddresses: List<InternetAddress>? = null,
        val badges: List<String>? = null,
        val tags: JsonArray? = null,
        val cacheTtl: Long? = null,
        val sources: JsonArray? = null,
        val searchWarnings: List<SearchWarning>? = null,
        val surveys: List<Survey>? = null,
        val commentsStats: CommentsStats? = null,
        val senderId: SenderId? = null,
        val manualCallerIdPrompt: Boolean? = null,
        val ns: Long? = null,
        val spamInfo: SpamInfo? = null,
        val imId: String? = null,
        val gender: String? = null,
        val image: String? = null
    )

    @Serializable
    data class Address(
        val city: String? = null,
        val countryCode: String? = null,
        val timeZone: String? = null,
        val type: String? = null,
        val address: String? = null,
        val area: String? = null,
        val street: String? = null,
        val zipCode: String? = null
    )

    @Serializable
    data class CommentsStats(
        val count: Long? = null,
        val timestamp: Long? = null,
        val showComments: Boolean? = null
    )

    @Serializable
    data class InternetAddress(
        val id: String? = null,
        val service: String? = null,
        val caption: String? = null,
        val type: String? = null
    )

    @Serializable
    data class Phone(
        val e164Format: String? = null,
        val numberType: String? = null,
        val nationalFormat: String? = null,
        val dialingCode: Int? = null,
        val countryCode: String? = null,
        val carrier: String? = null,
        val type: String? = null,
        val spamScore: Long? = null,
        val spamType: String? = null,
        val id: String? = null
    )

    @Serializable
    data class SearchWarning(
        val id: String? = null,
        val ruleName: String? = null,
        val features: JsonArray? = null,
        val ruleId: String? = null
    )

    @Serializable
    data class SenderId(
        val spamScore: Long? = null,
        val fraudScore: Long? = null,
        val isNewSender: Boolean? = null,
        val isFraudExcluded: Boolean? = null,
        val isValidSpamScore: Boolean? = null,
        val batchTimestamp: Long? = null
    )

    @Serializable
    data class SpamInfo(
        val spamScore: Long? = null,
        val spamType: String? = null,
        val spamStats: SpamStats? = null,
        val spamCategories: List<Long>? = null,
        val spamVersion: Long? = null
    )

    @Serializable
    data class SpamStats(
        val numReports: Long? = null,
        val numReports60Days: Long? = null,
        val numSearches60Days: Long? = null,
        val numCallsHourly: List<Long>? = null,
        val numCalls60Days: Long? = null,
        val numCallsNotAnswered: Long? = null,
        val numCallsAnswered: Long? = null,
        val topSpammedCountries: List<TopSpammedCountry>? = null,
        val numMessages60Days: Long? = null,
        val numCalls60DaysPointerPosition: Long? = null,
        val spammerType: String? = null,
        val numMessagesHourly: List<Long>? = null,
        val numMessages60DaysPointerPosition: Long? = null
    )

    @Serializable
    data class TopSpammedCountry(
        val countryCode: String? = null,
        val numCalls: Long? = null
    )


    @Serializable
    data class Survey(
        val id: String? = null,
        val frequency: Long? = null,
        val passthroughData: String? = null,
        val perNumberCooldown: Long? = null,
        val dynamicContentAccessKey: String? = null
    )
}
