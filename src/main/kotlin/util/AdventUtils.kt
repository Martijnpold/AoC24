package util

fun loadLines(file: String) =
    object {}.javaClass.getResource("/$file")!!
        .readText()
        .split("\r\n|\n|\r".toRegex())
