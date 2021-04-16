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
    
    @Test
    fun testToBoard() {
        """
            . o x 
            . x o 
        """.trimIndent()
            .toBoard(15, 15, HorizontalAlign.LEFT, VerticalAlign.TOP)
            .also {
                assertEquals(null, it[0, 0])
                assertEquals(Stone.WHITE, it[1, 0])
                assertEquals(Stone.BLACK, it[2, 0])
                assertEquals(null, it[0, 1])
                assertEquals(Stone.BLACK, it[1, 1])
                assertEquals(Stone.WHITE, it[2, 1])
            }

        """
            . o x 
            . x o 
        """.trimIndent()
            .toBoard(15, 15)
            .also {
                assertEquals(null, it[6, 6])
                assertEquals(Stone.WHITE, it[7, 6])
                assertEquals(Stone.BLACK, it[8, 6])
                assertEquals(null, it[6, 7])
                assertEquals(Stone.BLACK, it[7, 7])
                assertEquals(Stone.WHITE, it[8, 7])
            }

        """
            . o x 
            . x o 
        """.trimIndent()
            .toBoard(15, 15, HorizontalAlign.RIGHT, VerticalAlign.BOTTOM)
            .also {
                assertEquals(null, it[12, 13])
                assertEquals(Stone.WHITE, it[13, 13])
                assertEquals(Stone.BLACK, it[14, 13])
                assertEquals(null, it[12, 14])
                assertEquals(Stone.BLACK, it[13, 14])
                assertEquals(Stone.WHITE, it[14, 14])
            }
    }
    
    @Test
    fun testToString() {
        val board = Board(4, 4)
            .put(1, 1, Stone.BLACK)
            .put(2, 2, Stone.WHITE)
        
        assertEquals(
            """
               . . . .
               . x . .
               . . o .
               . . . .
            """.trimIndent(),
            board.toString()
        )
    }
}


