package com.chrnie.gomoku

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class BoardTest {

    @Test
    fun testBoard() {
        val boardWidth = 15
        val boardHeight = 15
        val board0 = Board(boardWidth, boardHeight)
        for (x in 0..14) {
            for (y in 0..14) {
                assertEquals(null, board0[x, y])
            }
        }

        val board1 = board0.put(0, 0, Chessman.WHITE)
        assertEquals(null, board0[0, 0])
        assertEquals(Chessman.WHITE, board1[0, 0])

        val board2 = board0.put(0, 0, Chessman.BLACK)
        assertEquals(null, board0[0, 0])
        assertEquals(Chessman.BLACK, board2[0, 0])

        val board3 = board1.put(0, 0, null)
        assertEquals(null, board3[0, 0])
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConstructException() {
        Board(-1, -1)
    }

    @Test(expected = CoordinateOutOfBounds::class)
    fun testGetException() {
        val board = Board(1, 1)
        board[1, 1]
    }

    @Test
    fun testBoardPerformance() {
        val random = Random()
        var board = Board(15, 15)

        val time = timer("board") {
            for (i in 0..1000000) {
                board = board.put(
                    random.nextInt(15), random.nextInt(15),
                    if (random.nextBoolean()) null else Chessman.WHITE
                )
            }
        }

        assertEquals(true, time < 250L)
    }

    private inline fun timer(tag: String, block: () -> Unit): Long {
        val startTime = System.currentTimeMillis()
        block()
        val time = System.currentTimeMillis() - startTime
        println("$tag coast: $time")
        return time
    }
}


