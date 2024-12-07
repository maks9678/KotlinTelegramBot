package org.example

import java.io.File

const val NUMBER_UNLEARNED_WORDS = 4
const val MIN_CORRECT_ANSWERS = 3

data class Word(
    val original: String,
    val translation: String,
    var correctAnswersCount: Int = 0,
)

fun main() {
    val dictionary = loadDictionary()
    while (true) {
        println(
            "Выберите действие:\n" +
                    "1. Учить слова\n" +
                    "2. Статистика\n" +
                    "0. Выход"
        )
        val input = readln()
        when (input) {
            "1" -> studyWord(dictionary)
            "2" -> printStatistics(dictionary)
            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun studyWord(dictionary: List<Word>) {
    while (true) {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < MIN_CORRECT_ANSWERS }.toMutableList()
        if (notLearnedList.isEmpty()) {
            println("Все слова в словаре выучены")
            return
        } else {
            val questionWords = notLearnedList.shuffled().take(NUMBER_UNLEARNED_WORDS)
            val correctAnswer = questionWords.random()
            val correctAnswerId: Int = questionWords.indexOf(correctAnswer)+1

            println("${correctAnswer.original}:")
            val optionsString = questionWords.mapIndexed { index, line ->
                "${index + 1} - ${line.translation}"
            }.joinToString(
                separator = "\n",
                prefix = "",
                postfix = "\n -------------\n 0 - Меню"
            )
            println(optionsString)

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
    val listNumberWordLearned = dictionary.filter { it.correctAnswersCount >= MIN_CORRECT_ANSWERS }
    val learnedCount = listNumberWordLearned.size
    val totalCount = dictionary.size

    val percent = learnedCount / totalCount.toFloat() * 100
    println(
        "Выучено $learnedCount из $totalCount слов | ${percent}%"
    )
}