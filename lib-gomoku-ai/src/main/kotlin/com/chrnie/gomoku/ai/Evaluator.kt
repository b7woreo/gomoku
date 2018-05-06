package com.chrnie.gomoku.ai

import com.chrnie.gomoku.*

interface Evaluator {
    fun evaluate(game: GomokuGame, chessman: Chessman): Int
}

object EvaluatorImpl : Evaluator {

    override fun evaluate(game: GomokuGame, chessman: Chessman): Int = with(Statistics()) {
        com.chrnie.gomoku.ai.EvaluatorImpl.statisticsHorizontal(this, game)
        com.chrnie.gomoku.ai.EvaluatorImpl.statisticsVertical(this, game)
        com.chrnie.gomoku.ai.EvaluatorImpl.statisticsDiagonal(this, game)
        com.chrnie.gomoku.ai.EvaluatorImpl.statisticsInverseDiagonal(this, game)

        return if (chessman == com.chrnie.gomoku.Chessman.BLACK) {
            blackScore - whileScore
        } else {
            whileScore - blackScore
        }
    }

    private fun statisticsHorizontal(statistics: Statistics, game: GomokuGame) {
        for (y in 0 until GomokuGame.CHESSBOARD_HEIGHT) {
            statistics(HorizontalIterator(game, 0, y), statistics)
        }
    }

    private fun statisticsVertical(statistics: Statistics, game: GomokuGame) {
        for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
            statistics(VerticalIterator(game, x, 0), statistics)
        }
    }

    private fun statisticsDiagonal(statistics: Statistics, game: GomokuGame) {
        for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
            statistics(DiagonalIterator(game, x, 0), statistics)
        }

        for (y in 1 until GomokuGame.CHESSBOARD_HEIGHT) {
            statistics(DiagonalIterator(game, 0, y), statistics)
        }
    }

    private fun statisticsInverseDiagonal(statistics: Statistics, game: GomokuGame) {
        for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
            statistics(InverseDiagonalIterator(game, x, GomokuGame.CHESSBOARD_HEIGHT - 1), statistics)
        }

        for (y in 0 until GomokuGame.CHESSBOARD_HEIGHT - 1) {
            statistics(InverseDiagonalIterator(game, 0, y), statistics)
        }
    }

    private fun statistics(iterator: ChessboardIterator, statistics: Statistics) {
        var count = 0
        var pre: Chessman? = null
        var dead = false

        while (iterator.hasNext()) {
            val cur = iterator.next()
            if (pre == null && cur == null) {
                continue
            }

            if (pre == null && cur != null) {
                count = 1
                pre = cur
                dead = false
                continue
            }

            if (pre != null && cur == null) {
                statistics.increaseCount(pre, count, dead)
                count = 0
                pre = cur
                dead = false
                continue
            }

            if (pre != null && pre != cur) {
                if (!dead) {
                    statistics.increaseCount(pre, count, true)
                }
                count = 1
                pre = cur
                dead = true
                continue
            }

            if (pre != null && pre == cur) {
                count += 1
                continue
            }

            throw RuntimeException("unknown branch: pre = $pre - current = $cur")
        }
    }

    private class Statistics {
        companion object {
            val scoreTable = mapOf(
                    1 to mapOf(false to 10),
                    2 to mapOf(false to 100, true to 10),
                    3 to mapOf(false to 1000, true to 100),
                    4 to mapOf(false to 10000, true to 1000),
                    5 to mapOf(false to 100000, true to 10000)
            )
        }

        var blackScore = 0
            private set

        var whileScore = 0
            private set

        fun increaseCount(chessman: Chessman, gomokuCount: Int, dead: Boolean) {
            val score = scoreTable[gomokuCount]?.let { it[dead] } ?: 0
            if (chessman == Chessman.BLACK) blackScore += score else whileScore += score
        }
    }
}