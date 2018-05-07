package com.chrnie.gomoku

import java.util.*

class GomokuGame {

    companion object {
        private const val CHESSBOARD_WIDTH = 15
        private const val CHESSBOARD_HEIGHT = 15
        private const val GOMOKU_COUNT = 5
    }

    constructor(chessboard: Array<Array<Chessman?>>) {
        val height = chessboard.size
        if (height != CHESSBOARD_HEIGHT) {
            throw RuntimeException("illegal height: $height")
        }
        val width = chessboard[0].size
        if (width != CHESSBOARD_WIDTH) {
            throw RuntimeException("illegal width: $width")
        }

        for (x in 0 until CHESSBOARD_WIDTH) {
            for (y in 0 until CHESSBOARD_HEIGHT) {
                this.chessboard[indexOf(x, y)] = chessboard[x][y]
            }
        }
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
            return checkHorizontalWin(x, y, chessman)
                    || checkVerticalWin(x, y, chessman)
                    || checkDiagonalWin(x, y, chessman)
                    || checkInverseDiagonalWin(x, y, chessman)
        }

        private fun checkHorizontalWin(x: Int, y: Int, chessman: Chessman): Boolean {
            var count = 1

            for (i in (0 until x).reversed()) {
                val index = indexOf(i, y)
                if (count == GOMOKU_COUNT || chessboard[index] != chessman) {
                    break
                }
                count += 1
            }

            for (i in x + 1 until CHESSBOARD_WIDTH) {
                val index = indexOf(i, y)
                if (count == GOMOKU_COUNT || chessboard[index] != chessman) {
                    break
                }
                count += 1
            }

            return count == GOMOKU_COUNT
        }

        private fun checkVerticalWin(x: Int, y: Int, chessman: Chessman): Boolean {
            var count = 1

            for (i in (0 until y).reversed()) {
                val index = indexOf(x, i)
                if (count == GOMOKU_COUNT || chessboard[index] != chessman) {
                    break
                }
                count += 1
            }

            for (i in y + 1 until CHESSBOARD_WIDTH) {
                val index = indexOf(x, i)
                if (count == GOMOKU_COUNT || chessboard[index] != chessman) {
                    break
                }
                count += 1
            }

            return count == GOMOKU_COUNT
        }

        private fun checkDiagonalWin(x: Int, y: Int, chessman: Chessman): Boolean {
            var count = 1
            var i = x - 1
            var j = y - 1

            while (i >= 0 || y >= 0) {
                val index = indexOf(i, j)
                if (count == GOMOKU_COUNT || chessboard[index] != chessman) {
                    break
                }
                count += 1
                i -= 1
                j -= 1
            }

            i = x + 1
            j = y + 1
            while (i < CHESSBOARD_WIDTH || y < CHESSBOARD_HEIGHT) {
                val index = indexOf(i, j)
                if (count == GOMOKU_COUNT || chessboard[index] != chessman) {
                    break
                }
                count += 1
                i += 1
                j += 1
            }

            return count == GOMOKU_COUNT
        }

        private fun checkInverseDiagonalWin(x: Int, y: Int, chessman: Chessman): Boolean {
            var count = 1
            var i = x + 1
            var j = y - 1

            while (i < CHESSBOARD_WIDTH || y >= 0) {
                val index = indexOf(i, j)
                if (count == GOMOKU_COUNT || chessboard[index] != chessman) {
                    break
                }
                count += 1
                i += 1
                j -= 1
            }

            i = x - 1
            j = y + 1
            while (i >= 0 || y < CHESSBOARD_HEIGHT) {
                val index = indexOf(i, j)
                if (count == GOMOKU_COUNT || chessboard[index] != chessman) {
                    break
                }
                count += 1
                i -= 1
                j += 1
            }

            return count == GOMOKU_COUNT
        }

        private fun toggleChessman() {
            this@GomokuGame.chessman = if (chessman == Chessman.BLACK) Chessman.WHITE else Chessman.BLACK
        }
    }
}