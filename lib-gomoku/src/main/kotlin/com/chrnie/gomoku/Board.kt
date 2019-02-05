package com.chrnie.gomoku

interface Board {

    val width: Int

    val height: Int

    operator fun get(x: Int, y: Int): Chessman?

    fun put(x: Int, y: Int, chessman: Chessman?): Board

}

fun Board(width: Int, height: Int): Board = BitBoard(width, height)

enum class Chessman {
    BLACK,
    WHITE
}