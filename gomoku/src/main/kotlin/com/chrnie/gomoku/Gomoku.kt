package com.chrnie.gomoku

interface Gomoku {

    companion object {
        const val GOMOKU_COUNT = 5
        const val BOARD_WIDTH = 15
        const val BOARD_HEIGHT = 15

        fun isCoordinateInBoard(coordinate: Coordinate): Boolean {
            return isCoordinateInBoard(coordinate.x, coordinate.y)
        }

        fun isCoordinateInBoard(x: Int, y: Int): Boolean {
            return x in 0 until BOARD_WIDTH && y in 0 until BOARD_HEIGHT
        }
    }

    val focus: Coordinate?

    val board: Board

    val isWon: Boolean

    val player: Player

    val winner: Player

    fun put(coordinate: Coordinate): Gomoku = put(coordinate.x, coordinate.y)

    fun put(x: Int, y: Int): Gomoku

    val canUndo: Boolean

    fun undo(): Gomoku

}

fun Gomoku(): Gomoku = LinkedGomoku()

sealed class Player {

    abstract val stone: Stone

    abstract val opponent: Player

}

object FirstHandPlayer : Player() {

    override val stone: Stone = Stone.BLACK

    override val opponent: Player = BackHandPlayer

}

object BackHandPlayer : Player() {

    override val stone: Stone = Stone.WHITE

    override val opponent: Player = FirstHandPlayer

}

fun String.toGomoku(
    horizontalAlign: HorizontalAlign = HorizontalAlign.CENTER,
    verticalAlign: VerticalAlign = VerticalAlign.CENTER
): Gomoku {
    val board = this.toBoard(
        Gomoku.BOARD_WIDTH, Gomoku.BOARD_HEIGHT,
        horizontalAlign, verticalAlign
    )
    
    val blackSequence = iterator {
        for (x in 0 until board.width) {
            for (y in 0 until board.height) {
                if (board[x, y] == Stone.BLACK) {
                    yield(Coordinate(x, y))
                }
            }
        }
    }
    val whiteSequence = iterator {
        for (x in 0 until board.width) {
            for (y in 0 until board.height) {
                if (board[x, y] == Stone.WHITE){
                    yield(Coordinate(x, y))
                }
            }
        }
    }

    var gomoku = Gomoku()
    while (true) {
        gomoku = if (blackSequence.hasNext() && whiteSequence.hasNext()) {
            gomoku.put(blackSequence.next())
                .put(whiteSequence.next())
        } else if (blackSequence.hasNext() && !whiteSequence.hasNext()) {
            gomoku.put(blackSequence.next())
        } else if (!blackSequence.hasNext() && !whiteSequence.hasNext()) {
            break
        } else {
            throw IllegalArgumentException()
        }
    }
    return gomoku
}