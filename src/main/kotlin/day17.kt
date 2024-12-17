import util.loadLines
import util.measure
import kotlin.math.abs
import kotlin.math.pow

fun main() {
    val lines = loadLines("day17.txt")

    val registers = lines.subList(0, 3).map {
        """Register .: (\d+)""".toRegex().find(it)!!.groupValues[1].toInt()
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

    val solution = findSolution(computer, program, 0, 16, toFind)

    println("day 17-2 = $solution")
}

private fun findSolution(computer: Computer, program: List<Int>, offset: Int, digit: Int, toFind: String): Int? {
    if (digit < 0) return null
    val increment = 8.0.pow(digit).toInt()
    val charIndex = abs(digit - 16) * 2

    return IntRange(0, 8).mapNotNull {
        val value = offset + increment * it
        val newComputer = Computer(computer.registers)
        newComputer.write(REG_A, value)

        val output = newComputer.runProgram(program)
        if (output == toFind) return@mapNotNull value

        val paddedOutput = output.padEnd(32, 'x')
        if (paddedOutput[charIndex] == toFind[charIndex]) {
            return@mapNotNull findSolution(computer, program, value, digit - 1, toFind)
        }

        return@mapNotNull null
    }.firstOrNull()
}

private const val REG_A = 0
private const val REG_B = 1
private const val REG_C = 2

private class Computer(
    val registers: MutableList<Int>,
    var pointer: Int = 0,
    val output: MutableList<Int> = mutableListOf(),
) {
    fun read(register: Int) = registers[register]

    fun write(register: Int, value: Int) {
        registers[register] = value
    }

    fun getComboOperand(operand: Int) = when (operand) {
        0, 1, 2, 3 -> operand
        4, 5, 6 -> read(operand - 4)
        else -> -1
    }

    fun execute(opcode: Int, operand: Int) {
        val combo = getComboOperand(operand)
        when (opcode) {
            //ADV
            0 -> write(REG_A, (read(REG_A) / 2.0.pow(combo)).toInt())
            //BXL
            1 -> write(REG_B, read(REG_B) xor operand)
            //BST
            2 -> write(REG_B, combo % 8)
            //JNZ
            3 -> if (read(REG_A) != 0) pointer = operand - 1
            //BXC
            4 -> write(REG_B, read(REG_B) xor read(REG_C))
            //OUT
            5 -> output.add(combo % 8)
            //BDV
            6 -> write(REG_B, (read(REG_A) / 2.0.pow(combo)).toInt())
            //CDV
            7 -> write(REG_C, (read(REG_A) / 2.0.pow(combo)).toInt())
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