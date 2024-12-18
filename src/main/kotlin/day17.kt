import util.loadLines
import util.measure
import kotlin.math.abs
import kotlin.math.pow

fun main() {
    val lines = loadLines("day17.txt")

    val registers = lines.subList(0, 3).map {
        """Register .: (\d+)""".toRegex().find(it)!!.groupValues[1].toLong()
    }
    val program = lines[4].split(": ")[1].split(",")
        .map { it.toInt() }

    measure { partOne(Computer(registers.toMutableList()), program) }
    measure { partTwo(Computer(registers.toMutableList()), program) }
}

private fun partOne(computer: Computer, program: List<Int>) {
    val output = computer.runProgram(program)
    println("day 17-1 = $output")
}

private fun partTwo(computer: Computer, program: List<Int>) {
    val toFind = program.joinToString(",")

    val solution = findSolution2(computer, program, 0, toFind)

    println("day 17-2 = $solution")
}

private fun findSolution2(computer: Computer, program: List<Int>, input: Long, toFind: String, digit: Int = 0): Long? {
    if (digit >= 16) return null

    return LongRange(0, 8).mapNotNull { offset ->
        val testValue = input * 8L + offset
        val output = Computer(computer.registers.toMutableList().also { it[0] = testValue }).runProgram(program)
        if (output == toFind) return@mapNotNull testValue
        if (toFind[toFind.length - 1 - digit * 2] == output[0]) {
            return@mapNotNull findSolution2(computer, program, testValue, toFind, digit + 1)
        } else null
    }.minOrNull()
}

private const val REG_A = 0
private const val REG_B = 1
private const val REG_C = 2

private class Computer(
    val registers: MutableList<Long>,
    var pointer: Int = 0,
    val output: MutableList<Long> = mutableListOf(),
) {
    fun read(register: Int) = registers[register.toInt()]

    fun write(register: Int, value: Long) {
        registers[register] = value
    }

    fun getComboOperand(operand: Int): Long = when (operand) {
        0, 1, 2, 3 -> operand.toLong()
        4, 5, 6 -> read(operand - 4)
        else -> -1L
    }

    fun execute(opcode: Int, operand: Int) {
        val combo = getComboOperand(operand)
        when (opcode) {
            //ADV
            0 -> write(REG_A, (read(REG_A) / 2.0.pow(combo.toDouble())).toLong())
            //BXL
            1 -> write(REG_B, read(REG_B) xor operand.toLong())
            //BST
            2 -> write(REG_B, combo % 8L)
            //JNZ
            3 -> if (read(REG_A) != 0L) pointer = operand - 1
            //BXC
            4 -> write(REG_B, read(REG_B) xor read(REG_C))
            //OUT
            5 -> output.add(combo % 8)
            //BDV
            6 -> write(REG_B, (read(REG_A) / 2.0.pow(combo.toDouble())).toLong())
            //CDV
            7 -> write(REG_C, (read(REG_A) / 2.0.pow(combo.toDouble())).toLong())
        }
    }

    fun runProgram(program: List<Int>): String {
        while (pointer < program.size / 2) {
            val opcode = program[pointer * 2]
            val operand = program[pointer * 2 + 1]
            execute(opcode, operand)
            pointer++
        }
        return output.joinToString(",")
    }
}