import util.loadLines
import kotlin.math.abs

fun main() {
    val lines = loadLines("day1.txt")
    val sets = lines.map { it.split("   ") }

    val setOne = sets.map { it[0].toInt() }.sorted()
    val setTwo = sets.map { it[1].toInt() }.sorted()

    partOne(setOne, setTwo)
    partTwo(setOne, setTwo)
}

private fun partOne(setOne: List<Int>, setTwo: List<Int>) {
    val sum = setOne.mapIndexed { index, one ->
        val two = setTwo[index]
        abs(one - two)
    }.sum()

    println("day 1-1 = $sum")
}

private fun partTwo(setOne: List<Int>, setTwo: List<Int>) {
    var indexTwo = 0

    val similarity = setOne.mapIndexed { index, one ->
        var similarity = 0
        //Skip until numbers are greater or match
        while(setTwo[indexTwo] < one) indexTwo++
        //Count instances of same number
        while(setTwo[indexTwo] == one) {
            similarity += one
            indexTwo++
        }
        similarity
    }.sum()

    println("day 1-2 = $similarity")
}