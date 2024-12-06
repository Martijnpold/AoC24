import model.Direction
import model.Grid
import model.Point
import util.loadGrid
import util.measure

fun main() {
    val grid = loadGrid("day4.txt")

    measure { partOne(grid) }
    measure { partTwo(grid) }
}

private fun partOne(grid: Grid<Char>) {
    val word = "XMAS"
    var count = 0
    grid.list.forEachIndexed { y, row ->
        row.forEachIndexed { x, _ ->
            val point = Point(x, y)
            Direction.entries.forEach { dir ->
                var found = ""
                for (i in word.indices) {
                    found += grid.atRelative(point, dir, i)
                }
                if (found == word)
                    count++
            }
        }
    }

    println("day 4-1 = $count")
}

private fun partTwo(grid: Grid<Char>) {
    var count = 0
    grid.list.forEachIndexed { y, row ->
        row.forEachIndexed { x, char ->
            //Only process shapes with an A in the center
            if (char != 'A') return@forEachIndexed

            //Find corners
            val point = Point(x, y)
            val corners = DIAGONALS.associateWith { dir ->
                grid.atRelative(point, dir)
            }

            //Count corner occurances
            val counts = corners.values.associateWith { corners.values.count { corner -> corner == it } }

            //Expect 2 M's and 2 S's, where a diagonal is not the same letters (filter out MAM or SAS)
            if (counts['M'] == 2 && counts['S'] == 2 && corners[Direction.UP_LEFT] != corners[Direction.DOWN_RIGHT]) {
                count++
            }
        }
    }

    println("day 4-2 = $count")
}

private val DIAGONALS = listOf(Direction.UP_RIGHT, Direction.DOWN_RIGHT, Direction.DOWN_LEFT, Direction.UP_LEFT)

private fun Grid<Char>.atRelative(point: Point, dir: Direction, count: Int = 1): Char {
    return this.at(point + dir.offset * count) ?: '.'
}