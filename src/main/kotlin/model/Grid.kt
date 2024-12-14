package model

import util.asGrid
import java.io.FileWriter

data class Grid<T>(
    val list: MutableList<MutableList<T>>,
) {
    fun isInBounds(point: Point) = isInBounds(point.x, point.y)

    fun isInBounds(x: Int, y: Int) =
        y >= 0 && y < list.size && x >= 0 && x < list[y].size

    fun set(point: Point, value: T) = set(point.x, point.y, value)

    fun set(x: Int, y: Int, value: T) {
        if (isInBounds(x, y)) {
            list[y][x] = value
        }
    }

    fun at(point: Point) = at(point.x, point.y)

    fun at(x: Int, y: Int) =
        if (isInBounds(x, y)) list[y][x] else null

    fun find(value: T): Point? {
        list.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, col ->
                if (col == value) return Point(colIndex, rowIndex)
            }
        }
        return null
    }

    fun findFirstMatching(block: (Point, T) -> Boolean): Point? {
        list.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, col ->
                val point = Point(colIndex, rowIndex)
                if (block(point, col)) return point
            }
        }
        return null
    }

    fun findAllMatching(block: (T) -> Boolean): List<Point> {
        return list.flatMapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, col ->
                if (block(col)) Point(colIndex, rowIndex) else null
            }
        }.filterNotNull()
    }

    fun print() {
        list.forEach {
            println(it.joinToString(""))
        }
    }

    fun write(fileWriter: FileWriter) {
        list.forEach {
            fileWriter.write(it.joinToString(""))
            fileWriter.write("\n")
        }
    }
}

fun <T> makeGrid(width: Int, height: Int, init: (Point) -> T): Grid<T> =
    List(width) { y ->
        List(height) { x ->
            init(Point(x, y))
        }
    }.asGrid()
