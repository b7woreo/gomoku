package com.chrnie.gomoku

import java.util.*

open class GomokuGame {
  companion object {
    const val CHESSBOARD_WIDTH = 15
    const val CHESSBOARD_HEIGHT = 15
    const val GOMOKU_COUNT = 5

    fun isCoordinateInBoard(x: Int, y: Int): Boolean {
      return (x in 0..(CHESSBOARD_WIDTH - 1)) && (y in 0..(CHESSBOARD_HEIGHT - 1))
    }

    internal fun ensureCoordinate(x: Int, y: Int) {
      if (x < 0 || x >= CHESSBOARD_WIDTH) {
        throw RuntimeException("x not in range: 0 - ${CHESSBOARD_WIDTH - 1}, current is: $x")
      }

      if (y < 0 || y >= CHESSBOARD_HEIGHT) {
        throw RuntimeException("y not in range: 0 - ${CHESSBOARD_HEIGHT - 1}, current is $y")
      }
    }

    private fun indexOf(x: Int, y: Int): Int = CHESSBOARD_WIDTH * y + x
  }

  private val chessboard: Array<Chessman?> = arrayOfNulls(CHESSBOARD_WIDTH * CHESSBOARD_HEIGHT)
  private val actionQueue = ArrayDeque<Action>()


  var chessman = Chessman.BLACK
    private set

  var winner: Chessman? = null
    private set

  val isWin get() = winner != null

  fun chessmanAt(x: Int, y: Int): Chessman? {
    ensureCoordinate(x, y)
    return chessboard[indexOf(x, y)]
  }

  fun putChessman(x: Int, y: Int): Boolean {
    ensureCoordinate(x, y)

    val action = Action(x, y, chessman)
    val success = action.execute()
    if (success) {
      actionQueue.push(action)
      onPutChessman(action.x, action.y, action.chessman)
    }
    return success
  }

  open fun onPutChessman(x: Int, y: Int, chessman: Chessman) {

  }


  val canUndo get() = !actionQueue.isEmpty()

  fun undo(): Boolean {
    if (actionQueue.isEmpty()) {
      return false
    }

    val action = actionQueue.pop()
    action.undo()
    onUndo(action.x, action.y, action.chessman)
    return true
  }

  open fun onUndo(x: Int, y: Int, chessman: Chessman) {

  }

  private fun toggleChessman() {
    this.chessman = if (chessman == Chessman.BLACK) Chessman.WHITE else Chessman.BLACK
  }

  private inner class Action(
    val x: Int,
    val y: Int,
    val chessman: Chessman
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
      return countConnectedChessman(x, y, CoordMapping.Rotate0) == GOMOKU_COUNT
          || countConnectedChessman(x, y, CoordMapping.Rotate45) == GOMOKU_COUNT
          || countConnectedChessman(x, y, CoordMapping.Rotate90) == GOMOKU_COUNT
          || countConnectedChessman(x, y, CoordMapping.Rotate135) == GOMOKU_COUNT
    }

    private fun countConnectedChessman(x: Int, y: Int, mapping: CoordMapping): Int {
      var count = 1

      val out = intArrayOf(-1, -1)

      for (dX in 1..Int.MAX_VALUE) {
        mapping.map(x, y, dX, 0, out)
        if (!isCoordinateInBoard(out[0], out[1])) {
          break
        }

        val c = chessmanAt(out[0], out[1])
        if (c != chessman) {
          break
        }

        count += 1
      }

      for (dX in -1 downTo Int.MIN_VALUE) {
        mapping.map(x, y, dX, 0, out)
        if (!isCoordinateInBoard(out[0], out[1])) {
          break
        }

        val c = chessmanAt(out[0], out[1])
        if (c != chessman) {
          break
        }

        count += 1
      }

      return count
    }
  }
}