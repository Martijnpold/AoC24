import model.Grid
import model.Point
import util.loadGrid
import util.measure


fun main() {
    val lines = loadGrid("day8.txt")
    val antennas = lines.findAllMatching { it != '.' }
        .groupBy { lines.at(it)!! }

    measure { partOne(lines, antennas) }
    measure { partTwo(lines, antennas) }
}

private fun partOne(grid: Grid<Char>, antennas: Map<Char, List<Point>>) {
    val antinodes = HashSet<Point>()

    antennas.forEach { char, points ->
        points.forEachIndexed { toIndex, toPoint ->
            points.forEachIndexed { fromIndex, fromPoint ->
                if (fromIndex == toIndex) return@forEachIndexed
                val diff = fromPoint - toPoint
                if (grid.isInBounds(fromPoint + diff)) {
                    antinodes.add(fromPoint + diff)
                }
                if (grid.isInBounds(toPoint - diff)) {
                    antinodes.add(toPoint - diff)
                }
            }
        }
    }

    println("day 8-1 = ${antinodes.size}")
}

private fun partTwo(grid: Grid<Char>, antennas: Map<Char, List<Point>>) {
    val antinodes = HashSet<Point>()

    antennas.forEach { char, points ->
        points.forEachIndexed { toIndex, toPoint ->
            points.forEachIndexed { fromIndex, fromPoint ->
                if (fromIndex == toIndex) return@forEachIndexed
                var current = fromPoint
                val diff = fromPoint - toPoint
                while(grid.isInBounds(current + diff)) current += diff
                while(grid.isInBounds(current)) {
                    antinodes.add(current)
                    current -= diff
                }
            }
        }
    }

    println("day 8-2 = ${antinodes.size}")
}