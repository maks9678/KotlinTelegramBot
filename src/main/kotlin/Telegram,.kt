import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun main(args: Array<String>) {

    val telegramBot = TelegramBotService(args)
    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBot.getUpdates()

        telegramBot.updateId =
            telegramBot.messageUpdateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull()?.plus(1) ?: continue
        println(updates)

        val inputText = telegramBot.messageInputTextRegex.find(updates)?.groups?.get(1)?.value ?: continue
        println(inputText)

        val chatId = telegramBot.messageChatIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        println(telegramBot.sendMessage(chatId,inputText))
    }
}

class TelegramBotService(args: Array<String>) {
    private val botToken = args[0]
    var updateId = 0
    val messageUpdateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val messageInputTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val messageChatIdRegex: Regex = "\"chat\":\\{[^}]\\t\"id\":\\s*(\\d+)\"".toRegex()

    private val client: HttpClient = HttpClient.newBuilder().build()
    fun getUpdates(): String {

        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val requestUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseUpdates = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
        return responseUpdates.body()
    }

    fun sendMessage(chatId: Int, inputText: String): String {
        var text = "Hello"
        if (inputText != text) {
            text = "Введите другое слово"
        }
        val urlOutput = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$text"
        val requestUpdates = HttpRequest.newBuilder().uri(URI.create(urlOutput)).build()
        val responseUpdates = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
        return responseUpdates.body()
    }
}