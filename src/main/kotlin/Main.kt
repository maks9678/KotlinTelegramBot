package org.example

fun main() {

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь.")
        return
    }
    while (true) {
        println(
            "Выберите действие:\n" +
                    "1. Учить слова\n" +
                    "2. Статистика\n" +
                    "0. Выход"
        )

        when (readln().toIntOrNull()) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        println("Все слова в словаре выучены")
                        break
                    } else {
                        println(question.asConsoleToString())
                        val userAnswerInput = readln().toIntOrNull()
                        if (userAnswerInput == 0) break
                        if (trainer.checkAnswer(userAnswerInput?.minus(1))) {
                            println("Правильно!\n")
                        } else {
                            println("Неправильно ${question.correctAnswer.questionWord} - ${question.correctAnswer.translate}")
                            continue
                        }
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println("Выучено:${statistics.learned} из ${statistics.total} | ${statistics.percent}")
            }

            0 -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}