package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Coordinate
import com.chrnie.gomoku.Direction
import com.chrnie.gomoku.Gomoku
import com.chrnie.gomoku.Player
import com.chrnie.gomoku.ai.algorithm.AlphaBetaNode

fun Gomoku.next(depth: Int): List<Coordinate> {
    return GomokuNode(this, player)
        .alphaBeta(depth)
        .map { it.gomoku.focus ?: throw IllegalStateException("focus == null") }
}

class GomokuNode(val gomoku: Gomoku, val maximizingPlayer: Player) : AlphaBetaNode<GomokuNode>() {

    override fun isTerminal(): Boolean = gomoku.isWon

    override fun heuristicValue(): Int =
        gomoku.board.evaluate().let { it[maximizingPlayer.stone] - it[maximizingPlayer.opponent.stone] }

    override fun children(): Sequence<GomokuNode> {
        return sequence {
            for (x in 0 until Gomoku.BOARD_WIDTH) {
                for (y in 0 until Gomoku.BOARD_HEIGHT) {
                    if (gomoku.board[x, y] != null) {
                        yield(Coordinate(x, y))
                    }
                }
            }
        }
            .flatMap { coordinate ->
                Direction.values()
                    .asSequence()
                    .map { coordinate.move(it, 1) }
            }
            .distinct()
            .ifEmpty { sequenceOf(Coordinate(7, 7)) }
            .filter { Gomoku.isCoordinateInBoard(it) && gomoku.board[it] == null }
            .map { gomoku.put(it) }
            .map { GomokuNode(it, maximizingPlayer) }
    }

}