package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.GomokuGame
import com.chrnie.gomoku.ai.algorithm.AlphaBetaNode

class GomokuAi(val game: GomokuGame, difficulty: Int) {

    private val depth = difficulty * 2

    init {
        if (difficulty <= 0) {
            throw RuntimeException("difficulty must > 0")
        }
    }

  fun next(): Point = if (game.isWin) throw RuntimeException("game is win")
  else game.chessman.let { maximizingPlayer ->
    StartNode(maximizingPlayer).alphaBeta(depth)
      .map { node ->
        if (node is LeafNode) {
          Point(node.x, node.y)
        } else {
          throw RuntimeException("node is not leaf node")
        }
      }
      .let {
        if (it.isNotEmpty()) it
        else listOf(
          Point(3, 3),
          Point(3, 11),
          Point(11, 3),
          Point(11, 11),
          Point(7, 7)
        )
      }
      .let { it[(Math.random() * it.size).toInt()] }
  }

  private inner class StartNode(maximizingPlayer: Chessman) : GameNode(maximizingPlayer) {
    override fun enter() {
      // ignore
    }

    override fun exit() {
      // ignore
    }
  }

  private inner class LeafNode(
    maximizingPlayer: Chessman,
    internal val x: Int,
    internal val y: Int
  ) : GameNode(maximizingPlayer) {

    override fun enter() {
      game.putChessman(x, y)
        }

        override fun exit() {
            game.undo()
        }
  }

  private abstract inner class GameNode(private val maximizingPlayer: Chessman) : AlphaBetaNode<GameNode>() {

    private val pointEvaluator = PointEvaluator
    private val boardEvaluator = BoardEvaluator

    override fun isTerminal(): Boolean = game.isWin

    override fun heuristicValue(): Int = boardEvaluator.evaluate(game, maximizingPlayer)

    override fun child(): Iterable<GameNode> =
      game.chessman.let { findPutChessmanPoint(it).map { (x, y) -> LeafNode(maximizingPlayer, x, y) } }

    private fun findPutChessmanPoint(player: Chessman): Iterable<Point> =
      (0 until GomokuGame.CHESSBOARD_WIDTH).flatMap { x ->
        (0 until GomokuGame.CHESSBOARD_HEIGHT)
          .filter { y -> game.chessmanAt(x, y) != null }
          .flatMap { y ->
            listOf(
              Point(x - 1, y - 1),
              Point(x, y - 1),
              Point(x + 1, y - 1),
              Point(x + 1, y),
              Point(x + 1, y + 1),
              Point(x, y + 1),
              Point(x - 1, y + 1),
              Point(x - 1, y)
            )
          }
      }
        .asSequence()
        .distinct()
        .filter { (x, y) ->
          (x in 0 until GomokuGame.CHESSBOARD_WIDTH)
              && (y in 0 until GomokuGame.CHESSBOARD_HEIGHT)
              && (game.chessmanAt(x, y) == null)
        }
        .map { Pair(it, pointEvaluator.evaluate(game, player, it.x, it.y)) }
        .sortedByDescending { (_, score) -> score }
        .map { it.first }
        .asIterable()
  }
}