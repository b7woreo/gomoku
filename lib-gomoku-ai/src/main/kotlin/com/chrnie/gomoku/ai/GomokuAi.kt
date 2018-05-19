package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.GomokuGame

abstract class GomokuAi(protected val player: Chessman, protected val game: GomokuGame) {

    abstract fun next(): Point

    protected fun findPutChessmanPoint(): List<Point> {
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
}