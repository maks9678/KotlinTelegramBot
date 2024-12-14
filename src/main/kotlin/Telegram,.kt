import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)
        val startUpdateId = updates.lastIndexOf("update_id")
        val endUpdateId = updates.lastIndexOf(",\n\"message\"")
        if (startUpdateId == -1 || endUpdateId == -1) continue
        val updatesString = updates.substring(startUpdateId + 11, endUpdateId)

        updateId = updatesString.toInt() + 1
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val client: HttpClient = HttpClient.newBuilder().build()
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val requestUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val responseUpdates = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
    return responseUpdates.body()
}