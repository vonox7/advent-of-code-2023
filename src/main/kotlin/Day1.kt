import java.io.File

class Day1 {
    fun runPart1(): Int {
        return File("input/day1.txt")
            .readText()
            .lines()
            .filter { it.isNotEmpty() }
            .sumOf { line ->
                (line.first { it.isDigit() }.toString() + line.last { it.isDigit() }.toString()).toInt()
            }
    }

    fun runPart2(): Int {
        val spelledOutDigits = mapOf(
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "seven" to "7",
            "eight" to "8",
            "nine" to "9"
        )
        val allDigits: Set<String> = spelledOutDigits.keys + spelledOutDigits.values

        return File("input/day1.txt")
            .readText()
            .lines()
            .filter { it.isNotEmpty() }
            .sumOf { line ->
                val first = line.findAnyOf(allDigits.toList())!!.second
                // The string might end with "eighthree", in that case we want to find "three", so use findLastAnyOf()
                val last = line.findLastAnyOf(allDigits.toList())!!.second

                // Make sure to replace "one" with "1", etc.
                ((spelledOutDigits[first] ?: first) + (spelledOutDigits[last] ?: last)).toInt()
            }
    }
}