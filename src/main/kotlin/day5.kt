import util.loadLines
import util.measure

fun main() {
    val lines = loadLines("day5.txt")
    val split = lines.indexOf("")
    val rules = lines.subList(0, split).map { it.split("|") }.map { it[0] to it[1] }
    val updates = lines.subList(split + 1, lines.size).map { it.split(",") }

    measure { partOne(updates, rules) }
    measure { partTwo(updates, rules) }
}

private fun partOne(updates: List<List<String>>, rules: List<Pair<String, String>>) {
    //Return only sequences where no broken rule is found
    val correct = updates.filter { seq ->
        val broken = seq.findBrokenRule(rules)
        broken == null
    }

    var sum = 0
    correct.forEach { sum += it[it.size / 2].toInt() }

    println("day 5-1 = $sum")
}

private fun partTwo(updates: List<List<String>>, rules: List<Pair<String, String>>) {
    val incorrect = updates.mapNotNull { seq ->
        val seqNew = seq.toMutableList()
        var changed = false

        //As long as a rule is broken, try to fix the rule and continue
        var broken = seq.findBrokenRule(rules)
        while (broken != null) {
            val firstIndex = seqNew.indexOf(broken.first)
            val secondIndex = seqNew.indexOf(broken.second)
            seqNew[firstIndex] = broken.second
            seqNew[secondIndex] = broken.first
            changed = true
            broken = seqNew.findBrokenRule(rules)
        }

        //If a sequence was changed to fix a rule, return
        if (changed) seqNew else null
    }

    var sum = 0
    incorrect.forEach { sum += it[it.size / 2].toInt() }

    println("day 5-2 = $sum")
}

private fun List<String>.findBrokenRule(rules: List<Pair<String, String>>): Pair<String, String>? {
    var seen = mutableListOf<String>()
    forEach { value ->
        val applicable = rules.findApplicableRules(this, value)
        applicable.forEach { rule ->
            if (seen.contains(rule.second)) {
                return rule
            }
        }
        seen.add(value)
    }
    return null
}

private fun List<Pair<String, String>>.findApplicableRules(seq: List<String>, value: String) =
    this.filter { it.first == value && seq.contains(it.second) }