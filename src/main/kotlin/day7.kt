import util.loadLines
import util.measure

fun main() {
    val lines = loadLines("day7.txt")
        .map { it.split(": ") }
        .map {
            it.flatMap { it.split(" ") }
                .map { it.toLong() }
        }

    measure { partOne(lines) }
    measure { partTwo(lines) }
}

private fun partOne(lines: List<List<Long>>) {
    val possible = lines.filter {
        val target = it[0]
        val numbers = it.subList(2, it.size)
        find(target, numbers, it[1], listOf(Operator.PLUS, Operator.MUL))
    }.sumOf { it[0] }

    println("day 7-1 = $possible")
}

private fun partTwo(lines: List<List<Long>>) {
    val possible = lines.filter {
        val target = it[0]
        val numbers = it.subList(2, it.size)
        find(target, numbers, it[1], Operator.entries)
    }.sumOf { it[0] }

    println("day 7-2 = $possible")
}

private fun find(target: Long, numbers: List<Long>, current: Long, operators: List<Operator>): Boolean {
    if (current > target) return false
    if (numbers.isEmpty()) return target == current

    return operators.any {
        val newCurrent = it.execute(current, numbers[0])
        val newNumbers = numbers.subList(1, numbers.size)
        find(target, newNumbers, newCurrent, operators)
    }
}

private enum class Operator(val block: (Long, Long) -> Long) {
    MUL({ one, two -> one * two }),
    PLUS({ one, two -> one + two }),
    COMBINE({ one, two -> "$one$two".toLong() }),
    ;

    fun execute(one: Long, two: Long) = block(one, two)
}