import model.Grid
import model.Point
import model.STRAIGHTS
import util.loadIntGrid
import util.measure

fun main() {
    val grid = loadIntGrid("day10.txt")

    measure { partOne(grid) }
    measure { partTwo(grid) }
}

private fun partOne(grid: Grid<Int>) {
    val zeroes = grid.findAllMatching { it == 0 }
    val score = zeroes.map {
        step(grid, it, 0)
    }.map { it.distinct() }.flatten()
    println("day 10-1 = ${score.size}")
}

private fun partTwo(grid: Grid<Int>) {
    val zeroes = grid.findAllMatching { it == 0 }
    val score = zeroes.map {
        step(grid, it, 0)
    }.flatten()
    println("day 10-2 = ${score.size}")
}

private fun step(grid: Grid<Int>, current: Point, value: Int): List<Point> {
    if(value == 9) return listOf(current)
    return STRAIGHTS.map {
        val nextPoint = current + it.offset
        val nextValue = grid.at(nextPoint)
        if(nextValue == value + 1) {
            step(grid, nextPoint, nextValue)
        } else {
            emptyList()
        }
    }.flatten()
}