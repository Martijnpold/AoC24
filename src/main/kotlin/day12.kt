import model.Direction
import model.Grid
import model.Point
import model.STRAIGHTS
import util.loadGrid
import util.measure

fun main() {
    val grid = loadGrid("day12.txt")

    measure { partOne(grid) }
    measure { partTwo(grid) }
}

private fun partOne(grid: Grid<Char>) {
    val regions = scanRegions(grid)

    println("day 12-1 = ${regions.sumOf { it.cost() }}")
}

private fun partTwo(grid: Grid<Char>) {
    val regions = scanRegions(grid)

    println("day 12-2 = ${regions.sumOf { it.complexCost() }}")
}

private fun scanRegions(
    grid: Grid<Char>,
): List<Region> {
    val regions = mutableListOf<Region>()
    val found = HashSet<Point>()
    var current: Point? = Point(0, 0)

    while(current != null) {
        val region = scanRegion(grid, current)
        regions.add(region)
        found.addAll(region.tiles)
        current = grid.findFirstMatching { point, c -> !found.contains(point) }
    }

    return regions
}

private fun scanRegion(
    grid: Grid<Char>,
    point: Point,
    region: Region = Region(grid.at(point)!!),
): Region {
    region.tiles.add(point)

    STRAIGHTS.forEach { dir ->
        val nPoint = point + dir.offset
        if (region.tiles.contains(nPoint)) return@forEach
        val nId = grid.at(nPoint) ?: '.'
        if (nId == region.id) {
            scanRegion(grid, nPoint, region)
        } else {
            region.borders.add(point to dir)
        }
    }

    return region
}

private class Region(
    val id: Char,
    val tiles: HashSet<Point> = HashSet(),
    val borders: HashSet<Pair<Point, Direction>> = HashSet(),
) {
    fun cost() = tiles.size * borders.size

    fun complexCost() = tiles.size * borders.filter { !borders.contains(it.getParent()) }.size
}

private val PARENT_DIRECTIONS = mapOf(
    Direction.UP to Direction.LEFT,
    Direction.DOWN to Direction.LEFT,
    Direction.LEFT to Direction.UP,
    Direction.RIGHT to Direction.UP,
)

private fun Pair<Point, Direction>.getParent() = first + PARENT_DIRECTIONS[second]!!.offset to second
