import model.Direction
import model.Grid
import model.Point
import util.asGrid
import util.loadGrid
import util.measure


fun main() {
    val grid = loadGrid("day6.txt")

    measure { partOne(grid) }
    measure { partTwo(grid) }
}

private fun partOne(grid: Grid<Char>) {
    val seen = stepsToLeave(grid).distinctBy { it.first }.size

    println("day 6-1 = $seen").let {  }
}

private fun partTwo(grid: Grid<Char>) {
    val seen = stepsToLeave(grid)

    //For every location visited, check if putting a wall in front of the guard makes it unsolvable (list size of 0)
    //Kind of crude implementation, but basically just copies the grid and adds a tile to it and reruns
    val blockable = hashSetOf<Point>()
    seen.forEach { (pos, dir) ->
        val blockade = pos + dir.offset
        if (grid.at(blockade) == '^') return@forEach
        if (grid.isInBounds(blockade)) {
            val gridList = grid.list.toMutableList().map { it.toMutableList() }
            gridList[blockade.y][blockade.x] = 'O'
            val newGrid = gridList.asGrid()
            if (stepsToLeave(newGrid).size == 0) {
                blockable.add(blockade)
            }
        }
    }

    println("day 6-2 = ${blockable.size}")
}

private fun stepsToLeave(grid: Grid<Char>): HashSet<Pair<Point, Direction>> {
    val steps = mutableListOf<Pair<Point, Direction>>()
    val seen = hashSetOf<Point>()
    val seenDirectionally = hashSetOf<Pair<Point, Direction>>()

    var location = grid.find('^')!!
    var direction = Direction.UP

    while (grid.isInBounds(location)) {
        var nextLocation = location + direction.offset

        //Rotate until a direction leads to a valid next location
        while (!grid.at(nextLocation).canWalkOn()) {
            direction = direction.rotate()
            nextLocation = location + direction.offset
        }

        //This exact spot and direction was already previously seen, so guard is looping
        if (seenDirectionally.contains(location to direction))
            return HashSet()

        seen.add(location)
        seenDirectionally.add(location to direction)
        steps.add(location to direction)
        location = nextLocation
    }

    //Return all unique points and directions visited
    return seenDirectionally
}

private fun Char?.canWalkOn() = this == null || this == '.' || this == '^'

private fun Grid<Char>.print() {
    list.forEach { println(it.joinToString("")) }
    println()
    println()
}