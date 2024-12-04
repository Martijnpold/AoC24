import util.loadLines

fun main() {
    val grid = loadLines("day4.txt").map { it.toCharArray().toList() }

    partOne(grid)
    partTwo(grid)
}

private fun partOne(grid: List<List<Char>>) {
    val word = "XMAS"
    var count = 0
    grid.forEachIndexed { y, row ->
        row.forEachIndexed { x, _ ->
            Direction.entries.forEach { dir ->
                var found = ""
                for (i in word.indices) {
                    found += grid.atRelative(x, y, dir, i)
                }
                if (found == word)
                    count++
            }
        }
    }

    println("day 4-1 = $count")
}

private fun partTwo(grid: List<List<Char>>) {
    var count = 0
    grid.forEachIndexed { y, row ->
        row.forEachIndexed { x, char ->
            //Only process shapes with an A in the center
            if (char != 'A') return@forEachIndexed

            //Find corners
            val corners = DIAGONALS.associateWith { dir ->
                grid.atRelative(x, y, dir)
            }

            //Count corner occurances
            val counts = corners.values.associate({ it to corners.values.count { corner -> corner == it }})
            
            //Expect 2 M's and 2 S's, where a diagonal is not the same letters (filter out MAM or SAS)
            if(counts['M'] == 2 && counts['S'] == 2 && corners[Direction.UP_LEFT] != corners[Direction.DOWN_RIGHT]) {
                count++
            }
        }
    }

    println("day 4-2 = $count")
}

private enum class Direction(val xOff: Int, val yOff: Int) {
    UP(0, -1),
    UP_RIGHT(1, -1),
    RIGHT(1, 0),
    DOWN_RIGHT(1, 1),
    DOWN(0, 1),
    DOWN_LEFT(-1, 1),
    LEFT(-1, 0),
    UP_LEFT(-1, -1),
}

private val DIAGONALS = listOf(Direction.UP_RIGHT, Direction.DOWN_RIGHT, Direction.DOWN_LEFT, Direction.UP_LEFT)

private fun List<List<Char>>.isInBounds(x: Int, y: Int) =
    y >= 0 && y < this.size && x >= 0 && x < this[y].size

private fun List<List<Char>>.at(x: Int, y: Int) =
    if (isInBounds(x, y)) this[y][x] else '.'

private fun List<List<Char>>.atRelative(x: Int, y: Int, dir: Direction, count: Int = 1): Char {
    val atY = y + dir.yOff * count
    val atX = x + dir.xOff * count
    return this.at(atX, atY)
}