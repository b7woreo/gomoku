package com.chrnie.gomoku

fun Coordinate(x: Int, y: Int): Coordinate =
    CoordinateImpl((x.toLong() shl Int.SIZE_BITS) or (y.toLong() and 0xFFFFFFFF))

interface Coordinate {
    val x: Int
    val y: Int

    fun move(direction: Direction, delta: Int): Coordinate

    operator fun component1() = x

    operator fun component2() = y
}

enum class Direction(val dx: Int, val dy: Int) {

    TOP(0, 1) {
        override fun reverse(): Direction = BOTTOM
    },

    BOTTOM(0, -1) {
        override fun reverse(): Direction = TOP
    },

    LEFT(-1, 0) {
        override fun reverse(): Direction = RIGHT
    },

    RIGHT(1, 0) {
        override fun reverse(): Direction = LEFT
    },

    TOP_LEFT(-1, 1) {
        override fun reverse(): Direction = BOTTOM_RIGHT
    },

    TOP_RIGHT(1, 1) {
        override fun reverse(): Direction = BOTTOM_LEFT
    },

    BOTTOM_LEFT(-1, -1) {
        override fun reverse(): Direction = TOP_RIGHT
    },

    BOTTOM_RIGHT(1, -1) {
        override fun reverse(): Direction = TOP_LEFT
    };

    abstract fun reverse(): Direction

}

private inline class CoordinateImpl(private val value: Long) : Coordinate {

    override val x: Int
        get() = (value shr Int.SIZE_BITS).toInt()

    override val y: Int
        get() = value.toInt()

    override fun move(direction: Direction, delta: Int): Coordinate {
        return Coordinate(x + (direction.dx * delta), y + (direction.dy * delta))
    }

    override fun toString(): String = "($x, $y)"
}


