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

        private const val MASK_CHESSMAN_IN_USE = 0b10L
        private const val MASK_CHESSMAN_IS_BLACK = 0b01L
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

        if ((word and MASK_CHESSMAN_IN_USE shl (chessmanIndex shl 1)) == 0L) return null
        return if ((word and MASK_CHESSMAN_IS_BLACK shl (chessmanIndex shl 1)) != 0L) Chessman.BLACK else Chessman.WHITE
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

        when (chessman) {
            null -> {
                newWords[wordIndex] = newWords[wordIndex] and
                        (MASK_CHESSMAN_IN_USE shl (chessmanIndex shl 1)).inv()
            }

            Chessman.BLACK -> {
                newWords[wordIndex] = newWords[wordIndex] or
                        (MASK_CHESSMAN_IN_USE shl (chessmanIndex shl 1))
                newWords[wordIndex] = newWords[wordIndex] or
                        (MASK_CHESSMAN_IS_BLACK shl (chessmanIndex shl 1))
            }

            Chessman.WHITE -> {
                newWords[wordIndex] = newWords[wordIndex] or
                        (MASK_CHESSMAN_IN_USE shl (chessmanIndex shl 1))
                newWords[wordIndex] = newWords[wordIndex] and
                        (MASK_CHESSMAN_IS_BLACK shl (chessmanIndex shl 1)).inv()
            }
        }

        return BitBoard(width, height, newWords)
    }

    private fun ensureCoordinateInBounds(x: Int, y: Int) {
        if (x !in 0..(width - 1) || y !in 0..(height - 1)) {
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