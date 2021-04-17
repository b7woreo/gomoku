package com.chrnie.gomoku

import com.chrnie.gomoku.Gomoku.Companion.isCoordinateInBoard

internal class LinkedGomoku private constructor(
    override val focus: Coordinate?,
    private val _parent: LinkedGomoku?
) : Gomoku {

    internal constructor() : this(null, null)

    override val player: Player

    override val board: Board

    override val isWon: Boolean

    private val _winner: Player?

    init {
        if (focus == null && _parent == null) {
            player = FirstHandPlayer
            board = Board(Gomoku.BOARD_WIDTH, Gomoku.BOARD_HEIGHT)
            isWon = false
            _winner = null
        } else if (focus != null && _parent != null) {
            player = _parent.player.opponent
            board = _parent.board.put(focus, _parent.player.stone)
            isWon = countContinuousStone(board, _parent.player.stone, focus, Direction.TOP) >= Gomoku.GOMOKU_COUNT
                    || countContinuousStone(board, _parent.player.stone, focus, Direction.RIGHT) >= Gomoku.GOMOKU_COUNT
                    || countContinuousStone(board,_parent.player.stone,focus,Direction.TOP_RIGHT) >= Gomoku.GOMOKU_COUNT
                    || countContinuousStone(board,_parent.player.stone,focus,Direction.BOTTOM_RIGHT) >= Gomoku.GOMOKU_COUNT
            _winner = if (isWon) _parent.player else null
        } else {
            throw IllegalStateException("focus is $focus, but _parent is $_parent")
        }
    }

    override val winner: Player
        get() {
            if (!isWon) throw IllegalStateException("Game is not won")
            return _winner!!
        }

    override val canUndo: Boolean = _parent != null

    override fun undo(): Gomoku {
        if (!canUndo) throw IllegalStateException("Can not undo")
        return _parent!!
    }

    override fun put(x: Int, y: Int): Gomoku {
        if (isWon) throw IllegalStateException("Game is won")
        if (board[x, y] != null) throw IllegalArgumentException("($x, $y) already has stone")

        val focus = Coordinate(x, y)
        return LinkedGomoku(focus, this)
    }

    private fun countContinuousStone(board: Board, playerStone: Stone, focus: Coordinate, direction: Direction): Int {
        var count = 1

        for (delta in 1..Int.MAX_VALUE) {
            val (x, y) = focus.move(direction, delta)
            if (!isCoordinateInBoard(x, y)) {
                break
            }

            val stone = board[x, y]
            if (stone == playerStone) {
                count += 1
                continue
            } else {
                break
            }
        }

        for (delta in 1..Int.MAX_VALUE) {
            val (x, y) = focus.move(direction.reverse(), delta)
            if (!isCoordinateInBoard(x, y)) {
                break
            }

            val stone = board[x, y]
            if (stone == playerStone) {
                count += 1
                continue
            } else {
                break
            }
        }

        return count
    }

    override fun toString(): String  = board.toString()

}