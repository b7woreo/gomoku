package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.GomokuGame
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.max
import kotlin.math.min

class GomokuAi(private val player: Chessman, private val game: GomokuGame) {

    companion object {
        private const val MINIMAX_INIT_DEPTH = 2
    }

    private val random = Random(System.currentTimeMillis())
    private val evaluator = EvaluatorImpl

    fun next(): Point {
        if (game.chessman != player) {
            throw RuntimeException("current not $player play")
        }

        var maxScore = Int.MIN_VALUE
        val resultList = ArrayList<Point>()

        val pointList = gen()
        for (p in pointList) {
            val score = minimax(p.x, p.y, MINIMAX_INIT_DEPTH)
            if (score < maxScore) {
                continue
            }
            if (score > maxScore) {
                maxScore = score
                resultList.clear()
            }
            resultList.add(p)
        }

        if (resultList.isEmpty()) {
            var p: Point? = null
            while (p == null) {
                val x = random.nextInt(game.chessboardWidth)
                val y = random.nextInt(game.chessboardHeight)
                if (game.chessmanAt(x, y) != null) {
                    continue
                }
                p = Point(x, y)
            }

            return p
        }

        val r = random.nextInt(resultList.size)
        return resultList[r]
    }

    private fun minimax(x: Int, y: Int, depth: Int): Int {
        game.putChessman(x, y)

        if (game.isWin || depth == 0) {
            game.undo()
            return evaluator.evaluate(game, player)
        }

        val pointList = gen()

        var score: Int
        if (player == game.chessman) {
            score = Int.MIN_VALUE

            for (p in pointList) {
                score = max(score, minimax(p.x, p.y, depth - 1))
            }
        } else {
            score = Int.MAX_VALUE

            for (p in pointList) {
                score = min(score, minimax(p.x, p.y, depth - 1))
            }
        }

        game.undo()
        return score
    }

    private fun gen(): List<Point> {
        val set = HashSet<Point>()

        val width = game.chessboardWidth
        val height = game.chessboardHeight

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (game.chessmanAt(x, y) == null) {
                    continue
                }

                set.add(Point(x - 1, y - 1))
                set.add(Point(x, y - 1))
                set.add(Point(x + 1, y - 1))
                set.add(Point(x + 1, y))
                set.add(Point(x + 1, y + 1))
                set.add(Point(x, y + 1))
                set.add(Point(x - 1, y + 1))
                set.add(Point(x - 1, y))
            }
        }

        return set.filter { (x, y) -> (x in 0 until width) && (y in 0 until height) && (game.chessmanAt(x, y) == null) }
    }

    data class Point(
            val x: Int,
            val y: Int
    )
}