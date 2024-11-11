package org.example

import java.io.File

fun main() {
    val dictionary = File("word.txt")
    dictionary.writeText("hello привет\n")
    dictionary.appendText("cat  кот\n")
    dictionary.appendText("dog собака\n")

    val listWord = dictionary.readLines()

    listWord.forEach { println(it) }
}