package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Coordinate
import com.chrnie.gomoku.Gomoku
import junit.framework.TestCase.assertEquals
import org.junit.Test

class GomokuExtTest {

    @Test
    fun next() {
        assertEquals(listOf(Coordinate(7, 7)), Gomoku().next(4))

        Gomoku()
            .put(7, 7)
            .put(8, 8)
            .put(7, 9)
            .put(7, 8)
            .put(6, 8)
            .put(8, 6)
            .put(5, 7)
            .put(8, 10)
            .put(5, 9)
            .run {
                assertEquals(listOf(Coordinate(8, 9), Coordinate(8, 11)), next(4))
            }
    }

}