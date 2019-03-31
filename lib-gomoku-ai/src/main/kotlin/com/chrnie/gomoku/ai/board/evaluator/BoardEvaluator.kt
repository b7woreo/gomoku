package com.chrnie.gomoku.ai.board.evaluator

import com.chrnie.gomoku.Stone
import com.chrnie.gomoku.CoordMapping
import com.chrnie.gomoku.GomokuGame

internal object BoardEvaluator {

  fun evaluate(game: GomokuGame, maximizingPlayer: Stone): Int = with(State.Output()) {
    statisticsHorizontal(this, game)
    statisticsVertical(this, game)
    statisticsDiagonal(this, game)
    statisticsInverseDiagonal(this, game)

    return when (maximizingPlayer) {
      Stone.BLACK -> this[Stone.BLACK] - this[Stone.WHITE]
      Stone.WHITE -> this[Stone.WHITE] - this[Stone.BLACK]
    }
  }
}

private fun statisticsHorizontal(output: State.Output, game: GomokuGame) {
  for (y in 0 until GomokuGame.CHESSBOARD_HEIGHT) {
    statistics(output, game, 0, y, CoordMapping.Rotate0)
  }
}

private fun statisticsVertical(output: State.Output, game: GomokuGame) {
  for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
    statistics(output, game, x, 0, CoordMapping.Rotate90)
  }
}

private fun statisticsDiagonal(output: State.Output, game: GomokuGame) {
  for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
    statistics(output, game, x, 0, CoordMapping.Rotate45)
  }
  for (y in 1 until GomokuGame.CHESSBOARD_HEIGHT) {
    statistics(output, game, 0, y, CoordMapping.Rotate45)
  }
}

private fun statisticsInverseDiagonal(output: State.Output, game: GomokuGame) {
  for (x in 0 until GomokuGame.CHESSBOARD_WIDTH) {
    statistics(output, game, x, 0, CoordMapping.Rotate135)
  }

  for (y in GomokuGame.CHESSBOARD_HEIGHT - 1 downTo 0) {
    statistics(output, game, GomokuGame.CHESSBOARD_WIDTH - 1, y, CoordMapping.Rotate135)
  }
}

private fun statistics(
  output: State.Output,
  game: GomokuGame,
  startX: Int,
  startY: Int,
  mapping: CoordMapping
) {

  var state = State.START
  val outCoord = intArrayOf(-1, -1)
  for (dX in 0..Int.MAX_VALUE) {
    mapping.map(startX, startY, dX, 0, outCoord)
    val (x, y) = outCoord
    if (!GomokuGame.isCoordinateInBoard(x, y)) {
      break
    }

    val chessman = game.chessmanAt(x, y)
    val input = when (chessman) {
      null -> State.Input.NONE
      Stone.BLACK -> State.Input.BLACK
      Stone.WHITE -> State.Input.WHITE
    }
    state = state.next(input, output)
  }
  state.next(State.Input.END, output)
}
