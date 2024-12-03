import util.loadLines

fun main() {
    val lines = loadLines("day2.txt")
    val series = lines.map { line -> line.split(" ").map { it.toInt() } }

    partOne(series)
    partTwo(series)
}

private fun partOne(series: List<List<Int>>) {
    val statuses = series.map { it.isValid() }

    println("day 2-1 = ${statuses.filter { it }.size}")
}

private fun partTwo(series: List<List<Int>>) {
    val statuses = series.map { it.isValid(1) }

    println("day 2-2 = ${statuses.filter { it }.size}")
}

private fun List<Int>.isIncreasing(): Boolean {
    var last = this[0]
    val score = sumOf {
        val compare = it.compareTo(last)
        last = it
        compare
    }
    return score >= 0
}

private fun List<Int>.isValid(maxErrors: Int = 0): Boolean {
    return isValid(-1, isIncreasing(), maxErrors)
}

private fun List<Int>.isValid(last: Int, increasing: Boolean, maxErrors: Int): Boolean {
    if (isEmpty()) return true

    //Return true if list would be valid if the value is skipped
    if (maxErrors > 0) {
        val validSkipped = subList(1, size).isValid(last, increasing, maxErrors - 1)
        if (validSkipped) {
            return true
        }
    }

    val current = get(0)
    val upper = last + if (increasing) 3 else -1
    val lower = last + if (increasing) 1 else -3

    val currentValid = last == -1 || current in lower..upper
    val restValid = subList(1, size).isValid(current, increasing, maxErrors)

    return currentValid && restValid
}