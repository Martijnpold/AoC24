package model

data class Grid<T>(
    val list: List<List<T>>,
) {
    fun isInBounds(point: Point) = isInBounds(point.x, point.y)

    fun isInBounds(x: Int, y: Int) =
        y >= 0 && y < list.size && x >= 0 && x < list[y].size

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
}
