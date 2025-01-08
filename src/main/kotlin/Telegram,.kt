import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val URL_BOT = "https://api.telegram.org/bot"

fun main(args: Array<String>) {
    val telegramBot = TelegramBotService(args[0])
    val updates: String = telegramBot.getUpdates()

    val messageUpdateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val messageInputTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val messageChatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        telegramBot.updateId =
            messageUpdateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull()?.plus(1) ?: continue
        println(updates)

        val inputText = messageInputTextRegex.find(updates)?.groups?.get(1)?.value ?: continue


        val chatId = messageChatIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull()
        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        if (inputText.lowercase() == "hello" && chatId != null) {
            telegramBot.sendMessage(chatId, "hello")
        }
        if (inputText.lowercase() == "menu"&& chatId != null) {
            telegramBot.sendMenu(chatId)
        }
        if (data?.lowercase() == "statistics_clicked"&& chatId != null) {
            telegramBot.sendMessage(chatId, "Выучено 100 из 100 слов ")
        }
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

    fun sendMessage(chatId: Int, text: String): String {
        val encoded = URLEncoder.encode(
            text,
            StandardCharsets.UTF_8
        )
        println(encoded)
        val urlOutput = "$URL_BOT$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val requestUpdates = HttpRequest.newBuilder().uri(URI.create(urlOutput)).build()
        val responseUpdates = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
        return responseUpdates.body()
    }

    fun sendMenu(chatId: Int): String {
        val urlOutput = "$URL_BOT$botToken/sendMessage"
        val sendMenuBody = """{
    "chat_id": $chatId,
    "text": "Основное меню",
    "reply_markup": {
        "inline_keyboard": [
            [
                {
                    "text": "Изучить слова",
                    "callback_data": "learn_words_clicked"
                },
                {
                    "text": "Статистика",
                    "callback_data": "statistics_clicked"
                }
            ]
        ]
    }
}""".trimIndent()
        val client: HttpClient = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlOutput))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}