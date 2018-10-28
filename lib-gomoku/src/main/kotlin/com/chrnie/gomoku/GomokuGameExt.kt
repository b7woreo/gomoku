package com.chrnie.gomoku

import kotlin.math.min

inline fun GomokuGame.iterateHorizontalChessman(
  startX: Int,
  startY: Int,
  positiveDirection: Boolean,
  block: (Chessman?) -> Unit
) {
  if (positiveDirection) {
    for (x in startX until GomokuGame.CHESSBOARD_WIDTH) {
      block(chessmanAt(x, startY))
    }
  } else {
    for (x in startX downTo 0) {
      block(chessmanAt(x, startY))
    }
  }
}

inline fun GomokuGame.iterateVerticalChessman(
  startX: Int,
  startY: Int,
  positiveDirection: Boolean,
  block: (Chessman?) -> Unit
) {
  if (positiveDirection) {
    for (y in startY until GomokuGame.CHESSBOARD_HEIGHT) {
      block(chessmanAt(startX, y))
    }
  } else {
    for (y in startY downTo 0) {
      block(chessmanAt(startX, y))
    }
  }
}

inline fun GomokuGame.iterateDiagonalChessman(
  startX: Int,
  startY: Int,
  positiveDirection: Boolean,
  block: (Chessman?) -> Unit
) {
  if (positiveDirection) {
    val count = min(GomokuGame.CHESSBOARD_WIDTH - startX, GomokuGame.CHESSBOARD_HEIGHT - startY)
    for (i in 0 until count) {
      block(chessmanAt(startX + i, startY + i))
    }
  } else {
    val count = min(startX + 1, startY + 1)
    for (i in 0 until count) {
      block(chessmanAt(startX - i, startY - i))
    }
  }
}

inline fun GomokuGame.iterateInverseDiagonalChessman(
  startX: Int,
  startY: Int,
  positiveDirection: Boolean,
  block: (Chessman?) -> Unit
) {
  if (positiveDirection) {
    val count = min(GomokuGame.CHESSBOARD_WIDTH - startX, startY + 1)
    for (i in 0 until count) {
      block(chessmanAt(startX + i, startY - i))
    }
  } else {
    val count = min(startX + 1, GomokuGame.CHESSBOARD_HEIGHT - startY)
    for (i in 0 until count) {
      block(chessmanAt(startX - i, startY + i))
    }
  }
}

fun GomokuGame.countHorizontalConnectedChessman(x: Int, y: Int): Int {
  val chessman = chessmanAt(x, y) ?: return 0

  var count = -1
  run iterate@{
    iterateHorizontalChessman(x, y, true) {
      if (it != chessman) {
        return@iterate
      }

      count += 1
    }
  }

  run iterate@{
    iterateHorizontalChessman(x, y, false) {
      if (it != chessman) {
        return@iterate
      }

      count += 1
    }
  }

  return count
}

fun GomokuGame.countVerticalConnectedChessman(x: Int, y: Int): Int {
  val chessman = chessmanAt(x, y) ?: return 0

  var count = -1
  run iterate@{
    iterateVerticalChessman(x, y, true) {
      if (it != chessman) {
        return@iterate
      }

      count += 1
    }
  }

  run iterate@{
    iterateVerticalChessman(x, y, false) {
      if (it != chessman) {
        return@iterate
      }

      count += 1
    }
  }

  return count
}

fun GomokuGame.countDiagonalConnectedChessman(x: Int, y: Int): Int {
  val chessman = chessmanAt(x, y) ?: return 0

  var count = -1
  run iterate@{
    iterateDiagonalChessman(x, y, true) {
      if (it != chessman) {
        return@iterate
      }

      count += 1
    }
  }

  run iterate@{
    iterateDiagonalChessman(x, y, false) {
      if (it != chessman) {
        return@iterate
      }

      count += 1
    }
  }

  return count
}

fun GomokuGame.countInverseDiagonalConnectedChessman(x: Int, y: Int): Int {
  val chessman = chessmanAt(x, y) ?: return 0

  var count = -1
  run iterate@{
    iterateInverseDiagonalChessman(x, y, true) {
      if (it != chessman) {
        return@iterate
      }

      count += 1
    }
  }

  run iterate@{
    iterateInverseDiagonalChessman(x, y, false) {
      if (it != chessman) {
        return@iterate
      }

      count += 1
    }
  }

  return count
}

