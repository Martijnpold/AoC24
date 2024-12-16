import model.Direction
import model.Grid
import model.Point
import model.STRAIGHTS
import util.loadGrid
import util.measure

fun main() {
    val grid = loadGrid("day16.txt")

    measure { partOne(grid) }
    measure { partTwo(grid) }
}

private fun partOne(grid: Grid<Char>) {
    val start = grid.find('S')!!
    val end = grid.find('E')!!

    val path = aStar(grid, Location(start, Direction.RIGHT), end)
    val sum = path.first().getCost()

    println("day 16-1 = $sum")
}

private fun partTwo(grid: Grid<Char>) {
    val start = grid.find('S')!!
    val end = grid.find('E')!!

    val path = aStar(grid, Location(start, Direction.RIGHT), end)
    val sum = path.flatMap { it.path }.map { it.point }.distinct().size

    println("day 16-2 = $sum")
}

private fun reconstructPaths(
    cameFrom: Map<Location, List<Location>>,
    current: Location
): List<Route> {
    if (!cameFrom.containsKey(current)) return listOf(Route(listOf(current)))

    val paths = mutableListOf<Route>()
    for (predecessor in cameFrom[current]!!) {
        for (path in reconstructPaths(cameFrom, predecessor)) {
            paths.add(Route(listOf(current) + path.path))
        }
    }
    return paths
}

// A* finds a path from start to goal.
// h is the heuristic function. h(n) estimates the cost to reach goal from node n.
private fun aStar(grid: Grid<Char>, start: Location, goal: Point): List<Route> {
    val routes = mutableListOf<Route>()
    var routeCost: Int? = null
    val openSet = mutableListOf(start)
    val cameFrom = mutableMapOf<Location, MutableList<Location>>()
    val gScore = mutableMapOf<Location, Double>()
    gScore[start] = 0.0
    val fScore = mutableMapOf<Location, Double>()
    fScore[start] = heuristic(start, goal)

    while (openSet.isNotEmpty()) {
        val current = openSet.minBy { fScore[it] ?: Double.MAX_VALUE }
        openSet.remove(current)

        if (current.point == goal) {
            if (routeCost == null) routeCost = gScore[current]?.toInt()
            if (gScore[current]?.toInt() == routeCost) {
                routes.addAll(reconstructPaths(cameFrom, current))
            }
            continue
        }

        current.getNeighbors().forEach { neighbor ->
            if (grid.at(neighbor.point) == '#') return@forEach
            val tentative = (gScore[current] ?: Double.MAX_VALUE) + moveCost(current, neighbor)

            if (tentative < (gScore[neighbor] ?: Double.MAX_VALUE)) {
                gScore[neighbor] = tentative
                fScore[neighbor] = tentative + heuristic(current, goal)
                openSet.add(neighbor)
                cameFrom[neighbor] = mutableListOf(current)
            } else if (tentative == (gScore[neighbor] ?: Double.MAX_VALUE)) {
                cameFrom.computeIfAbsent(neighbor) { mutableListOf() }.add(current)
            }
        }
    }

    return routes
}

private fun Location.getNeighbors() = listOf(
    Location(point, direction.rotate(2)),
    Location(point, direction.rotate(2 * 3)),
    Location(point + direction.offset, direction)
)

private fun heuristic(current: Location, goal: Point) = current.point.distance(goal)

private fun moveCost(from: Location, to: Location) =
    from.point.distance(to.point) + rotationCost(from, to)

private fun rotationCost(from: Location, to: Location) = if (from.direction != to.direction) 1000 else 0

private fun Point.directionTo(other: Point) =
    (other - this).let { diff ->
        STRAIGHTS.first { it.offset == diff }
    }

private data class Location(
    val point: Point,
    val direction: Direction,
)

private data class Route(
    val path: List<Location>
) {
    fun getCost(): Int {
        var last = path[0]
        return path.mapIndexed { index, location ->
            if (index == 0) return@mapIndexed 0
            val cost = moveCost(last, location).toInt()
            last = location
            cost
        }.sum()
    }
}