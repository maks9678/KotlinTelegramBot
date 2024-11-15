package org.example

import java.io.File

data class Word(
    val original: String,
    val translation: String,
    var correctAnswersCount: Int = 0
)

fun loadDictionary(): MutableList<Word> {
    val direction = mutableListOf<Word>()
    val dictionaryFile = File("word.txt")

    dictionaryFile.forEachLine { line ->
        val parts = line.split("|")
        val correctAnswersCount = parts.getOrNull(2)?.toInt() ?: 0
        val word = Word(parts[0], parts[1], correctAnswersCount)
        direction.add(word)
    }
    return direction
}

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
            "1" -> println("Учить слова")
            "2" -> printStatistics(dictionary)
            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun printStatistics(dictionary: MutableList<Word>) {
    val listNumberWordLearned = dictionary.filter { it.correctAnswersCount >= 3 }
    val learnedCount= listNumberWordLearned.size
    val totalCount = dictionary.size

    val percent = learnedCount / totalCount.toFloat() * 100
    println(
        "Выучено $learnedCount из $totalCount слов | ${percent}%"
    )
}