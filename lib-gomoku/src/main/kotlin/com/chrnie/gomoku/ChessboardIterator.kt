package com.chrnie.gomoku

sealed class ChessboardIterator(val game: GomokuGame, val originX: Int, val originY: Int) {

    var x: Int = originX
        protected set
    var y: Int = originY
        protected set

    fun reset() {
        x = originX
        y = originY
    }

    abstract fun hasNext(): Boolean

    abstract fun next(): Chessman?

    abstract fun hasPrevious(): Boolean

    abstract fun previous(): Chessman?

    protected fun checkHasNext() {
        if (!hasNext()) {
            throw RuntimeException("do not has next")
        }
    }

    protected fun checkHasPrevious() {
        if (!hasPrevious()) {
            throw RuntimeException("do not has previous")
        }
    }
}

class HorizontalIterator(game: GomokuGame, x: Int, y: Int) : ChessboardIterator(game, x, y) {
    override fun hasNext(): Boolean {
        return x + 1 < GomokuGame.CHESSBOARD_WIDTH
    }

    override fun next(): Chessman? {
        checkHasNext()

        x += 1
        return game.chessmanAt(x, y)
    }

    override fun hasPrevious(): Boolean {
        return x - 1 >= 0
    }

    override fun previous(): Chessman? {
        checkHasPrevious()

        x -= 1
        return game.chessmanAt(x, y)
    }
}

class VerticalIterator(game: GomokuGame, x: Int, y: Int) : ChessboardIterator(game, x, y) {
    override fun hasNext(): Boolean {
        return y + 1 < GomokuGame.CHESSBOARD_HEIGHT
    }

    override fun next(): Chessman? {
        checkHasNext()

        y += 1
        return game.chessmanAt(x, y)
    }

    override fun hasPrevious(): Boolean {
        return y - 1 >= 0
    }

    override fun previous(): Chessman? {
        checkHasPrevious()

        y -= 1
        return game.chessmanAt(x, y)
    }
}

class DiagonalIterator(game: GomokuGame, x: Int, y: Int) : ChessboardIterator(game, x, y) {
    override fun hasNext(): Boolean {
        return (x + 1 < GomokuGame.CHESSBOARD_WIDTH) && (y + 1 < GomokuGame.CHESSBOARD_HEIGHT)
    }

    override fun next(): Chessman? {
        checkHasNext()

        x += 1
        y += 1
        return game.chessmanAt(x, y)
    }

    override fun hasPrevious(): Boolean {
        return (x - 1 >= 0) && (y - 1 >= 0)
    }

    override fun previous(): Chessman? {
        checkHasPrevious()

        x -= 1
        y -= 1
        return game.chessmanAt(x, y)
    }
}

class InverseDiagonalIterator(game: GomokuGame, x: Int, y: Int) : ChessboardIterator(game, x, y) {
    override fun hasNext(): Boolean {
        return (x + 1 < GomokuGame.CHESSBOARD_WIDTH) && (y - 1 >= 0)
    }

    override fun next(): Chessman? {
        checkHasNext()

        x += 1
        y -= 1
        return game.chessmanAt(x, y)
    }

    override fun hasPrevious(): Boolean {
        return (x - 1 >= 0) && (y + 1 < GomokuGame.CHESSBOARD_HEIGHT)
    }

    override fun previous(): Chessman? {
        checkHasPrevious()

        x -= 1
        y += 1
        return game.chessmanAt(x, y)
    }
}