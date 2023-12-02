import java.io.File

class Day2 {
    data class Grab(val red: Int, val green: Int, val blue: Int)

    fun runPart1(): Int {
        return File("input/day2.txt")
            .readText()
            .lines()
            .filter { it.isNotEmpty() }
            .sumOf { line ->
                // Parse input
                val gameId = line.substringBefore(":").substringAfter(" ").toInt()
                val games = line.substringAfter(":").split("; ")
                    .map { gameString ->
                        val red = gameString.substringBefore(" red").substringAfterLast(" ").toIntOrNull() ?: 0
                        val green = gameString.substringBefore(" green").substringAfterLast(" ").toIntOrNull() ?: 0
                        val blue = gameString.substringBefore(" blue").substringAfterLast(" ").toIntOrNull() ?: 0
                        return@map Grab(red, green, blue)
                    }

                // Check if "possible game"
                return@sumOf if (games.all { it.red <= 12 } && games.all { it.green <= 13 } && games.all { it.blue <= 14 }) {
                    gameId
                } else {
                    0
                }
            }
    }

    fun runPart2(): Int {
        return File("input/day2.txt")
            .readText()
            .lines()
            .filter { it.isNotEmpty() }
            .sumOf { line ->
                // Parse input
                val games = line.substringAfter(":").split("; ")
                    .map { gameString ->
                        val red = gameString.substringBefore(" red").substringAfterLast(" ").toIntOrNull() ?: 0
                        val green = gameString.substringBefore(" green").substringAfterLast(" ").toIntOrNull() ?: 0
                        val blue = gameString.substringBefore(" blue").substringAfterLast(" ").toIntOrNull() ?: 0
                        return@map Grab(red, green, blue)
                    }

                return@sumOf games.maxOf { it.red } * games.maxOf { it.green } * games.maxOf { it.blue }
            }
    }
}