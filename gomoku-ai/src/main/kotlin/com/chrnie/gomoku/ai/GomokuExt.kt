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

private class GomokuNode(
    val gomoku: Gomoku,
    val maximizingPlayer: Player,
    val parent: GomokuNode? = null
) :
    AlphaBetaNode<GomokuNode>() {

    private val _situation: Situation
    private val _heuristicValue: Int

    init {
        val prevSituation = parent?.let { parent._situation }
        _situation = gomoku.board.evaluate(prevSituation, gomoku.focus)
        _heuristicValue =
            _situation.heuristicValue(maximizingPlayer.stone) - _situation.heuristicValue(maximizingPlayer.opponent.stone)
    }

    override fun isTerminal(): Boolean = gomoku.isWon

    override fun heuristicValue(): Int = _heuristicValue

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
            .map { GomokuNode(it, maximizingPlayer, this) }
            .sortedByDescending { _heuristicValue * if (maximizingPlayer == gomoku.player) 1 else -1 }
    }

}