package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.CoordMapping
import com.chrnie.gomoku.GomokuGame

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
    statistics(game, 0, y, CoordMapping.Rotate0, statistics)
  }
}

private fun statisticsVertical(statistics: Statistics, game: GomokuGame) {
  for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
    statistics(game, x, 0, CoordMapping.Rotate90, statistics)
  }
}

private fun statisticsDiagonal(statistics: Statistics, game: GomokuGame) {
  for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
    statistics(game, x, GomokuGame.CHESSBOARD_HEIGHT - 1, CoordMapping.Rotate315, statistics)
  }

  for (y in 0 until GomokuGame.CHESSBOARD_HEIGHT - 1) {
    statistics(game, 0, y, CoordMapping.Rotate315, statistics)
  }
}

private fun statisticsInverseDiagonal(statistics: Statistics, game: GomokuGame) {
  for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
    statistics(game, x, 0, CoordMapping.Rotate135, statistics)
  }

  for (y in 1 until GomokuGame.CHESSBOARD_HEIGHT) {
    statistics(game, GomokuGame.CHESSBOARD_WIDTH - 1, y, CoordMapping.Rotate135, statistics)
  }
}

private fun statistics(
  game: GomokuGame,
  x: Int,
  y: Int,
  mapping: CoordMapping,
  statistics: Statistics
) {
  var count = 0
  var pre: Chessman? = null
  var dead = true

  val out = arrayOf(-1, -1)
  for (dX in 0..Int.MAX_VALUE) {
    mapping.map(x, y, dX, 0, out)
    if (!GomokuGame.isCoordinateInBoard(out[0], out[1])) {
      break
    }

    val cur = game.chessmanAt(out[0], out[1])

    if (pre == null && cur == null) {
      continue
    }

    if (pre == null && cur != null) {
      count = 1
      pre = cur
      dead = false
      continue
    }

    if (pre != null && cur == null) {
      statistics.increaseCount(pre, count, dead)
      count = 0
      pre = cur
      dead = false
      continue
    }

    if (pre != null && pre != cur) {
      if (!dead) {
        statistics.increaseCount(pre, count, true)
      }
      count = 1
      pre = cur
      dead = true
      continue
    }

    if (pre != null && pre == cur) {
      count += 1
      continue
    }

    throw RuntimeException("unknown branch: pre = $pre - current = $cur")
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
