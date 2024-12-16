import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    while (true) {
        Thread.sleep(2000)
        try {
        val updates: String = getUpdates(botToken, updateId)
        println(updates)


        val messageUpdateIdRegex: Regex = "\"update_id\":\"(.+?)\"".toRegex()
        val mathResultUpdateId: kotlin.text.MatchResult? = messageUpdateIdRegex.find(updates)
        val groupsUpdateId = mathResultUpdateId?.groups
        val textUpdateId = groupsUpdateId?.get(1)?.value
            updateId = textUpdateId?.toInt()?.plus(1) ?: continue

            val messageInputTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
            val mathResultInputText: kotlin.text.MatchResult? = messageInputTextRegex.find(updates)
            val groupsInputText = mathResultInputText?.groups
            val inputText = groupsInputText?.get(1)?.value
            println(inputText)
        }catch (e:Exception){continue}


    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val client: HttpClient = HttpClient.newBuilder().build()
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val requestUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val responseUpdates = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
    return responseUpdates.body()
}