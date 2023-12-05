import java.io.File

class Day5 {
    fun run(part2: Boolean): Long {
        data class MapEntry(
            val source: String,
            val destination: String,
            val sourceStart: Long,
            val destinationStart: Long,
            val rangeLength: Long
        )
        val mapEntries = mutableListOf<MapEntry>()

        var seeds: List<Long> = emptyList()

        var currentMap = "" to ""  // Start to end

        File("input/day5.txt")
            .readText()
            .lines()
            .forEach { line ->
                when {
                    line.startsWith("seeds: ") -> {
                        seeds = line.substringAfter("seeds: ").split(" ").map { it.toLong() }
                    }
                    line.endsWith(" map:") -> {
                        currentMap = line.substringBefore("-") to line.substringAfter("-to-").substringBefore(" ")
                    }
                    line.isEmpty() -> Unit // Skip
                    else -> {
                        val mapEntryString = line.split(" ").map { it.toLong() }
                        mapEntries += MapEntry(
                            source = currentMap.first,
                            destination = currentMap.second,
                            destinationStart = mapEntryString[0],
                            sourceStart = mapEntryString[1],
                            rangeLength = mapEntryString[2]
                        )
                    }
                }
            }

        fun getMapEntry(source: String, number: Long): MapEntry {
            // O(n), but we don't care for such small list lengths
            return mapEntries.singleOrNull { it.source == source && it.sourceStart <= number && it.sourceStart + it.rangeLength > number } // Mapped
                ?: mapEntries.first { it.source == source }.copy(sourceStart = number, destinationStart = number, rangeLength = 1) // Unmapped
        }

        fun resolve(source: String, number: Long): Long {
            val mapEntry = getMapEntry(source, number)
            val destinationNumber = mapEntry.destinationStart + number - mapEntry.sourceStart
            return if (mapEntry.destination == "location") {
                destinationNumber
            } else {
                resolve(mapEntry.destination, destinationNumber)
            }
        }

        return seeds.minOf { resolve("seed", it) }
    }
}