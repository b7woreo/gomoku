package com.chrnie.gomoku.ai

import com.chrnie.gomoku.*

object EvaluatorPoint {
    val WIN = 5
    val LIVE_FOUR = 4
    val DOUBLE_THREE = 3
    val LIVE_THREE = 2
    val LIVE_TWO = 1
    val OTHER = 0

    fun evaluate(game: GomokuGame, chessman: Chessman, x: Int, y: Int): Int {
        var five = 0
        var four = 0
        var three = 0
        var two = 0

        listOf(
            HorizontalIterator(game, x, y),
            VerticalIterator(game, x, y),
            DiagonalIterator(game, x, y),
            InverseDiagonalIterator(game, x, y)
        )
            .map { count(chessman, it) }
            .forEach {
                when (it) {
                    5 -> five += 1
                    4 -> four += 1
                    3 -> three += 1
                }
            }

        return when {
            five != 0 -> WIN
            four != 0 -> LIVE_FOUR
            three >= 2 -> DOUBLE_THREE
            three != 0 -> LIVE_THREE
            two != 0 -> LIVE_TWO
            else -> OTHER
        }
    }

    /**
     * 统计连续五子或活子情况下的连续数
     */
    private fun count(chessman: Chessman, iterator: ChessboardIterator): Int {
        var count = 1
        var dead = false

        while (iterator.hasPrevious()) {
            val c = iterator.previous()
            if (c != chessman) {
                break
            }
            count += 1
        }

        if (iterator.current() != null) dead = true

        iterator.reset()

        while (iterator.hasNext()) {
            val c = iterator.next()
            if (c != chessman) {
                break
            }
            count += 1
        }

        if ((iterator.current() != null || dead) && count != 5) {
            return 0
        }

        return count
    }
}