package com.chrnie.gomoku

import java.util.*

class GomokuGame private constructor(private val chessboard: Array<Chessman?>) {
  companion object {
    const val CHESSBOARD_WIDTH = 15
    const val CHESSBOARD_HEIGHT = 15
    const val GOMOKU_COUNT = 5

    internal fun checkCoordinate(x: Int, y: Int) {
      if (x < 0 || x >= CHESSBOARD_WIDTH) {
        throw RuntimeException("x not in range: 0 - ${CHESSBOARD_WIDTH - 1}, current is: $x")
      }

      if (y < 0 || y >= CHESSBOARD_HEIGHT) {
        throw RuntimeException("y not in range: 0 - ${CHESSBOARD_HEIGHT - 1}, current is $y")
      }
    }

    private fun indexOf(x: Int, y: Int): Int = CHESSBOARD_WIDTH * y + x
  }

  constructor() : this(arrayOfNulls(CHESSBOARD_WIDTH * CHESSBOARD_HEIGHT))

  private val actionQueue = ArrayDeque<Action>()

  var chessman = Chessman.BLACK
    private set

  var winner: Chessman? = null
    private set

  val isWin get() = winner != null

  fun chessmanAt(x: Int, y: Int): Chessman? {
    checkCoordinate(x, y)
    return chessboard[indexOf(x, y)]
  }

  fun putChessman(x: Int, y: Int): Boolean {
    checkCoordinate(x, y)

    val action = Action(x, y, chessman)
    val isExecute = action.execute()
    if (isExecute) {
      actionQueue.push(action)
    }
    return isExecute
  }

  val canUndo get() = !actionQueue.isEmpty()

  fun undo(): Boolean {
    if (actionQueue.isEmpty()) {
      return false
    }

    val action = actionQueue.pop()
    action.undo()
    return true
  }

  fun restart() {
    chessboard.fill(null)
    actionQueue.clear()
    chessman = Chessman.BLACK
    winner = null
  }

  private fun toggleChessman() {
    this.chessman = if (chessman == Chessman.BLACK) Chessman.WHITE else Chessman.BLACK
  }

  private inner class Action(
    private val x: Int,
    private val y: Int,
    private val chessman: Chessman
  ) {

    fun execute(): Boolean {
      if (isWin) {
        return false
      }

      val index = indexOf(x, y)
      if (chessboard[index] != null) {
        return false
      }

      chessboard[index] = chessman

      if (isWin(x, y)) {
        winner = chessman
      }
      toggleChessman()
      return true
    }

    fun undo() {
      if (isWin) {
        winner = null
      }
      toggleChessman()

      val index = indexOf(x, y)
      chessboard[index] = null
    }

    private fun isWin(x: Int, y: Int): Boolean {
      return countHorizontalConnectedChessman(x, y) == GOMOKU_COUNT
          || countVerticalConnectedChessman(x, y) == GOMOKU_COUNT
          || countDiagonalConnectedChessman(x, y) == GOMOKU_COUNT
          || countInverseDiagonalConnectedChessman(x, y) == GOMOKU_COUNT
    }
  }

  class Builder {

    private val chessboard = arrayOfNulls<Chessman>(CHESSBOARD_WIDTH * CHESSBOARD_HEIGHT)

    fun putChessman(x: Int, y: Int, chessman: Chessman): Builder {
      checkCoordinate(x, y)
      chessboard[indexOf(x, y)] = chessman
      return this
    }

    fun build(): GomokuGame {
      return GomokuGame(chessboard)
    }
  }
}