import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val messageUpdateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val messageInputTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)

        updateId = messageUpdateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull()?.plus(1) ?: continue
        println(updates)

        val inputText = messageInputTextRegex.find(updates)?.groups?.get(1)?.value ?: continue
        println(inputText)
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val client: HttpClient = HttpClient.newBuilder().build()
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val requestUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val responseUpdates = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
    return responseUpdates.body()
}