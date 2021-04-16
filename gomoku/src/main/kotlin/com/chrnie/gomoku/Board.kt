package com.chrnie.gomoku

interface Board {

    val width: Int

    val height: Int

    operator fun get(coordinate: Coordinate): Stone? = get(coordinate.x, coordinate.y)

    operator fun get(x: Int, y: Int): Stone?

    fun put(coordinate: Coordinate, stone: Stone?): Board = put(coordinate.x, coordinate.y, stone)

    fun put(x: Int, y: Int, stone: Stone?): Board

}

fun Board(width: Int, height: Int): Board = BitBoard(width, height)

enum class Stone {
    BLACK,
    WHITE
}

enum class VerticalAlign {
    TOP,
    CENTER,
    BOTTOM
}

enum class HorizontalAlign {
    LEFT,
    CENTER,
    RIGHT
}

fun String.toBoard(
    width: Int,
    height: Int,
    horizontalAlign: HorizontalAlign = HorizontalAlign.CENTER,
    verticalAlign: VerticalAlign = VerticalAlign.CENTER
): Board {
    val stones = this.split("\n")
        .map { line ->
            line
                .filter { it != ' ' }
                .map {
                    when (it) {
                        'x' -> Stone.BLACK
                        'o' -> Stone.WHITE
                        else -> null
                    }
                }
        }

    val h = stones.size
    if (h !in 1..height) throw IllegalArgumentException()
    val w = stones[0].size
    if (w !in 1..width) throw IllegalArgumentException()
    if (stones.any { it.size != w }) throw IllegalArgumentException()

    val xOffset = when (horizontalAlign) {
        HorizontalAlign.LEFT -> 0
        HorizontalAlign.CENTER -> (width - w) / 2
        HorizontalAlign.RIGHT -> width - w
    }

    val yOffset = when (verticalAlign) {
        VerticalAlign.TOP -> 0
        VerticalAlign.CENTER -> (width - w) / 2
        VerticalAlign.BOTTOM -> height - h
    }

    return stones.foldIndexed(Board(width, height)) { y, board, line ->
        line.foldIndexed(board) { x, it, stone ->
            it.put(x + xOffset, y + yOffset, stone)
        }
    }
}