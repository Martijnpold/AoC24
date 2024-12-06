import util.loadLines
import util.measure

fun main() {
    val lines = loadLines("day3.txt")

    measure { partOne(lines) }
    measure { partTwo(lines) }
}

private fun partOne(lines: List<String>) {
    val regex = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()

    val result = lines.sumOf { line ->
        regex.findAll(line).sumOf { match ->
            match.groups[1]!!.value.toInt() * match.groups[2]!!.value.toInt()
        }
    }

    println("day 3-1 = $result")
}

private fun partTwo(lines: List<String>) {
    val regex = """(do\(\)|don't\(\)|mul\((\d{1,3}),(\d{1,3})\))""".toRegex()

    var enabled = true
    val result = lines.sumOf { line ->
        regex.findAll(line).sumOf { match ->
            when (match.groupValues[1]) {
                "don't()" -> enabled = false
                "do()" -> enabled = true
                else -> {
                    if (enabled) return@sumOf match.groups[2]!!.value.toInt() * match.groups[3]!!.value.toInt()
                }
            }
            0
        }
    }

    println("day 3-2 = $result")
}