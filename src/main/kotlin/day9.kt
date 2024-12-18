import util.loadLines
import util.measure

fun main() {
    val lines = loadLines("day9.txt")

    measure { partOne(lines) }
    measure { partTwo(lines) }
}

private fun partOne(lines: List<String>) {
    val disk = lines.first().readDisk()

    //Pop non-null values starting from the back and append them starting from the front
    var head = 0
    var tail = disk.size
    while (head < tail) {
        head = disk.findNext(head, { it == null })
        tail = disk.findNext(tail, { it != null }, -1)
        if (head > tail) break
        val popped = disk.set(tail, null)
        disk[head] = popped
    }

    val sum = disk.checkSum()
    println("day 9-1 = $sum")
}

private fun partTwo(lines: List<String>) {
    val disk = lines.first().readDisk()
    val emptyBlocks = mutableListOf<Pair<Int, Int>>()
    val filledBlocks = mutableListOf<Pair<Int, Int>>()

    //Build a list of empty blocks from front to back,
    //where key is the index and value is the size of the block
    var head = 0
    while (head != -1) {
        head = disk.findNext(head, { it == null })
        if (head == -1) break
        val tail = disk.findNext(head, { it != null })
        emptyBlocks.add(head to (tail - head))
        head = tail
    }

    //Build a list of full blocks from back to front
    //where key is the index and value is the size of the block
    head = disk.size
    while (head != -1) {
        if (head in disk.indices && disk[head] != null) head += 1
        head = disk.findNext(head, { it != null }, -1)
        if (head == -1) break
        val headValue = disk[head]
        val tail = disk.findNext(head, { it != headValue }, -1)
        filledBlocks.add(tail + 1 to (head - tail))
        head = tail
    }

    //Step through the full blocks (ordered back to front)
    //Find an empty block that is equal or bigger (front to back)
    //Insert full block into empty block
    //Update or pop empty block to reflect the remaining space
    filledBlocks.forEach { (start, size) ->
        val toPut = emptyBlocks.firstOrNull { (emptyIndex, emptySize) ->
            emptySize >= size && emptyIndex < start
        }

        val putIndex = emptyBlocks.indexOf(toPut)
        if (toPut == null) return@forEach

        disk.setBlock(toPut.first, size, disk[start])
        disk.setBlock(start, size, null)

        //If block fills up entire space remove, else update
        if(toPut.second == size) {
            emptyBlocks.removeAt(putIndex)
        } else {
            emptyBlocks[putIndex] = toPut.first + size to toPut.second - size
        }
    }

    println("day 9-2 = ${disk.checkSum()}")
}

private fun String.readDisk(): MutableList<Int?> {
    val disk = mutableListOf<Int?>()
    forEachIndexed { index, char ->
        val blockId = if (index % 2 == 0) index / 2 else null
        repeat("$char".toInt()) {
            disk.add(blockId)
        }
    }
    return disk
}

private fun List<Int?>.checkSum(): Long {
    var sum = 0L
    forEachIndexed { index, value ->
        if (value != null)
            sum += index * value
    }
    return sum
}

private fun <T> MutableList<T>.setBlock(index: Int, length: Int, value: T) {
    repeat(length) {
        this[index + it] = value
    }
}

private fun <T> List<T>.findNext(current: Int, block: (T) -> Boolean, direction: Int = 1): Int {
    var next = current + direction
    while (next in indices && !block(this[next])) next += direction
    return if (next in indices) next else -1
}