package org.example

import java.io.File

const val NUMBER_UNLEARNED_WORDS = 4
const val MIN_CORRECT_ANSWERS = 3

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer() {

    private val dictionary = loadDictionary()
    private var question: Question? = null


    fun getStatistics(): Statistics {
        val learned = dictionary.filter { it.correctAnswerCount >= MIN_CORRECT_ANSWERS }.size
        val total = dictionary.size
        val percent = learned / total * 100
        return Statistics(learned, total, percent)

    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswerCount < MIN_CORRECT_ANSWERS }
        if (notLearnedList.isEmpty()) return null
        val questionWords = notLearnedList.take(NUMBER_UNLEARNED_WORDS).shuffled()
        val correctAnswer = questionWords.random()
        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswerCount++
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        val dictionary = mutableListOf<Word>()
        val dictionaryFile = File("word.txt")

        dictionaryFile.readLines().forEach {
            val splitLine = it.split("|")
            val splitLine2 = splitLine.getOrNull(2)?.toIntOrNull() ?: 0
            dictionary.add(Word(splitLine[0], splitLine[1], splitLine2))
        }
        return dictionary
    }

    private fun saveDictionary(dictionary: List<Word>) {
        val dictionaryFile = File("word.txt")
        val content = dictionary.joinToString(separator = "\n") {
            "${it.questionWord}|${it.translate}|${it.correctAnswerCount}"
        }
        dictionaryFile.writeText(content)
    }
}