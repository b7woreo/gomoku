package com.chrnie.gomoku.ai

import com.chrnie.gomoku.*

internal object PointEvaluator {

  fun evaluate(game: GomokuGame, chessman: Chessman, x: Int, y: Int): Int {
    listOf(
      countLiveConnectedChessman(chessman, x, y, game::iterateHorizontalChessman),
      countLiveConnectedChessman(chessman, x, y, game::iterateVerticalChessman),
      countLiveConnectedChessman(chessman, x, y, game::iterateDiagonalChessman),
      countLiveConnectedChessman(chessman, x, y, game::iterateInverseDiagonalChessman)
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

  private inline fun countLiveConnectedChessman(
    chessman: Chessman,
    x: Int,
    y: Int,
    iterator: (Int, Int, Boolean, (Chessman?) -> Unit) -> Unit
  ): Int {
    var count = 0
    var first: Boolean;
    var live = true

    first = true
    iterator(x, y, true) {
      if (!first && it != chessman) {
        live = live && it == null
        return@iterator
      }

      first = false
      count += 1
    }

    first = true
    iterator(x, y, false) {
      if (!first && it != chessman) {
        live = live && it == null
        return@iterator
      }

      first = false
      count += 1
    }

    return if (live || count == GomokuGame.GOMOKU_COUNT) count else 0
  }
}