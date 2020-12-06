package com.chrnie.gomoku

import org.junit.Assert.assertEquals
import org.junit.Test

class CoordinateTest {

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