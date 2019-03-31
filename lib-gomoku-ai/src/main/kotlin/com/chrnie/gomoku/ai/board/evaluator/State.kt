package com.chrnie.gomoku.ai.board.evaluator

import com.chrnie.gomoku.Stone

sealed class State {

  companion object {
    val START: State = StartState

    private val fsmTable = mapOf(
      Stone.WHITE to mapOf(
        true to mapOf(
          1 to ChessmanState(Stone.WHITE, 1, true),
          2 to ChessmanState(Stone.WHITE, 2, true),
          3 to ChessmanState(Stone.WHITE, 3, true),
          4 to ChessmanState(Stone.WHITE, 4, true),
          5 to ChessmanState(Stone.WHITE, 5, true)
        ),
        false to mapOf(
          1 to ChessmanState(Stone.WHITE, 1, false),
          2 to ChessmanState(Stone.WHITE, 2, false),
          3 to ChessmanState(Stone.WHITE, 3, false),
          4 to ChessmanState(Stone.WHITE, 4, false),
          5 to ChessmanState(Stone.WHITE, 5, false)
        )
      ),
      Stone.BLACK to mapOf(
        true to mapOf(
          1 to ChessmanState(Stone.BLACK, 1, true),
          2 to ChessmanState(Stone.BLACK, 2, true),
          3 to ChessmanState(Stone.BLACK, 3, true),
          4 to ChessmanState(Stone.BLACK, 4, true),
          5 to ChessmanState(Stone.BLACK, 5, true)
        ),
        false to mapOf(
          1 to ChessmanState(Stone.BLACK, 1, false),
          2 to ChessmanState(Stone.BLACK, 2, false),
          3 to ChessmanState(Stone.BLACK, 3, false),
          4 to ChessmanState(Stone.BLACK, 4, false),
          5 to ChessmanState(Stone.BLACK, 5, false)
        )
      )
    )
  }

  abstract fun next(input: Input, output: Output): State

  protected fun fsm(stone: Stone, count: Int, dead: Boolean): State {
    return fsmTable.let { it[stone] }?.let { it[dead] }?.let { it[count] }
      ?: throw RuntimeException("can not find match fsm")
  }

  enum class Input {
    NONE,
    BLACK,
    WHITE,
    END
  }

  class Output {

    private var blackScore = 0
    private var whiteScore = 0

    operator fun get(stone: Stone) = when (stone) {
      Stone.BLACK -> blackScore
      Stone.WHITE -> whiteScore
    }

    operator fun set(stone: Stone, score: Int) = when (stone) {
      Stone.BLACK -> blackScore = score
      Stone.WHITE -> whiteScore = score
    }
  }
}

private object StartState : State() {

  override fun next(input: Input, output: Output): State = when (input) {
    Input.NONE -> NoneState
    Input.BLACK -> fsm(Stone.BLACK, 1, true)
    Input.WHITE -> fsm(Stone.WHITE, 1, true)
    Input.END -> throw RuntimeException("can not input end to start fsm")
  }

}

private object NoneState : State() {

  override fun next(input: Input, output: Output): State = when (input) {
    Input.NONE -> NoneState
    Input.BLACK -> fsm(Stone.BLACK, 1, false)
    Input.WHITE -> fsm(Stone.WHITE, 1, false)
    Input.END -> EndState
  }

}

private class ChessmanState(val stone: Stone, val count: Int, val dead: Boolean) : State() {

  companion object {
    private val scoreTable = mapOf(
      1 to mapOf(false to 10, true to 10),
      2 to mapOf(false to 100, true to 10),
      3 to mapOf(false to 1000, true to 100),
      4 to mapOf(false to 10000, true to 1000),
      5 to mapOf(false to 100000, true to 100000)
    )

    private fun findScore(count: Int, dead: Boolean): Int {
      return scoreTable.let { it[count] }?.let { it[dead] }
        ?: throw RuntimeException("can not find match score")
    }
  }

  override fun next(input: Input, output: Output): State = when (input) {
    Input.NONE -> {
      output[stone] += findScore(count, dead)
      NoneState
    }
    Input.WHITE -> {
      if (stone == Stone.WHITE) fsm(Stone.WHITE, count + 1, dead)
      else {
        if (!dead) output[stone] += findScore(count, true)
        fsm(Stone.WHITE, 1, true)
      }
    }
    Input.BLACK -> {
      if (stone == Stone.BLACK) fsm(Stone.BLACK, count + 1, dead)
      else {
        if (!dead) output[stone] += findScore(count, true)
        fsm(Stone.BLACK, 1, true)
      }
    }
    Input.END -> {
      if (!dead) output[stone] += findScore(count, true)
      EndState
    }
  }
}

private object EndState : State() {

  override fun next(input: Input, output: Output): State {
    throw RuntimeException("can not input to end fsm")
  }
}


