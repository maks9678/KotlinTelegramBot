package org.example.tgBot

import java.io.File

fun main() {
    val dictionary = File("word.txt")
    dictionary.forEachLine {println(it)}
}