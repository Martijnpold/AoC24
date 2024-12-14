package util

import model.Grid

fun <T> Collection<Collection<T>>.asGrid() = Grid(this.map { it.toMutableList() }.toMutableList())

fun <T> Collection<T>.hasIndex(index: Int) =
    index in 0..<size
