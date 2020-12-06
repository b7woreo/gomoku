package com.chrnie.gomoku.ai

import com.chrnie.gomoku.*
import kotlin.math.min

internal fun Board.evaluate(): Score = Score().also { score ->
    (0 until width).map { Coordinate(it, 0) }
        .forEach { statistics(score, this, it, Direction.TOP) }

    (0 until height).map { Coordinate(0, it) }
        .forEach { statistics(score, this, it, Direction.RIGHT) }

    (0 until width).map { Coordinate(it, 0) }
        .forEach { statistics(score, this, it, Direction.TOP_RIGHT) }
    (1 until height).map { Coordinate(0, it) }
        .forEach { statistics(score, this, it, Direction.TOP_RIGHT) }

    (0 until width).map { Coordinate(it, 0) }
        .forEach { statistics(score, this, it, Direction.BOTTOM_RIGHT) }
    (1 until height).map { Coordinate(0, it) }
        .forEach { statistics(score, this, it, Direction.BOTTOM_RIGHT) }
}

internal class Score {

    private var blackScore = 0
    private var whiteScore = 0

    operator fun get(stone: Stone) = when (stone) {
        Stone.BLACK -> blackScore
        Stone.WHITE -> whiteScore
    }

    operator fun set(stone: Stone, score: Int) = when (stone) {
        Stone.BLACK -> blackScore = score
        Stone.WHITE -> whiteScore = score
    }
}

private fun statistics(
    output: Score,
    board: Board,
    coordinate: Coordinate,
    direction: Direction
) {
    var state = State.start()

    for (delta in 0..Int.MAX_VALUE) {
        val movedCoordinate = coordinate.move(direction, delta)
        if (!Gomoku.isCoordinateInBoard(movedCoordinate)) {
            break
        }

        val stone = board[movedCoordinate.x, movedCoordinate.y]
        state = state.next(stone, output)
    }

    state.end(output)
}

private sealed class State {

    companion object {
        private val START: State = StartState

        fun start(): State = START

        private val fsmTable = mapOf(
            Stone.WHITE to mapOf(
                true to mapOf(
                    1 to ChessmanState(Stone.WHITE, 1, true),
                    2 to ChessmanState(Stone.WHITE, 2, true),
                    3 to ChessmanState(Stone.WHITE, 3, true),
                    4 to ChessmanState(Stone.WHITE, 4, true),
                    5 to ChessmanState(Stone.WHITE, 5, true)
                ),
                false to mapOf(
                    1 to ChessmanState(Stone.WHITE, 1, false),
                    2 to ChessmanState(Stone.WHITE, 2, false),
                    3 to ChessmanState(Stone.WHITE, 3, false),
                    4 to ChessmanState(Stone.WHITE, 4, false),
                    5 to ChessmanState(Stone.WHITE, 5, false)
                )
            ),
            Stone.BLACK to mapOf(
                true to mapOf(
                    1 to ChessmanState(Stone.BLACK, 1, true),
                    2 to ChessmanState(Stone.BLACK, 2, true),
                    3 to ChessmanState(Stone.BLACK, 3, true),
                    4 to ChessmanState(Stone.BLACK, 4, true),
                    5 to ChessmanState(Stone.BLACK, 5, true)
                ),
                false to mapOf(
                    1 to ChessmanState(Stone.BLACK, 1, false),
                    2 to ChessmanState(Stone.BLACK, 2, false),
                    3 to ChessmanState(Stone.BLACK, 3, false),
                    4 to ChessmanState(Stone.BLACK, 4, false),
                    5 to ChessmanState(Stone.BLACK, 5, false)
                )
            )
        )
    }

    abstract fun next(input: Stone?, output: Score): State

    open fun end(output: Score) {
        // default do nothing
    }

    protected fun fsm(stone: Stone, count: Int, dead: Boolean): State {
        return fsmTable.let { it[stone] }?.let { it[dead] }?.let { it[min(count, 5)] }
            ?: throw IllegalStateException("can not find match fsm")
    }
}

private object StartState : State() {

    override fun next(input: Stone?, output: Score): State = when (input) {
        null -> NoneState
        Stone.BLACK -> fsm(Stone.BLACK, 1, true)
        Stone.WHITE -> fsm(Stone.WHITE, 1, true)
    }
}

private object NoneState : State() {

    override fun next(input: Stone?, output: Score): State = when (input) {
        null -> NoneState
        Stone.BLACK -> fsm(Stone.BLACK, 1, false)
        Stone.WHITE -> fsm(Stone.WHITE, 1, false)
    }

}

private class ChessmanState(val stone: Stone, val count: Int, val dead: Boolean) : State() {

    companion object {
        private val scoreTable = mapOf(
            1 to mapOf(false to 10, true to 10),
            2 to mapOf(false to 100, true to 10),
            3 to mapOf(false to 1000, true to 100),
            4 to mapOf(false to 10000, true to 1000),
            5 to mapOf(false to 100000, true to 100000)
        )

        private fun findScore(count: Int, dead: Boolean): Int {
            return scoreTable.let { it[count] }?.let { it[dead] }
                ?: throw RuntimeException("can not find match score")
        }
    }

    override fun next(input: Stone?, output: Score): State = when (input) {
        null -> {
            output[stone] += findScore(count, dead)
            NoneState
        }
        Stone.BLACK -> {
            if (stone == Stone.BLACK) fsm(Stone.BLACK, count + 1, dead)
            else {
                if (!dead) output[stone] += findScore(count, true)
                fsm(Stone.BLACK, 1, true)
            }
        }
        Stone.WHITE -> {
            if (stone == Stone.WHITE) fsm(Stone.WHITE, count + 1, dead)
            else {
                if (!dead) output[stone] += findScore(count, true)
                fsm(Stone.WHITE, 1, true)
            }
        }
    }

    override fun end(output: Score) {
        if (!dead) output[stone] += findScore(count, true)
    }
}
