package util

import kotlin.system.measureNanoTime

fun loadLines(file: String) =
    object {}.javaClass.getResource("/$file")!!
        .readText()
        .split("\r\n|\n|\r".toRegex())

fun loadGrid(file: String) =
    loadLines(file).map { it.toCharArray().toList() }.asGrid()

fun measure(block: () -> Unit) {
    val nano = measureNanoTime(block)
    println("took ${nano}ns (or ${nano / 1000000.0}ms)")
}