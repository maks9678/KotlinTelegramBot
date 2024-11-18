package org.example

import java.io.File

data class Word(
    val original: String,
    val translation: String,
    var correctAnswersCount: Int = 0,
)

fun main() {
    val dictionary = loadDictionary()
    val notLearnedList = dictionary.filter { it.correctAnswersCount < 3 }.toMutableList()
    while (true) {
        println(
            "Выберите действие:\n" +
                    "1. Учить слова\n" +
                    "2. Статистика\n" +
                    "0. Выход"
        )
        val input = readln()
        when (input) {
            "1" -> studyWord(notLearnedList)
            "2" -> printStatistics(dictionary)
            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun studyWord(notLearnedList: MutableList<Word>) {
    while (true) {
        if (notLearnedList.firstOrNull() == null) {
            println("Все слова в словаре выучены")
            return
        } else {
            val questionWords = notLearnedList.take(4).shuffled()
            val correctAnswer = questionWords[0]
            var correctAnswerId: Int? = null

            println("${correctAnswer.original}:")
            questionWords.forEachIndexed { index, line ->
                println("$index - ${line.translation}")
                if (correctAnswer.translation == line.translation) correctAnswerId = index
            }
            println("-------------")
            println("0 - Меню")

            println("введите цифру варианта ответа:")
            val userAnswerInput = readln().toInt()
            when (userAnswerInput) {
                correctAnswerId -> {
                    println("Правильно!")
                    correctAnswer.correctAnswersCount++
                    saveDictionary(notLearnedList)
                }

                0 -> return
                else -> println("Неправильно! ${correctAnswer.original} – это ${correctAnswer.translation}")
            }
        }
    }
}

fun saveDictionary(dictionary: List<Word>) {
    val dictionaryFile = File("word.txt")
    val content = dictionary.joinToString(separator = "\n") {
        "${it.original}|${it.translation}|${it.correctAnswersCount}"
    }
    dictionaryFile.writeText(content)
}

fun loadDictionary(): List<Word> {
    val direction = mutableListOf<Word>()
    val dictionaryFile = File("word.txt")

    dictionaryFile.forEachLine { line ->
        val parts = line.split("|")
        val correctAnswersCount = parts.getOrNull(2)?.toInt() ?: 0
        val word = Word(parts[0], parts[1], correctAnswersCount)
        direction.add(word)
    }
    return direction.toList()
}

fun printStatistics(dictionary: List<Word>) {
    val listNumberWordLearned = dictionary.filter { it.correctAnswersCount >= 3 }
    val learnedCount = listNumberWordLearned.size
    val totalCount = dictionary.size

    val percent = learnedCount / totalCount.toFloat() * 100
    println(
        "Выучено $learnedCount из $totalCount слов | ${percent}%"
    )
}