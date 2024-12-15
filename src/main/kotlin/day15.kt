import model.Direction
import model.Grid
import model.Point
import model.makeGrid
import util.*

fun main() {
    val parts = loadLines("day15.txt").splitBy("")

    val charGrid = parts[0].asCharGrid()

    val commands = parts[1].joinToString("").mapNotNull {
        when (it) {
            '^' -> Direction.UP
            '>' -> Direction.RIGHT
            'v' -> Direction.DOWN
            '<' -> Direction.LEFT
            else -> null
        }
    }


    measure { partOne(charGrid.asEntityGrid(), commands) }
    measure { partTwo(charGrid.asEntityGrid(), commands) }
}

private fun partOne(grid: Grid<Entity?>, commands: List<Direction>) {
    val player = grid.findFirstMatching { _, entity -> entity is Player }!!
        .let { grid.at(it) as Player }

    commands.forEach { player.push(it) }

    val sum = grid.findAllMatching { it is Box }
        .sumOf { grid.at(it)!!.coordinate() }

    println("day 15-1 = $sum")
}

private fun partTwo(grid: Grid<Entity?>, commands: List<Direction>) {
    val bigGrid = makeGrid<Entity?>(grid.width() * 2, grid.height()) { null }
    grid.findAllMatching { it != null }.forEach { pos ->
        grid.at(pos).split(bigGrid).let { pair ->
            if (pair.first != null) bigGrid.set(pair.first!!.point, pair.first)
            if (pair.second != null) bigGrid.set(pair.second!!.point, pair.second)
        }
    }

    val bigPlayer = bigGrid.findFirstMatching { _, entity -> entity is Player }
        .let { bigGrid.at(it!!) as Player }

//    bigGrid.print { "${it.asChar()}" }

    commands.forEach {
        bigPlayer.push(it)
//        bigGrid.print { "${it.asChar()}" }
//        println("")
    }

    val sum = bigGrid.findAllMatching { it is DoubleBox && it.isLeftHalf() }
        .sumOf { bigGrid.at(it)!!.coordinate() }

    println("day 15-2 = $sum")
}

private abstract class Entity(
    var grid: Grid<Entity?>,
    var point: Point,
) {
    fun moveTo(newPoint: Point) {
        grid.set(point, null)
        grid.set(newPoint, this)
        point = newPoint
    }

    abstract fun canPush(direction: Direction): Boolean

    abstract fun push(direction: Direction): Boolean

    fun coordinate() = point.y * 100 + point.x

    override fun toString(): String = "${asChar()}"
}

private abstract class Pushable(grid: Grid<Entity?>, point: Point) : Entity(grid, point) {
    override fun canPush(direction: Direction): Boolean {
        val nb = grid.at(point + direction.offset)
        return nb == null || nb.canPush(direction)
    }

    override fun push(direction: Direction): Boolean {
        val nb = grid.at(point + direction.offset)
        return if (nb == null || (nb.canPush(direction) && nb.push(direction))) {
            moveTo(point + direction.offset)
            true
        } else false
    }
}

private class Wall(grid: Grid<Entity?>, point: Point) : Entity(grid, point) {
    override fun canPush(direction: Direction): Boolean = false

    override fun push(direction: Direction): Boolean = false
}

private class Player(grid: Grid<Entity?>, point: Point) : Pushable(grid, point)

private class Box(grid: Grid<Entity?>, point: Point) : Pushable(grid, point)

private class DoubleBox(grid: Grid<Entity?>, point: Point, var partner: DoubleBox?) : Pushable(grid, point) {
    override fun canPush(direction: Direction): Boolean {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) return super.canPush(direction)
        return super.canPush(direction) && partner?.canPushUnpartnered(direction) == true
    }

    private fun canPushUnpartnered(direction: Direction) = super.canPush(direction)

    override fun push(direction: Direction): Boolean {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) return super.push(direction)
        return super.push(direction).also { wasPushed ->
            if (wasPushed && partner!!.point.y != point.y) partner!!.push(direction)
        }
    }

    fun isLeftHalf() = point.x < partner!!.point.x
}

private fun Entity?.split(toGrid: Grid<Entity?>): Pair<Entity?, Entity?> {
    if (this == null) return null to null
    val left = Point(point.x * 2, point.y)
    val right = left + Direction.RIGHT.offset
    return when (this) {
        is Wall -> Wall(toGrid, left) to Wall(toGrid, right)
        is Player -> Player(toGrid, left) to null
        is Box -> (DoubleBox(toGrid, left, null) to DoubleBox(toGrid, right, null))
            .also {
                it.first.partner = it.second
                it.second.partner = it.first
            }

        else -> null to null
    }
}

private fun Grid<Char>.asEntityGrid(): Grid<Entity?> {
    val grid = makeGrid<Entity?>(width(), height()) { null }

    findAllMatching { it != '.' }
        .forEach { point ->
            val char = at(point)
            val ent = when (char) {
                '#' -> Wall(grid, point)
                'O' -> Box(grid, point)
                '@' -> Player(grid, point)
                else -> null
            }
            grid.set(point, ent)
        }

    return grid
}

private fun Entity?.asChar() = when (this) {
    is Player -> '@'
    is Wall -> '#'
    is Box -> 'O'
    is DoubleBox -> if (isLeftHalf()) '[' else ']'
    else -> '.'
}