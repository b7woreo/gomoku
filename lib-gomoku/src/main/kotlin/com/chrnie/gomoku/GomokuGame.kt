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

        val action = Action(x, y)
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

    private fun toggleChessman() {
        chessman = if (chessman == Chessman.BLACK) Chessman.WHITE else Chessman.BLACK
    }

    private fun checkWin(): Boolean = checkHorizontalWin()
            || checkVerticalWin()
            || checkDiagonalWin()
            || checkInverseDiagonalWin()

    private fun checkHorizontalWin(): Boolean {
        for (y in 0 until CHESSBOARD_HEIGHT) {
            var count = 0
            var preChessman: Chessman? = null

            for (x in 0 until CHESSBOARD_WIDTH) {
                val chessman = chessboard[indexOf(x, y)]

                if (preChessman != null && preChessman == chessman) {
                    count += 1
                } else {
                    count = 1
                }


                if (count == GOMOKU_COUNT) {
                    return true
                }

                preChessman = chessman
            }
        }

        return false
    }

    private fun checkVerticalWin(): Boolean {
        for (x in 0 until CHESSBOARD_WIDTH) {
            var count = 0
            var preChessman: Chessman? = null

            for (y in 0 until CHESSBOARD_HEIGHT) {
                val chessman = chessboard[indexOf(x, y)]

                if (preChessman != null && preChessman == chessman) {
                    count += 1
                } else {
                    count = 1
                }

                if (count == GOMOKU_COUNT) {
                    return true
                }

                preChessman = chessman
            }
        }

        return false
    }

    private fun checkDiagonalWin(): Boolean {
        for (v in 0 until CHESSBOARD_HEIGHT) {
            var x = 0
            var y = v

            var count = 0
            var preChessman: Chessman? = null

            while ((x > 0 || x < CHESSBOARD_WIDTH) && (y > 0 || y < CHESSBOARD_HEIGHT)) {
                val chessman = chessboard[indexOf(x, y)]

                if (preChessman != null && preChessman == chessman) {
                    count += 1
                } else {
                    count = 1
                }

                if (count == GOMOKU_COUNT) {
                    return true
                }

                preChessman = chessman

                x += 1
                y += 1
            }
        }

        for (h in 0 until CHESSBOARD_WIDTH) {
            var x = h
            var y = 0

            var count = 0
            var preChessman: Chessman? = null

            while ((x > 0 || x < CHESSBOARD_WIDTH) && (y > 0 || y < CHESSBOARD_HEIGHT)) {
                val chessman = chessboard[indexOf(x, y)]

                if (preChessman != null && preChessman == chessman) {
                    count += 1
                } else {
                    count = 1
                }

                if (count == GOMOKU_COUNT) {
                    return true
                }

                preChessman = chessman

                x += 1
                y += 1
            }
        }

        return false
    }

    private fun checkInverseDiagonalWin(): Boolean {
        for (v in 0 until CHESSBOARD_HEIGHT) {
            var x = CHESSBOARD_WIDTH - 1
            var y = v

            var count = 0
            var preChessman: Chessman? = null

            while ((x > 0 || x < CHESSBOARD_WIDTH) && (y > 0 || y < CHESSBOARD_HEIGHT)) {
                val chessman = chessboard[indexOf(x, y)]

                if (preChessman != null && preChessman == chessman) {
                    count += 1
                } else {
                    count = 1
                }

                if (count == GOMOKU_COUNT) {
                    return true
                }

                preChessman = chessman

                x -= 1
                y -= 1
            }
        }

        for (h in 0 until CHESSBOARD_WIDTH) {
            var x = h
            var y = 0

            var count = 0
            var preChessman: Chessman? = null

            while ((x > 0 || x < CHESSBOARD_WIDTH) && (y > 0 || y < CHESSBOARD_HEIGHT)) {
                val chessman = chessboard[indexOf(x, y)]

                if (preChessman != null && preChessman == chessman) {
                    count += 1
                } else {
                    count = 1
                }

                if (count == GOMOKU_COUNT) {
                    return true
                }

                preChessman = chessman

                x -= 1
                y -= 1
            }
        }

        return false
    }

    private inner class Action(private val x: Int, private val y: Int) {

        fun execute(): Boolean {
            if (isWin) {
                return false
            }

            val index = indexOf(x, y)
            val chessman = chessboard[index]
            if (chessman != null) {
                return false
            }

            chessboard[index] = chessman
            if (checkWin()) {
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
    }
}