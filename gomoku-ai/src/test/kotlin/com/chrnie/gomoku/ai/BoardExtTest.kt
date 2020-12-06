package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Board
import com.chrnie.gomoku.Stone
import org.junit.Test

class BoardExtTest {

    @Test
    fun evaluate() {
        Board(15, 15).assertScore { it[Stone.BLACK] == it[Stone.WHITE] }

            .put(6, 6, Stone.BLACK).assertScore { it[Stone.BLACK] > it[Stone.WHITE] }
            .put(5, 5, Stone.WHITE).assertScore { it[Stone.BLACK] == it[Stone.WHITE] }

            .put(5, 6, Stone.BLACK).assertScore { it[Stone.BLACK] > it[Stone.WHITE] }
            .put(4, 6, Stone.WHITE).assertScore { it[Stone.BLACK] == it[Stone.WHITE] }

            .put(6, 4, Stone.BLACK).assertScore { it[Stone.BLACK] > it[Stone.WHITE] }
            .put(6, 5, Stone.WHITE).assertScore { it[Stone.WHITE] > it[Stone.BLACK] }

            .put(4, 5, Stone.BLACK).assertScore { it[Stone.BLACK] > it[Stone.WHITE] }
            .put(7, 4, Stone.WHITE).assertScore { it[Stone.BLACK] > it[Stone.WHITE] }

            .put(6, 7, Stone.BLACK).assertScore { it[Stone.BLACK] > it[Stone.WHITE] }
            .put(7, 5, Stone.WHITE).assertScore { it[Stone.BLACK] > it[Stone.WHITE] }
    }

    private inline fun Board.assertScore(block: (Score) -> Boolean): Board {
        val score = this.evaluate()
        block(score)
        return this
    }
}