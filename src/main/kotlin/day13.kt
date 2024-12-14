import util.loadLines
import util.measure
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun main() {
    val machines = mutableListOf<ClawMachine>()

    var buttons = mutableListOf<LongPoint>()
    loadLines("day13.txt").forEach { line ->
        if (line.startsWith("Button")) {
            """X\+(\d+), Y\+(\d+)""".toRegex().find(line)!!.let { match ->
                val point = LongPoint(match.groupValues[1].toLong(), match.groupValues[2].toLong())
                buttons.add(point)
            }
        }
        if (line.startsWith("Prize")) {
            """X=(\d+), Y=(\d+)""".toRegex().find(line)!!.let { match ->
                val point = LongPoint(match.groupValues[1].toLong(), match.groupValues[2].toLong())
                machines.add(ClawMachine(point, buttons))
            }
            buttons = mutableListOf()
        }
    }


    measure { partOne(machines) }
    measure { partTwo(machines) }
}

private fun partOne(machines: List<ClawMachine>) {
    var sum = machines.mapNotNull {
        calculateNaive(it.prize, it.buttons[0], it.buttons[1])
    }.sum()
    println("day 13-1 = $sum")
}

private fun partTwo(machines: List<ClawMachine>) {
    var sum = machines.mapNotNull {
        val offset = LongPoint(10000000000000, 10000000000000)
        val cost = solveForCoefficients(it.buttons[0], it.buttons[1] * 3, it.prize + offset, Long.MAX_VALUE) ?: return@mapNotNull null
        cost.first * 3 + cost.second
    }.sum()
    println("day 13-2 = $sum")
}

private fun calculateNaive(goal: LongPoint, buttonA: LongPoint, buttonB: LongPoint, maxPresses: Long = 100): Long? {
    val zero = LongPoint(0, 0)
    var currentCost: Long? = null

    val maxPressA = maxEstimatePresses(goal, buttonA).let { max(it.x, it.y) }.coerceAtMost(maxPresses)
    repeat(maxPressA.toInt()) { pressA ->
        val leftover = goal - buttonA * pressA.toLong()
        val pressB = maxEstimatePresses(leftover, buttonB).let { max(it.x, it.y) }.coerceAtMost(maxPresses)

        val result = goal - buttonA * pressA.toLong() - buttonB * pressB
        if (result == zero) {
            currentCost = min(pressA * 3 + pressB, currentCost ?: 99999999)
        }
    }
    return currentCost
}

private fun maxEstimatePresses(goal: LongPoint, button: LongPoint) =
    LongPoint(
        ceil(goal.x.toFloat() / button.x.toFloat()).toLong(),
        ceil(goal.y.toFloat() / button.y.toFloat()).toLong(),
    )

private data class ClawMachine(
    val prize: LongPoint,
    val buttons: List<LongPoint>,
)

private fun solveForCoefficients(v1: LongPoint, v2: LongPoint, v3: LongPoint, maxPresses: Long = 100): Pair<Long, Long>? {
    // Calculate the determinant of the matrix
    val determinant = (v1.x * v2.y - v2.x * v1.y).toDouble()

    if (abs(determinant) < 1e-10) {
        // Matrix is singular, no unique solution
        return null
    }

    // Solve using Cramer's rule
    val aCoefficient = (v3.x * v2.y - v2.x * v3.y) / determinant
    val bCoefficient = (v1.x * v3.y - v3.x * v1.y) / determinant * 3

    if (aCoefficient.toLong() - aCoefficient != 0.0) return null
    if (bCoefficient.toLong() - bCoefficient != 0.0) return null
    if (aCoefficient > maxPresses || bCoefficient > maxPresses) return null
    return Pair(aCoefficient.toLong(), bCoefficient.toLong())
}

private data class LongPoint(val x: Long, val y: Long) {
    operator fun plus(other: LongPoint) = LongPoint(x + other.x, y + other.y)

    operator fun minus(other: LongPoint) = LongPoint(x - other.x, y - other.y)

    operator fun times(other: Long) = LongPoint(x * other, y * other)

    fun distance() = sqrt((x * x + y * y).toFloat())
}