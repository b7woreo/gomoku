package com.chrnie.gomoku

class CoordinateOutOfBoundsException(x: Int, y: Int, width: Int, height: Int) : RuntimeException(
    "Coordinate ($x, $y) not in range (0 - ${width - 1}, 0 - ${height - 1})"
)