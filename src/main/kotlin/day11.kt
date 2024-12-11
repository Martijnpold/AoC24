import util.hasIndex
import util.loadLines
import util.measure

fun main() {
    val stones = loadLines("day11.txt")
        .first().split(" ")
        .map { it.toLong() }

    measure { partOne(stones) }
    measure { partTwo(stones) }
}

private fun partOne(stones: List<Long>) {
    val cache = mutableMapOf<Pair<Long, Int>, Long>()
    val sum = stones.sumOf { blink(cache, it, 25) }

    println("day 11-1 = $sum")
}

private fun partTwo(stones: List<Long>) {
    val cache = mutableMapOf<Pair<Long, Int>, Long>()
    val sum = stones.sumOf { blink(cache, it, 75) }

    println("day 11-2 = $sum")
}

private fun blink(cache: MutableMap<Pair<Long, Int>, Long>, stone: Long, times: Int): Long {
    if (times == 0) return 1

    if (cache.containsKey(stone to times)) {
        return cache[stone to times]!!
    }

    val result = if (stone == 0L) {
        blink(cache, 1, times - 1)
    } else if (stone.toString().length % 2 == 0) {
        val valueS = "$stone"
        val left = valueS.substring(0, valueS.length / 2).toLong()
        val right = valueS.substring(valueS.length / 2, valueS.length).toLong()
        blink(cache, left, times - 1) + blink(cache, right, times - 1)
    } else {
        blink(cache, stone * 2024, times - 1)
    }

    cache[stone to times] = result
    return result
}