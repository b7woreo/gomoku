package com.chrnie.gomoku

import org.junit.Assert.assertEquals
import org.junit.Test

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

        val board1 = board0.put(0, 0, Stone.WHITE)
        assertEquals(null, board0[0, 0])
        assertEquals(Stone.WHITE, board1[0, 0])

        val board2 = board0.put(0, 0, Stone.BLACK)
        assertEquals(null, board0[0, 0])
        assertEquals(Stone.BLACK, board2[0, 0])

        val board3 = board1.put(0, 0, null)
        assertEquals(null, board3[0, 0])
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConstructException() {
        Board(0, 0)
    }

    @Test(expected = CoordinateOutOfBoundsException::class)
    fun testGetException() {
        val board = Board(1, 1)
        board[1, 1]
    }
}


