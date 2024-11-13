package org.example.tgBot

import java.io.File

data class Word(
    val original: String,
    val translation: String,
    var correctAnswersCount: Int = 0
)

fun main() {
    val dictionaryFile = File("word.txt")
    val dictionary = mutableListOf<Word>()
    dictionaryFile.forEachLine { line ->
        val parts = line.split("|")

        val correctAnswersCount = parts.getOrNull(2)?.toInt() ?: 0

        val word = Word(parts[0], parts[1], correctAnswersCount)
        dictionary.add(word)
    }
    dictionary.forEach { word ->
        println("Word: ${word.original}, " +
                "Translation: ${word.translation}, " +
                "Correct Answers: ${word.correctAnswersCount}"
        )
    }
}
