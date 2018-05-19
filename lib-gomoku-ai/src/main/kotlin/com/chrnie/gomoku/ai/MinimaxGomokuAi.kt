package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.GomokuGame
import java.util.*
import kotlin.math.max
import kotlin.math.min

class MinimaxGomokuAi(player: Chessman, game: GomokuGame) : GomokuAi(player, game) {

    companion object {
        private const val DEFAULT_DEPTH = 2
    }

    private val random = Random(System.currentTimeMillis())
    private val evaluator = EvaluatorImpl

    override fun next(): Point {
        if (game.chessman != player) {
            throw RuntimeException("current not $player play")
        }

        if (game.isWin) {
            throw RuntimeException("game is win")
        }

        var maxScore = Int.MIN_VALUE
        val resultList = ArrayList<Point>()

        val pointList = findPutChessmanPoint()
        for (p in pointList) {
            val score = minimax(p.x, p.y, DEFAULT_DEPTH)
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

        val pointList = findPutChessmanPoint()

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
}