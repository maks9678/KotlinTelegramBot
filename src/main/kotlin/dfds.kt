import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.Word

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName ("callback_query")
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
)
@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
)


fun main() {
    val a = """{
    "ok": true,
    "result": [
        {
            "update_id": 960373510,
            "message": {
                "message_id": 782,
                "from": {
                    "id": 1990405187,
                    "is_bot": false,
                    "first_name": "\u041c\u0430\u043a\u0441\u0438\u043c",
                    "username": "makskrutikov",
                    "language_code": "ru"
                },
                "chat": {
                    "id": 1990405187,
                    "first_name": "\u041c\u0430\u043a\u0441\u0438\u043c",
                    "username": "makskrutikov",
                    "type": "private"
                },
                "date": 1738010522,
                "text": "/start",
                "entities": [
                    {
                        "offset": 0,
                        "length": 6,
                        "type": "bot_command"
                    }
                ]
            }
        }
    ]
}""".trimIndent()

    val json = Json {
        ignoreUnknownKeys = true
    }
    val response = json.decodeFromString<Response>(a)
}