package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Coordinate
import com.chrnie.gomoku.Gomoku
import com.chrnie.gomoku.toGomoku
import junit.framework.TestCase.assertEquals
import org.junit.Test

class GomokuExtTest {

    @Test
    fun next() {
        assertEquals(listOf(Coordinate(7, 7)), Gomoku().next(4))
        
        """
            . x 
            o o 
            . x 
        """.trimIndent()
            .toGomoku()
            .run {
                assertEquals(
                    listOf(
                        Coordinate(8, 7),
                        Coordinate(6, 5)
                    ),
                    this.next(4)
                )
            }

        """
            . . . o
            x . x .
            . x o o 
            x . x .
            . . . o
        """.trimIndent()
            .toGomoku()
            .run {
                assertEquals(
                    listOf(
                        Coordinate(8, 6),
                        Coordinate(8, 8)
                    ),
                    next(1)
                )
            }
    }

}