import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.LearnWordsTrainer
import org.example.Question
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val URL_BOT = "https://api.telegram.org/bot"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val STATISTIC_CLINKED = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)


fun main(args: Array<String>) {

    val json = Json {
        ignoreUnknownKeys = true
    }

    val telegramBot = TelegramBotService(args[0])

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь.")
        return
    }

    while (true) {
        Thread.sleep(2000)
        val responseString = telegramBot.getUpdates()
        println(responseString)

        val response = json.decodeFromString<Response>(responseString)
        val updates = response.result
        val firstUpdate: Update = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        telegramBot.updateId = updateId + 1L

        val message: String? = firstUpdate.message?.text
        val chatId: Long? = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val data: String? = firstUpdate.callbackQuery?.data

        if (message?.lowercase() == "/start" && chatId != null) {
            telegramBot.sendMenu(json, chatId)
        }
        if (data?.lowercase() == STATISTIC_CLINKED && chatId != null) {
            val statistics = trainer.getStatistics()
            telegramBot.sendMessage(
                json,
                chatId,
                "Выучено: ${statistics.learned} из ${statistics.total} | ${statistics.percent}%"
            )
        }
        if (data == LEARN_WORDS_CLICKED && chatId != null) {
            checkNextQuestionAndSend(json, trainer, telegramBot, chatId)
        }

        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true && chatId != null) {
            val answerId = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()-1
            if (trainer.checkAnswer(answerId)) {
                telegramBot.sendMessage(json, chatId, "Правильно!")
            } else {
                telegramBot.sendMessage(
                    json,
                    chatId,
                    "Не правильно: ${trainer.question?.correctAnswer?.questionWord} - ${trainer.question?.correctAnswer?.translate}"
                )
            }
            checkNextQuestionAndSend(json, trainer, telegramBot, chatId)
        }
    }
}


fun checkNextQuestionAndSend(
    json: Json,
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long

) {
    val question: Question? = trainer.getNextQuestion()
    if (question == null) {
        telegramBotService.sendMessage(json, chatId, "Все слова в словаре выучены")
    } else {
        telegramBotService.sendQuestion(json, chatId, question)

    }
}

class TelegramBotService(private val botToken: String) {
    var updateId = 0L
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(): String {
        val urlGetUpdates = "$URL_BOT$botToken/getUpdates?offset=$updateId"
        val requestUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseUpdates = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
        return responseUpdates.body()
    }

    fun sendMessage(json: Json, chatId: Long, text: String): String {
        val urlOutput = "$URL_BOT$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = text,
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request = HttpRequest.newBuilder().uri(URI.create(urlOutput))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(json: Json, chatId: Long): String {
        val urlOutput = "$URL_BOT$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = (ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard("Изучить слова", LEARN_WORDS_CLICKED),
                        InlineKeyboard("Статистика", STATISTIC_CLINKED),
                    )
                )
            )
                    )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request = HttpRequest.newBuilder().uri(URI.create(urlOutput))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendQuestion(json: Json, chatId: Long, question: Question): String {

        val urlOutput = "$URL_BOT$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "How it translates: ${question.correctAnswer.questionWord}",
            replyMarkup = ReplyMarkup(listOf(question.variants.mapIndexed { index, word ->
                InlineKeyboard(word.translate, "${CALLBACK_DATA_ANSWER_PREFIX}${index + 1}")
            }))

        )
        val requestBodyString = json.encodeToString(requestBody)
        val request = HttpRequest.newBuilder().uri(URI.create(urlOutput))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}