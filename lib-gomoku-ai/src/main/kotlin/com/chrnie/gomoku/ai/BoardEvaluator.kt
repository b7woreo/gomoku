package com.chrnie.gomoku.ai

import com.chrnie.gomoku.*

internal object BoardEvaluator {

  fun evaluate(game: GomokuGame, maximizingPlayer: Chessman): Int = with(Statistics()) {
    statisticsHorizontal(this, game)
    statisticsVertical(this, game)
    statisticsDiagonal(this, game)
    statisticsInverseDiagonal(this, game)

    return when (maximizingPlayer) {
      Chessman.BLACK -> blackScore - whileScore
      Chessman.WHITE -> whileScore - blackScore
    }
  }
}

private fun statisticsHorizontal(statistics: Statistics, game: GomokuGame) {
  for (y in 0 until GomokuGame.CHESSBOARD_HEIGHT) {
    statistics(0, y, game::iterateHorizontalChessman, statistics)
  }
}

private fun statisticsVertical(statistics: Statistics, game: GomokuGame) {
  for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
    statistics(x, 0, game::iterateVerticalChessman, statistics)
  }
}

private fun statisticsDiagonal(statistics: Statistics, game: GomokuGame) {
  for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
    statistics(x, 0, game::iterateDiagonalChessman, statistics)
  }

  for (y in 1 until GomokuGame.CHESSBOARD_HEIGHT) {
    statistics(0, y, game::iterateDiagonalChessman, statistics)
  }
}

private fun statisticsInverseDiagonal(statistics: Statistics, game: GomokuGame) {
  for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
    statistics(x, GomokuGame.CHESSBOARD_HEIGHT - 1, game::iterateInverseDiagonalChessman, statistics)
  }

  for (y in 0 until GomokuGame.CHESSBOARD_HEIGHT - 1) {
    statistics(0, y, game::iterateInverseDiagonalChessman, statistics)
  }
}

private fun statistics(
  x: Int,
  y: Int,
  iterator: (x: Int, y: Int, positiveDirection: Boolean, (Chessman?) -> Unit) -> Unit,
  statistics: Statistics
) {
  var count = 1
  var pre: Chessman? = null
  var dead = true

  iterator(x, y, true) iterator@{
    if (pre == null && it == null) {
      return@iterator
    }

    if (pre == null && it != null) {
      count = 1
      pre = it
      dead = false
      return@iterator
    }

    if (pre != null && it == null) {
      statistics.increaseCount(pre!!, count, dead)
      count = 0
      pre = it
      dead = false
      return@iterator
    }

    if (pre != null && pre != it) {
      if (!dead) {
        statistics.increaseCount(pre!!, count, true)
      }
      count = 1
      pre = it
      dead = true
      return@iterator
    }

    if (pre != null && pre == it) {
      count += 1
      return@iterator
    }

    throw RuntimeException("unknown branch: pre = $pre - current = $it")
  }
}

private class Statistics {
  companion object {
    val scoreTable = mapOf(
      1 to mapOf(false to 10),
      2 to mapOf(false to 100, true to 10),
      3 to mapOf(false to 1000, true to 100),
      4 to mapOf(false to 10000, true to 1000),
      5 to mapOf(false to 100000, true to 100000)
    )
  }

  var blackScore = 0
    private set

  var whileScore = 0
    private set

  fun increaseCount(chessman: Chessman, gomokuCount: Int, dead: Boolean) {
    val score = scoreTable[gomokuCount]?.let { it[dead] } ?: 0
    if (chessman == Chessman.BLACK) blackScore += score else whileScore += score
  }
}
