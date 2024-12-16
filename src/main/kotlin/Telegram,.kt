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

        val messageUpdateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
        val mathResultUpdateId: MatchResult? = messageUpdateIdRegex.find(updates)
        val groupsUpdateId = mathResultUpdateId?.groups
        val textUpdateId = groupsUpdateId?.get(1)?.value
        if(textUpdateId!=null){
        updateId = textUpdateId.toInt()+1
            println(updates)}
        else continue

        val messageInputTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val mathResultInputText: MatchResult? = messageInputTextRegex.find(updates)
        val groupsInputText = mathResultInputText?.groups
        val inputText = groupsInputText?.get(1)?.value
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