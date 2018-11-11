package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.GomokuGame
import com.chrnie.gomoku.ai.board.evaluator.BoardEvaluator
import org.junit.Assert.assertEquals
import org.junit.Test

class BoardEvaluatorTest {

  @Test
  fun testEvaluate() {
    val gomokuGame = GomokuGame()
    gomokuGame.putChessman(6, 6)
    gomokuGame.putChessman(5, 5)

    gomokuGame.putChessman(5, 6)
    gomokuGame.putChessman(4, 6)

    gomokuGame.putChessman(6, 4)
    gomokuGame.putChessman(6, 5)

    gomokuGame.putChessman(4, 5)
    gomokuGame.putChessman(7, 4)

    gomokuGame.putChessman(6, 7)
    gomokuGame.putChessman(7, 5)

    val result = BoardEvaluator.evaluate(gomokuGame, Chessman.WHITE)
    assertEquals(-830, result)
  }
}