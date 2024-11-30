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
    val notLearnedList = dictionary.filter { it.correctAnswersCount < MIN_CORRECT_ANSWERS }.toMutableList()
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
        if (notLearnedList.isEmpty()) {
            println("Все слова в словаре выучены")
            return
        }
        val questionCount = minOf(notLearnedList.size, NUMBER_UNLEARNED_WORDS)
        val questionWords = notLearnedList.shuffled().take(questionCount)

        val correctAnswer = questionWords.random()
        println("${correctAnswer.original}:")
        val optionsString = questionWords.mapIndexed{ index, line ->
            "$index - ${line.translation}"
        }.joinToString(
            separator = "\n",
            prefix = "",
            postfix = "\n${questionWords.size} - Выход"
        )
        println(optionsString)
        println("введите цифру варианта ответа:")
        val answer = readln().toInt()
        if (questionWords[answer].translation == correctAnswer.translation) {
            println("Правильный ответ!")
            correctAnswer.correctAnswersCount++
        } else println("Не правильный ответ")

        if (correctAnswer.correctAnswersCount >= MIN_CORRECT_ANSWERS) notLearnedList.remove(correctAnswer)
    }
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