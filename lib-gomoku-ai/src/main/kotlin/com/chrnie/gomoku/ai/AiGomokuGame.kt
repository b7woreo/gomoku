package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Stone
import com.chrnie.gomoku.GomokuGame
import com.chrnie.gomoku.ai.algorithm.AlphaBetaNode
import com.chrnie.gomoku.ai.board.evaluator.BoardEvaluator
import java.util.*
import kotlin.collections.HashMap

class AiGomokuGame(difficulty: Int) : GomokuGame() {

  companion object {
    private val random = Random(System.currentTimeMillis())

    private val blackHash = IntArray(GomokuGame.CHESSBOARD_WIDTH * GomokuGame.CHESSBOARD_HEIGHT) {
      random.nextInt()
    }

    private val whiteHash = IntArray(GomokuGame.CHESSBOARD_WIDTH * GomokuGame.CHESSBOARD_HEIGHT) {
      random.nextInt()
    }

    private fun indexOf(x: Int, y: Int): Int = CHESSBOARD_WIDTH * y + x
  }

  private val hashToHeuristicValue = HashMap<Int, Int>()
  private val depth = difficulty * 2
  private var hash = random.nextInt()

  init {
    if (difficulty <= 0) {
      throw RuntimeException("difficulty must > 0")
    }
  }

  override fun onPutChessman(x: Int, y: Int, stone: Stone) {
    hash = when (stone) {
      Stone.WHITE -> {
        hash xor whiteHash[indexOf(x, y)]
      }

      Stone.BLACK -> {
        hash xor blackHash[indexOf(x, y)]
      }
    }
  }

  override fun onUndo(x: Int, y: Int, stone: Stone) {
    hash = when (stone) {
      Stone.WHITE -> {
        hash xor whiteHash[indexOf(x, y)]
      }

      Stone.BLACK -> {
        hash xor blackHash[indexOf(x, y)]
      }
    }
  }

  fun next(): Point =
    if (isWin) throw RuntimeException("game is win")
    else chessman.let { maximizingPlayer ->
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

  private inner class StartNode(maximizingPlayer: Stone) : GameNode(maximizingPlayer) {

    private var hash: Int? = null

    override fun enter() {
      hash = this@AiGomokuGame.hash
    }

    override fun exit() {
      // ignore
    }

    override fun hashCode(): Int {
      if (hash == null) {
        throw RuntimeException("hash == null")
      }
      return hash!!
    }

    override fun equals(other: Any?): Boolean {
      return other?.hashCode() == hashCode()
    }
  }

  private inner class LeafNode(
          maximizingPlayer: Stone,
          internal val x: Int,
          internal val y: Int
  ) : GameNode(maximizingPlayer) {

    private var hash: Int? = null

    override fun enter() {
      putChessman(x, y)
      hash = this@AiGomokuGame.hash
    }

    override fun exit() {
      undo()
    }

    override fun hashCode(): Int {
      if (hash == null) {
        throw RuntimeException("hash == null")
      }
      return hash!!
    }

    override fun equals(other: Any?): Boolean {
      return other?.hashCode() == hashCode()
    }
  }

  private abstract inner class GameNode(private val maximizingPlayer: Stone) : AlphaBetaNode<GameNode>() {

    private val pointEvaluator = PointEvaluator
    private val boardEvaluator = BoardEvaluator

    override fun isTerminal(): Boolean = isWin

    override fun heuristicValue(): Int {
      return hashToHeuristicValue[hashCode()] ?: boardEvaluator.evaluate(this@AiGomokuGame, maximizingPlayer).also {
        hashToHeuristicValue[hashCode()] = it
      }
    }


    override fun child(): Iterable<GameNode> =
      chessman.let { findPutChessmanPoint(it).map { (x, y) -> LeafNode(maximizingPlayer, x, y) } }

    private fun findPutChessmanPoint(player: Stone): Iterable<Point> =
      (0 until GomokuGame.CHESSBOARD_WIDTH).flatMap { x ->
        (0 until GomokuGame.CHESSBOARD_HEIGHT)
          .filter { y -> chessmanAt(x, y) != null }
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
              && (chessmanAt(x, y) == null)
        }
        .map { Pair(it, pointEvaluator.evaluate(this@AiGomokuGame, player, it.x, it.y)) }
        .sortedByDescending { (_, score) -> score }
        .map { it.first }
        .asIterable()
  }
}
