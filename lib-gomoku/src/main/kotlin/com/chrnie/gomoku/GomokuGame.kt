package com.chrnie.gomoku

import java.util.*

class GomokuGame {

    companion object {
        const val CHESSBOARD_WIDTH = 15
        const val CHESSBOARD_HEIGHT = 15
        const val GOMOKU_COUNT = 5
    }

    private val chessboard = arrayOfNulls<Chessman>(CHESSBOARD_WIDTH * CHESSBOARD_HEIGHT)

    private val actionQueue = ArrayDeque<Action>()

    var chessman = Chessman.BLACK
        private set

    var winner: Chessman? = null
        private set

    val isWin get() = winner != null

    val chessboardWidth get() = CHESSBOARD_WIDTH

    val chessboardHeight get() = CHESSBOARD_HEIGHT

    fun chessmanAt(x: Int, y: Int): Chessman? {
        checkCoordinate(x, y)
        return chessboard[indexOf(x, y)]
    }

    fun putChessman(x: Int, y: Int): Boolean {
        checkCoordinate(x, y)

        val action = Action(x, y, chessman)
        val isExecute = action.execute()
        if (isExecute) {
            actionQueue.push(action)
        }
        return isExecute
    }

    val canUndo get() = !actionQueue.isEmpty()

    fun undo(): Boolean {
        if (actionQueue.isEmpty()) {
            return false
        }

        val action = actionQueue.pop()
        action.undo()
        return true
    }

    private fun checkCoordinate(x: Int, y: Int) {
        if (x < 0 || x >= CHESSBOARD_WIDTH) {
            throw RuntimeException("x not in range: 0 - $CHESSBOARD_WIDTH")
        }

        if (y < 0 || y >= CHESSBOARD_HEIGHT) {
            throw RuntimeException("y not in range: 0 - $CHESSBOARD_HEIGHT")
        }
    }

    private fun indexOf(x: Int, y: Int): Int = CHESSBOARD_WIDTH * y + x

    private inner class Action(
            private val x: Int,
            private val y: Int,
            private val chessman: Chessman
    ) {

        fun execute(): Boolean {
            if (isWin) {
                return false
            }

            val index = indexOf(x, y)
            if (chessboard[index] == null) {
                return false
            }

            chessboard[index] = chessman
            if (checkWin(x, y, chessman)) {
                winner = chessman
            } else {
                toggleChessman()
            }
            return true
        }

        fun undo() {
            if (isWin) {
                winner = null
            } else {
                toggleChessman()
            }

            val index = indexOf(x, y)
            chessboard[index] = null
        }

        private fun checkWin(x: Int, y: Int, chessman: Chessman): Boolean {
            return checkWin(HorizontalIterator(this@GomokuGame, x, y), chessman)
                    || checkWin(VerticalIterator(this@GomokuGame, x, y), chessman)
                    || checkWin(DiagonalIterator(this@GomokuGame, x, y), chessman)
                    || checkWin(InverseDiagonalIterator(this@GomokuGame, x, y), chessman)
        }

        private fun checkWin(iterator: ChessboardIterator, chessman: Chessman): Boolean {
            var count = 1

            while (count != GOMOKU_COUNT && iterator.hasPrevious()) {
                if (iterator.previous() != chessman) {
                    break
                }

                count += 1
            }

            iterator.reset()

            while (count != GOMOKU_COUNT && iterator.hasNext()) {
                if (iterator.next() != chessman) {
                    break
                }

                count += 1
            }

            return count == GOMOKU_COUNT
        }

        private fun toggleChessman() {
            this@GomokuGame.chessman = if (chessman == Chessman.BLACK) Chessman.WHITE else Chessman.BLACK
        }
    }
}