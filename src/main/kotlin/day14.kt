import model.Point
import model.makeGrid
import util.loadLines
import util.measure
import kotlin.math.abs

fun main() {
    val robots = loadLines("day14.txt")
        .map { """p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex().find(it)!! }
        .map { match ->
            val values = match.groupValues.subList(1, match.groups.size).map { it.toInt() }
            Robot(
                Point(values[0], values[1]),
                Point(values[2], values[3])
            )
        }

    measure { partOne(robots) }
    measure { partTwo(robots) }
}

private fun partOne(robots: List<Robot>) {
    val gridSize = 101 to 103

    val quadrantCounts = robots.mapNotNull {
        val position = it.calculatePosition(100, gridSize)
        val quadrant = calculateQuadrant(position, gridSize)
        quadrant
    }.groupBy { it }

    var sum = 1
    quadrantCounts.values.forEach { sum *= it.size }

    println("day 14-1 = $sum")
}

private fun partTwo(robots: List<Robot>) {
    val gridSize = 101 to 103
    val answer = 8149

    val grid = makeGrid(gridSize.first, gridSize.second) { '.' }

    robots.forEach {
        val position = it.calculatePosition(answer, gridSize)
        grid.set(position, 'x')
    }

    grid.print()

    println("day 14-2 = $answer")
}

private class Robot(
    val position: Point,
    val velocity: Point,
) {
    fun calculatePosition(seconds: Int, gridSize: Pair<Int, Int>): Point {
        val raw = position + velocity * seconds
        val cappedMax = Point(raw.x % gridSize.first, raw.y % gridSize.second)
        val x = if (cappedMax.x >= 0) cappedMax.x else {
            gridSize.first - (abs(cappedMax.x) % gridSize.first)
        }
        val y = if (cappedMax.y >= 0) cappedMax.y else {
            gridSize.second - (abs(cappedMax.y) % gridSize.second)
        }
        return Point(x, y)
    }
}

private fun calculateQuadrant(point: Point, gridSize: Pair<Int, Int>): Int? {
    val xSize = gridSize.first / 2
    val ySize = gridSize.second / 2

    return if (point.x < xSize) {
        if (point.y < ySize) 0
        else if (point.y > ySize) 2
        else null
    } else if (point.x > xSize) {
        if (point.y < ySize) 1
        else if (point.y > ySize) 3
        else null
    } else null
}