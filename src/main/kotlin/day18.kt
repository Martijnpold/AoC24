import model.Grid
import model.Point
import model.STRAIGHTS
import model.makeGrid
import util.loadLines
import util.measure

fun main() {
    val lines = loadLines("day18.txt")
        .map { it.split(",") }
        .map { Point(it[0].toInt(), it[1].toInt()) }

    measure { partOne(lines) }
    measure { partTwo(lines) }
}

private fun partOne(bytes: List<Point>) {
    val grid = makeGrid(71, 71) { '.' }

    bytes.subList(0, 1024).forEach { grid.set(it, '#') }
    val route = aStar(grid, Point(0, 0), Point(grid.width() - 1, grid.height() - 1))

    println("day 18-1 = ${route.size - 1}")
}

private fun partTwo(bytes: List<Point>) {
    val grid = makeGrid(71, 71) { '.' }

    val start = Point(0, 0)
    val goal = Point(grid.width() - 1, grid.height() - 1)
    var lastRoute = aStar(grid, start, goal).toHashSet()

    val index = IntRange(0, bytes.size - 1).first {
        grid.set(bytes[it], '#')
        if (lastRoute.contains(bytes[it])) {
            //Recalculate route if the newly placed byte blocks the last calculated route
            lastRoute = aStar(grid, start, goal).toHashSet()
        }
        lastRoute.isEmpty()
    }

    println("day 18-2 = ${bytes[index]}")
}

private fun reconstructPath(cameFrom: Map<Point, Point>, end: Point): List<Point> {
    var current = end
    val totalPath = mutableListOf(end)
    while (cameFrom.containsKey(current)) {
        current = cameFrom[current]!!
        totalPath.addFirst(current)
    }
    return totalPath
}

private fun aStar(grid: Grid<Char>, start: Point, goal: Point): List<Point> {
    val openSet = mutableListOf(start)
    val cameFrom = mutableMapOf<Point, Point>()
    val gScore = mutableMapOf<Point, Double>()
    val fScore = mutableMapOf<Point, Double>()

    gScore[start] = 0.0
    fScore[start] = heuristic(start, goal)

    while (openSet.isNotEmpty()) {
        val current = openSet.minBy { fScore[it] ?: 99999999999.0 }
        fScore.remove(current)
        openSet.remove(current)

        if (current == goal) {
            return reconstructPath(cameFrom, current)
        }

        STRAIGHTS.forEach { direction ->
            val neighbor = current + direction.offset
            if (!grid.isInBounds(neighbor)) return@forEach
            if (grid.at(neighbor) == '#') return@forEach

            val tentative = (gScore[current] ?: 9999999999.0) + moveCost(current, neighbor)

            if (tentative <= (gScore[neighbor] ?: 9999999999.0)) {
                cameFrom[neighbor] = current
                gScore[neighbor] = tentative
                fScore[neighbor] = tentative + heuristic(current, neighbor)
                if (!openSet.contains(neighbor))
                    openSet.add(neighbor)
            }
        }
    }
    return emptyList()
}

private fun heuristic(current: Point, goal: Point) = current.distance(goal)

private fun moveCost(from: Point, to: Point) = from.distance(to)