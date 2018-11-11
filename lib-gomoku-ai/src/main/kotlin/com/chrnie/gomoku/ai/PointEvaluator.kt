package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.CoordMapping
import com.chrnie.gomoku.GomokuGame

internal object PointEvaluator {

  fun evaluate(game: GomokuGame, chessman: Chessman, x: Int, y: Int): Int {
    listOf(
      countLiveConnectedChessman(game, chessman, x, y, CoordMapping.Rotate0),
      countLiveConnectedChessman(game, chessman, x, y, CoordMapping.Rotate45),
      countLiveConnectedChessman(game, chessman, x, y, CoordMapping.Rotate90),
      countLiveConnectedChessman(game, chessman, x, y, CoordMapping.Rotate135)
    ).fold(IntArray(3)) { acc, liveCount ->
      when (liveCount) {
        5 -> acc.apply { this[2] += 1 }
        4 -> acc.apply { this[1] += 1 }
        3 -> acc.apply { this[0] += 1 }
        else -> acc
      }
    }.run {
      return when {
        this[2] >= 1 -> 5
        this[1] >= 1 -> 4
        this[0] >= 2 -> 3
        else -> 0
      }
    }
  }

  private fun countLiveConnectedChessman(
    game: GomokuGame,
    chessman: Chessman,
    x: Int,
    y: Int,
    mapping: CoordMapping
  ): Int {
    var count = 1
    var live = true
    val out = intArrayOf(-1, -1)

    for (dX in 1..Int.MAX_VALUE) {
      mapping.map(x, y, dX, 0, out)
      if (!GomokuGame.isCoordinateInBoard(out[0], out[1])) {
        break
      }

      val c = game.chessmanAt(out[0], out[1])
      if (c != chessman) {
        live = live && c == null
        break
      }

      count += 1
    }

    for (dX in -1 downTo Int.MIN_VALUE) {
      mapping.map(x, y, dX, 0, out)
      if (!GomokuGame.isCoordinateInBoard(out[0], out[1])) {
        break
      }

      val c = game.chessmanAt(out[0], out[1])
      if (c != chessman) {
        live = live && c == null
        break
      }

      count += 1
    }

    return if (live || count == GomokuGame.GOMOKU_COUNT) count else 0
  }
}