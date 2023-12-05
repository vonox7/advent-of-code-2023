import java.io.File

class Day5 {
    data class MapEntry(
        val source: String,
        val destination: String,
        val sourceStart: Long,
        val destinationStart: Long,
        val rangeLength: Long
    ) {
        val sourceRange = LongRange(sourceStart, sourceStart + rangeLength - 1)
    }

    fun runPart1(): Long {
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

    fun runPart2(): Long {
        val mapEntries = mutableListOf<MapEntry>()

        var seeds: List<LongRange> = emptyList()

        var currentMap = "" to ""  // Start to end

        File("input/day5.txt")
            .readText()
            .lines()
            .forEach { line ->
                when {
                    line.startsWith("seeds: ") -> {
                        seeds = line.substringAfter("seeds: ")
                            .split(" ")
                            .map { it.toLong() }
                            .chunked(2)
                            .map { LongRange(it[0], it[0] + it[1] - 1) }
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

        fun LongRange.intersect(other: LongRange): LongRange {
            return LongRange(maxOf(this.first, other.first), minOf(this.last, other.last))
        }

        fun getMapEntries(source: String, range: LongRange): Map<MapEntry, LongRange> {
            val relevantMapEntries = mapEntries.filter { it.source == source }.sortedBy { it.sourceStart }
            val targetMapEntries = relevantMapEntries.toMutableList()
            var lastEnd = -1L
            relevantMapEntries.forEach { mapEntry ->
                if (mapEntry.sourceStart != lastEnd + 1) {
                    // Add unmapped gap
                    targetMapEntries += relevantMapEntries.first().copy(
                        sourceStart = lastEnd + 1,
                        destinationStart = lastEnd + 1,
                        rangeLength = mapEntry.sourceStart - lastEnd
                    )
                }
                lastEnd = mapEntry.sourceRange.last
            }
            // Add last unmapped (until Long.MAX_VALUE)
            targetMapEntries += relevantMapEntries.first()
                .copy(sourceStart = lastEnd + 1, destinationStart = lastEnd + 1, rangeLength = Long.MAX_VALUE - lastEnd)

            return targetMapEntries.filter { !it.sourceRange.intersect(range).isEmpty() }
                .associateWith { mapEntry ->
                    val offset = mapEntry.destinationStart - mapEntry.sourceStart
                    val sourceIntersectedRange = mapEntry.sourceRange.intersect(range)
                    LongRange(sourceIntersectedRange.first + offset, sourceIntersectedRange.last + offset)
                }
        }

        fun resolve(source: String, range: LongRange): List<LongRange> {
            val resolvedMapEntries = getMapEntries(source, range)
            return resolvedMapEntries.flatMap { (mapEntry, range) ->
                if (resolvedMapEntries.keys.first().destination == "location") {
                    listOf(range)
                } else {
                    resolve(mapEntry.destination, range)
                }
            }
        }

        return seeds.minOf { seed -> resolve("seed", seed).minBy { it.first }.first }
    }
}