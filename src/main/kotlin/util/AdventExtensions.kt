package util

import model.Grid

fun <T> List<List<T>>.asGrid() = Grid(this)