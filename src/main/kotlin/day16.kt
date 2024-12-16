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

    println("day 16-2 = 0")
}

private fun reconstructPath(cameFrom: Map<Location, Location>, end: Location): Route {
    var current = end
    val totalPath = mutableListOf(end)
    while (cameFrom.containsKey(current)) {
        current = cameFrom[current]!!
        totalPath.addFirst(current)
    }
    return Route(totalPath)
}

// A* finds a path from start to goal.
// h is the heuristic function. h(n) estimates the cost to reach goal from node n.
private fun aStar(grid: Grid<Char>, start: Location, goal: Point): List<Route> {
    val routes = mutableListOf<Route>()
    var routeCost: Int? = null
    // The set of discovered nodes that may need to be (re-)expanded.
    // Initially, only the start node is known.
    // This is usually implemented as a min-heap or priority queue rather than a hash-set.
    val openSet = mutableListOf(start)

    // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from the start
    // to n currently known.
    val cameFrom = mutableMapOf<Location, Location>()

    // For node n, gScore[n] is the currently known cost of the cheapest path from start to n.
    val gScore = mutableMapOf<Location, Double>()
    gScore[start] = 0.0

    // For node n, fScore[n] := gScore[n] + h(n). fScore[n] represents our current best guess as to
    // how cheap a path could be from start to finish if it goes through n.
    val fScore = mutableMapOf<Location, Double>()
    fScore[start] = heuristic(start, goal)

    while (openSet.isNotEmpty()) {
        // This operation can occur in O(Log(N)) time if openSet is a min-heap or a priority queue
        val current = openSet.minBy { fScore[it] ?: 99999999999.0 }
        fScore.remove(current)
        openSet.remove(current)

        if (current.point == goal) {
            val route = reconstructPath(cameFrom, current)
            if (routeCost == null) routeCost = route.getCost()
            if (route.getCost() > routeCost) return routes
            routes.add(route)
            continue
        }

        current.getNeighbors().forEach { neighbor ->
            if (grid.at(neighbor.point) == '#') return@forEach
            // d(current,neighbor) is the weight of the edge from current to neighbor
            // tentative_gScore is the distance from start to the neighbor through current
            val tentative = (gScore[current] ?: 9999999999.0) + moveCost(current, neighbor)

            if (tentative <= (gScore[neighbor] ?: 9999999999.0)) {
                // This path to neighbor is better than any previous one. Record it!
                cameFrom[neighbor] = current
                gScore[neighbor] = tentative
                fScore[neighbor] = tentative + heuristic(current, neighbor.point)
                if (!openSet.contains(neighbor))
                    openSet.add(neighbor)
            }
        }

        // Open set is empty but goal was never reached
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