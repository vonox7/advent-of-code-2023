import java.io.File
import kotlin.math.pow

class Day4 {
    data class Card(val cardNumber: Int, val winningNumbers: Set<Int>, val numbersYouHave: Set<Int>)
    fun run(part2: Boolean): Int {
        // Parse input
        val cards = File("input/day4.txt")
            .readText()
            .lines()
            .map { line ->
                val cardNumber = line.substringBefore(":").substringAfterLast(" ").toInt()
                val winningNumbers = line
                    .substringAfter(":")
                    .substringBefore("|")
                    .split(" ")
                    .mapNotNull { number -> number.trim().takeIf { it.isNotEmpty() }?.toInt() }
                val numbersYouHave = line
                    .substringAfter("|")
                    .split(" ")
                    .mapNotNull { number -> number.trim().takeIf { it.isNotEmpty() }?.toInt() }
                Card(cardNumber, winningNumbers.toSet(), numbersYouHave.toSet())
            }

        // Calculate score
        return if (part2) {
            val cardsToMatches: Map<Card, Int> = cards.associateWith { it.winningNumbers.intersect(it.numbersYouHave).count() }
            val cardCount: MutableMap<Int, Int> = cardsToMatches.keys.map { it.cardNumber }.associateWith { 1 }.toMutableMap() // Card number to matches
            cardsToMatches.forEach { (card, matches) ->
                (card.cardNumber + 1..card.cardNumber + matches).forEach { nextCardNumber ->
                    cardCount[nextCardNumber] = cardCount[nextCardNumber]!! + cardCount[card.cardNumber]!!
                }
            }
            cardCount.values.sum()
        } else {
            cards.sumOf { card ->
                val sameNumbers = card.winningNumbers.intersect(card.numbersYouHave).count()
                if (sameNumbers == 0) 0 else (2.toDouble().pow(sameNumbers - 1)).toInt()
            }
        }
    }
}