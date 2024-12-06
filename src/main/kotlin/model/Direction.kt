package model

val DIAGONALS = listOf(Direction.UP_RIGHT, Direction.DOWN_RIGHT, Direction.DOWN_LEFT, Direction.UP_LEFT)
val STRAIGHTS = listOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)

enum class Direction(val offset: Point) {
    UP(0, -1),
    UP_RIGHT(1, -1),
    RIGHT(1, 0),
    DOWN_RIGHT(1, 1),
    DOWN(0, 1),
    DOWN_LEFT(-1, 1),
    LEFT(-1, 0),
    UP_LEFT(-1, -1),
    ;

    constructor(x: Int, y: Int) : this(Point(x, y))

    fun rotate(dir: Int = 2): Direction {
        val toFind = (ordinal + dir) % entries.size
        return entries.find { it.ordinal == toFind }!!
    }
}