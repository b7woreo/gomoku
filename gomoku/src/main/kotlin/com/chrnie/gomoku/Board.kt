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