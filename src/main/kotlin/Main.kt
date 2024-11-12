package org.example

import java.io.File

fun main() {
    val dictionary = File("word.txt")
    dictionary.forEachLine {println(it)}
}