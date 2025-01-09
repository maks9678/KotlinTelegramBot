package org.example

import java.io.File

const val NUMBER_UNLEARNED_WORDS = 4
const val MIN_CORRECT_ANSWERS = 3

data class Word(
    val questionWord: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
)

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)
fun Question.asConsoleToString(): String {
    val variants = this.variants
        .mapIndexed { index, word: Word -> "${index + 1} - ${word.translate}" }
        .joinToString(separator = "\n")
    return this.correctAnswer.questionWord + "\n" + variants + "\n 0 - Выйти в меню "
}

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
        val questionList = if (notLearnedList.size < NUMBER_UNLEARNED_WORDS) {
            val learnedList = dictionary.filter { it.correctAnswerCount <= NUMBER_UNLEARNED_WORDS }.shuffled()
            notLearnedList.shuffled()
                .take(NUMBER_UNLEARNED_WORDS) + learnedList.take(NUMBER_UNLEARNED_WORDS - notLearnedList.size)
        } else {
            notLearnedList.shuffled().take(NUMBER_UNLEARNED_WORDS)
        }
        val correctAnswer = questionList.random()
        question = Question(
            variants = questionList,
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