package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.GomokuGame
import java.util.*
import kotlin.math.max
import kotlin.math.min

class AlphabetaGomokuAi(player: Chessman, game: GomokuGame, val difficulty: Int) : GomokuAi(player, game) {

    private val random = Random(System.currentTimeMillis())
    private val evaluator = EvaluatorImpl

    init {
        if (difficulty <= 0) {
            throw RuntimeException("difficulty must > 0")
        }
    }

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
            val score = alphabeta(p.x, p.y, difficulty, Int.MIN_VALUE, Int.MAX_VALUE)
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

    private fun alphabeta(x: Int, y: Int, depth: Int, alpha: Int, beta: Int): Int {
        println("($x,$y) - depth:$depth - alpha:$alpha - beta:$beta")

        game.putChessman(x, y)

        if (game.isWin || depth == 0) {
            game.undo()
            return evaluator.evaluate(game, player)
        }

        val pointList = findPutChessmanPoint()

        var a = alpha
        var b = beta

        var score: Int
        if (player == game.chessman) {
            score = Int.MIN_VALUE

            for (p in pointList) {
                score = max(score, alphabeta(p.x, p.y, depth - 1, a, b))
                a = max(a, score)
                if (b <= a) {
                    break
                }
            }
        } else {
            score = Int.MAX_VALUE

            for (p in pointList) {
                score = min(score, alphabeta(p.x, p.y, depth - 1, a, b))
                b = min(score, b)
                if (b <= a) {
                    break
                }
            }
        }

        game.undo()
        return score
    }
}