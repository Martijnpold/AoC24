package model

import kotlin.math.sqrt

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    operator fun times(other: Int) = Point(x * other, y * other)

    operator fun rem(other: Int) = Point(x % other, y % other)

    fun distance(goal: Point) =
        (goal - this).let { diff -> sqrt((diff.x * diff.x + diff.y * diff.y).toDouble()) }

    override fun toString(): String = "[$x, $y]"
}