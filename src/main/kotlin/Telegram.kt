import org.example.LearnWordsTrainer
import org.example.Question
import org.example.asConsoleToString
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val URL_BOT = "https://api.telegram.org/bot"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val STATISTIC_CLINKED = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

fun main(args: Array<String>) {
    val telegramBot = TelegramBotService(args[0])
    var updates: String

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь.")
        return
    }

    val messageUpdateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val messageInputTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val messageChatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        updates = telegramBot.getUpdates()
        telegramBot.updateId =
            messageUpdateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull()?.plus(1) ?: continue
        println(updates)

        val inputText = messageInputTextRegex.find(updates)?.groups?.get(1)?.value ?: continue


        val chatId = messageChatIdRegex.find(updates)?.groups?.get(1)?.value?.toLongOrNull()?:0
        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        if (inputText.lowercase() == "/start") {
            telegramBot.sendMenu(chatId)
        }
        if (data?.lowercase() == STATISTIC_CLINKED) {
            val statistics = trainer.getStatistics()
            telegramBot.sendMessage(
                chatId,
                "Выучено: ${statistics.learned} из ${statistics.total} | ${statistics.percent}%"
            )
        }
        if (data?.lowercase() == LEARN_WORDS_CLICKED && chatId != null) {
                val question: Question? = trainer.getNextQuestion()
                if (question == null) {
                    telegramBot.sendMessage(chatId, "Все слова в словаре выучены")
                    break
                } else {
                    telegramBot.sendQuestion(chatId, question)
                    val userAnswerInput: Int? = data.removePrefix(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull()

                    if (trainer.checkAnswer(userAnswerInput) ){
                        telegramBot.sendMessage(chatId, "Правильно!")
                    } else {
                        telegramBot.sendMessage(
                            chatId,
                            "Неправильно ${question.correctAnswer.questionWord} - ${question.correctAnswer.translate}"
                        )
                        continue
                    }
                }
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

    fun sendMessage(chatId: Long, text: String): String {
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

    fun sendMenu(chatId: Long): String {
        val urlOutput = "$URL_BOT$botToken/sendMessage"
        val sendMenuBody = """{
    "chat_id": $chatId,
    "text": "Основное меню",
    "reply_markup": {
        "inline_keyboard": [
            [
                {
                    "text": "Изучить слова",
                    "callback_data": "$LEARN_WORDS_CLICKED"
                },
                {
                    "text": "Статистика",
                    "callback_data": "$STATISTIC_CLINKED"
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

    fun sendQuestion(chatId: Long, question: Question): String {
        val urlOutput = "$URL_BOT$botToken/sendMessage"
        val sendMenuBody = """{
    "chat_id": $chatId,
    "text": "How it translates: ${question.correctAnswer.questionWord}",
    "reply_markup": {
        "inline_keyboard": [
            [
                {
                    "text": "${question.variants[0].translate}",
                    "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}1"
                },
                {
                    "text": "${question.variants[1].translate}",
                    "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}2"
                },
                {
                    "text": "${question.variants[2].translate}",
                    "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}3"
                },
                {
                    "text": "${question.variants[3].translate}",
                    "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}4"
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