package com.chrnie.gomoku

internal class BitBoard private constructor(
    override val width: Int,
    override val height: Int,
    words: LongArray?
) : Board {

    companion object {
        /*
        * BITS_PER_CHESSMAN == 2
        * BITS_PER_WORD == 64
        * (BITS_PER_WORD / BITS_PER_CHESSMAN) == 32
        * (n / 32) == (n >> 5)
        */
        private const val ADDRESS_CHESSMAN_PER_WORD = 5

        private const val CHESSMAN_NONE = 0b00L
        private const val CHESSMAN_BLACK = 0b01L
        private const val CHESSMAN_WHITE = 0b10L
        private const val CHESSMAN_MASK = 0b11L
    }

    private val _words: LongArray

    init {
        if (width < 0 || height < 0) throw IllegalArgumentException("Board width and height must >= 0, current is [$width, $height]")

        val chessmanIndex = chessmanIndex(width - 1, height - 1)
        _words = words ?: LongArray(wordIndex(chessmanIndex) + 1)
    }

    internal constructor(width: Int, height: Int) : this(width, height, null)

    override operator fun get(x: Int, y: Int): Chessman? {
        ensureCoordinateInBounds(x, y)

        val chessmanIndex = chessmanIndex(x, y)
        val wordIndex = wordIndex(chessmanIndex)
        val word = _words[wordIndex]
        val shift = chessmanIndex shl 1
        val mask = word and (CHESSMAN_MASK shl shift)

        if (mask xor (CHESSMAN_NONE shl shift) == 0L) {
            return null
        }

        if (mask xor (CHESSMAN_BLACK shl shift) == 0L) {
            return Chessman.BLACK
        }

        if (mask xor (CHESSMAN_WHITE shl shift) == 0L) {
            return Chessman.WHITE
        }

        throw IllegalStateException("Unknown mask: $mask")
    }

    override fun put(x: Int, y: Int, chessman: Chessman?): Board {
        ensureCoordinateInBounds(x, y)

        val curChessman = get(x, y)
        if (curChessman == chessman) return this

        val wordSize = _words.size
        val newWords = LongArray(wordSize)
        System.arraycopy(_words, 0, newWords, 0, wordSize)

        val chessmanIndex = chessmanIndex(x, y)
        val wordIndex = wordIndex(chessmanIndex)
        val shift = chessmanIndex shl 1

        newWords[wordIndex] = newWords[wordIndex] and (CHESSMAN_MASK shl shift).inv()

        when (chessman) {
            null -> {
                newWords[wordIndex] = newWords[wordIndex] or (CHESSMAN_NONE shl shift)
            }
            Chessman.BLACK -> {
                newWords[wordIndex] = newWords[wordIndex] or (CHESSMAN_BLACK shl shift)
            }
            Chessman.WHITE -> {
                newWords[wordIndex] = newWords[wordIndex] or (CHESSMAN_WHITE shl shift)
            }
        }

        return BitBoard(width, height, newWords)
    }

    private fun ensureCoordinateInBounds(x: Int, y: Int) {
        if (x !in 0 until width || y !in 0 until height) {
            throw CoordinateOutOfBounds(x, y, width, height)
        }
    }

    private fun chessmanIndex(x: Int, y: Int): Int {
        return y * width + x
    }

    private fun wordIndex(chessmanIndex: Int): Int {
        return chessmanIndex shr ADDRESS_CHESSMAN_PER_WORD
    }
}