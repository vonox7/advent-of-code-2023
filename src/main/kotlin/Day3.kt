import java.io.File

class Day3 {
    sealed class Character(val x: Int, val y: Int)
    class Symbol(x: Int, y: Int, val char: Char, val attachedPartNumbers: MutableList<Int> = mutableListOf()) :
        Character(x, y)

    class Empty(x: Int, y: Int) : Character(x, y)
    class Digit(x: Int, y: Int, val digit: Int, var attachedSymbol: Symbol? = null) : Character(x, y)

    fun run(part2: Boolean): Int {
        // Parse schematic
        val schematic: List<List<Character>> = File("input/day3.txt")
            .readText()
            .lines()
            .filter { it.isNotEmpty() }
            .mapIndexed { x, line ->
                line.mapIndexed { y, char ->
                    when {
                        char == '.' -> Empty(x, y)
                        char.isDigit() -> Digit(x, y, char.digitToInt())
                        else -> Symbol(x, y, char)
                    }
                }
            }

        fun getAdjacentCharacters(x: Int, y: Int): List<Character> {
            return listOfNotNull(
                schematic.getOrNull(x - 1)?.getOrNull(y - 1),
                schematic.getOrNull(x - 1)?.getOrNull(y),
                schematic.getOrNull(x - 1)?.getOrNull(y + 1),
                schematic.getOrNull(x)?.getOrNull(y - 1),
                schematic.getOrNull(x)?.getOrNull(y + 1),
                schematic.getOrNull(x + 1)?.getOrNull(y - 1),
                schematic.getOrNull(x + 1)?.getOrNull(y),
                schematic.getOrNull(x + 1)?.getOrNull(y + 1),
            )
        }

        fun flagDigits(character: Character, symbol: Symbol) {
            getAdjacentCharacters(character.x, character.y)
                .filterIsInstance<Digit>()
                .filter { it.attachedSymbol == null }
                .forEach { digit ->
                    digit.attachedSymbol = symbol
                    flagDigits(digit, symbol)
                }
        }

        // Traverse schematic to flag digits with its attached symbol
        schematic.forEachIndexed { x, charactersLine ->
            charactersLine.forEachIndexed { y, character ->
                if (character is Symbol) {
                    flagDigits(character, character)
                }
            }
        }

        // Traverse schematic to get the final digit sum which have an attached symbol
        var digitSumWithAttachedSymbol = 0
        schematic.forEachIndexed { x, charactersLine ->
            val currentDigits = mutableListOf<Digit>()

            charactersLine.forEachIndexed { y, character ->
                if (character is Digit && character.attachedSymbol != null) {
                    currentDigits += character
                } else {
                    // Treat each other character as separator
                    if (currentDigits.isNotEmpty()) {
                        val partNumber = currentDigits.joinToString("") { it.digit.toString() }.toInt()
                        currentDigits.first().attachedSymbol!!.attachedPartNumbers += partNumber
                        digitSumWithAttachedSymbol += partNumber
                        currentDigits.clear()
                    }
                }
            }
            // New line is also a separator
            if (currentDigits.isNotEmpty()) {
                val partNumber = currentDigits.joinToString("") { it.digit.toString() }.toInt()
                currentDigits.first().attachedSymbol!!.attachedPartNumbers += partNumber
                digitSumWithAttachedSymbol += partNumber
                currentDigits.clear()
            }
        }

        return if (part2) {
            schematic.sumOf { line ->
                line.sumOf { character ->
                    (character as? Symbol)?.attachedPartNumbers?.takeIf { it.count() == 2 }?.let { it[0] * it[1] } ?: 0
                }
            }
        } else {
            digitSumWithAttachedSymbol
        }
    }
}