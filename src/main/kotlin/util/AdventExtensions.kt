package util

import model.Grid

fun <T> Collection<Collection<T>>.asGrid() = Grid(this.map { it.toMutableList() }.toMutableList())

fun Collection<String>.asCharGrid() = this.map { it.map { char -> char } }.asGrid()

fun <T> Collection<T>.hasIndex(index: Int) =
    index in 0..<size

fun <T> List<T>.splitBy(value: T): List<List<T>> {
    val split = mutableListOf<List<T>>()
    var last = -1
    forEachIndexed { index, t ->
        if(t == value) {
            split.add(subList(last + 1, index))
            last = index
        }
    }
    if(last + 1 < size) split.add(subList(last + 1, size))
    return split
}