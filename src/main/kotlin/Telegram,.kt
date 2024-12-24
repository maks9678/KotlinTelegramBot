import java.awt.SystemColor.text
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val URL_BOT = "https://api.telegram.org/bot"

fun main(args: Array<String>) {
    var text = "Hello"
    val telegramBot = TelegramBotService(args[0])
    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBot.getUpdates()
        val messageUpdateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
        val messageInputTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val messageChatIdRegex: Regex = "\"chat\"\\s*\\{[^}]*\"id\":\\s*(\\d+)".toRegex()

        telegramBot.updateId =
            messageUpdateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull()?.plus(1) ?: continue
        println(updates)

        val inputText = messageInputTextRegex.find(updates)?.groups?.get(1)?.value ?: continue
        println(inputText)

        val chatId = messageChatIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue

        if (inputText != text) {
            text = "Введите другое слово"
        } else println(telegramBot.sendMessage(chatId))
    }
}

class TelegramBotService(private val botToken: String) {
    var updateId = 0
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(): String {
        val urlGetUpdates = "$URL_BOT$botToken/getUpdates?offset=$updateId"
        val requestUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseUpdates = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
        return responseUpdates.body()
    }

    fun sendMessage(chatId: Int): String {
        val urlOutput = "$URL_BOT$botToken/sendMessage?chat_id=$chatId&text=$text"
        val requestUpdates = HttpRequest.newBuilder().uri(URI.create(urlOutput)).build()
        val responseUpdates = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
        return responseUpdates.body()
    }
}