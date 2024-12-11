package util

import model.Grid

fun <T> List<List<T>>.asGrid() = Grid(this)

fun <T> List<T>.hasIndex(index: Int) =
    index in 0..<size
