package com.chrnie.gomoku

import org.junit.Assert.assertEquals
import org.junit.Test

class CoordinateTest {
    
    @Test
    fun `test x,y`() {
        Coordinate(0,0).run {
            assertEquals(0, x)
            assertEquals(0, y)
        }

        Coordinate(Int.MAX_VALUE, Int.MIN_VALUE).run {
            assertEquals(Int.MAX_VALUE, x)
            assertEquals(Int.MIN_VALUE, y)
        }

        Coordinate(Int.MIN_VALUE, Int.MAX_VALUE).run {
            assertEquals(Int.MIN_VALUE, x)
            assertEquals(Int.MAX_VALUE, y)
        }
    }
    
    @Test
    fun `test destructuring`() {
        val (x, y) = Coordinate(1, -1)
        assertEquals(1, x)
        assertEquals(-1, y)
    }
    
    @Test
    fun `test equals`() {
        assertEquals(Coordinate(0, 0), Coordinate(0, 0))
    }

    @Test
    fun testCoordinate_move() {
        Coordinate(0, 0)
                .move(Direction.TOP, 1)
                .also { assertEquals(Coordinate(0, 1), it) }

        Coordinate(0, 0)
                .move(Direction.BOTTOM, 1)
                .also { assertEquals(Coordinate(0, -1), it) }

        Coordinate(0, 0)
                .move(Direction.LEFT, 1)
                .also { assertEquals(Coordinate(-1, 0), it) }

        Coordinate(0, 0)
                .move(Direction.RIGHT, 1)
                .also { assertEquals(Coordinate(1, 0), it) }


        Coordinate(0, 0)
                .move(Direction.TOP_LEFT, 1)
                .also { assertEquals(Coordinate(-1, 1), it) }

        Coordinate(0, 0)
                .move(Direction.TOP_RIGHT, 1)
                .also { assertEquals(Coordinate(1, 1), it) }


        Coordinate(0, 0)
                .move(Direction.BOTTOM_LEFT, 1)
                .also { assertEquals(Coordinate(-1, -1), it) }

        Coordinate(0, 0)
                .move(Direction.BOTTOM_RIGHT, 1)
                .also { assertEquals(Coordinate(1, -1), it) }
    }
}