package com.chrnie.gomoku

import org.junit.Assert.*
import org.junit.Test

class GomokuTest {

    @Test
    fun play() {
        Gomoku().also {
            assertFalse(it.canUndo)
            assertFalse(it.isWon)
            assertEquals(FirstHandPlayer, it.player)
            assertNull(it.focus)
            assertEquals(
                    IllegalStateException::class,
                    runCatching { it.winner }.exceptionOrNull()?.let { it::class }
            )
            assertEquals(
                    IllegalStateException::class,
                    runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
            )
        }
                .put(7, 7).also {
                    assertTrue(it.canUndo)
                    assertFalse(it.isWon)
                    assertEquals(BackHandPlayer, it.player)
                    assertEquals(Coordinate(7, 7), it.focus)
                    assertEquals(
                            IllegalStateException::class,
                            runCatching { it.winner }.exceptionOrNull()?.let { it::class }
                    )
                    assertEquals(
                            null,
                            runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
                    )
                }
                .put(1, 1).also {
                    assertTrue(it.canUndo)
                    assertFalse(it.isWon)
                    assertEquals(FirstHandPlayer, it.player)
                    assertEquals(Coordinate(1, 1), it.focus)
                    assertEquals(
                            IllegalStateException::class,
                            runCatching { it.winner }.exceptionOrNull()?.let { it::class }
                    )
                    assertEquals(
                            null,
                            runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
                    )
                }
                .put(7, 6).also {
                    assertTrue(it.canUndo)
                    assertFalse(it.isWon)
                    assertEquals(BackHandPlayer, it.player)
                    assertEquals(Coordinate(7, 6), it.focus)
                    assertEquals(
                            IllegalStateException::class,
                            runCatching { it.winner }.exceptionOrNull()?.let { it::class }
                    )
                    assertEquals(
                            null,
                            runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
                    )
                }
                .put(0, 14).also {
                    assertTrue(it.canUndo)
                    assertFalse(it.isWon)
                    assertEquals(FirstHandPlayer, it.player)
                    assertEquals(Coordinate(0, 14), it.focus)
                    assertEquals(
                            IllegalStateException::class,
                            runCatching { it.winner }.exceptionOrNull()?.let { it::class }
                    )
                    assertEquals(
                            null,
                            runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
                    )
                }
                .put(7, 5).also {
                    assertTrue(it.canUndo)
                    assertFalse(it.isWon)
                    assertEquals(BackHandPlayer, it.player)
                    assertEquals(Coordinate(7, 5), it.focus)
                    assertEquals(
                            IllegalStateException::class,
                            runCatching { it.winner }.exceptionOrNull()?.let { it::class }
                    )
                    assertEquals(
                            null,
                            runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
                    )
                }
                .put(14, 0).also {
                    assertTrue(it.canUndo)
                    assertFalse(it.isWon)
                    assertEquals(FirstHandPlayer, it.player)
                    assertEquals(Coordinate(14, 0), it.focus)
                    assertEquals(
                            IllegalStateException::class,
                            runCatching { it.winner }.exceptionOrNull()?.let { it::class }
                    )
                    assertEquals(
                            null,
                            runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
                    )
                }
                .put(7, 8).also {
                    assertTrue(it.canUndo)
                    assertFalse(it.isWon)
                    assertEquals(BackHandPlayer, it.player)
                    assertEquals(Coordinate(7, 8), it.focus)
                    assertEquals(
                            IllegalStateException::class,
                            runCatching { it.winner }.exceptionOrNull()?.let { it::class }
                    )
                    assertEquals(
                            null,
                            runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
                    )
                }
                .put(14, 14).also {
                    assertTrue(it.canUndo)
                    assertFalse(it.isWon)
                    assertEquals(FirstHandPlayer, it.player)
                    assertEquals(Coordinate(14, 14), it.focus)
                    assertEquals(
                            IllegalStateException::class,
                            runCatching { it.winner }.exceptionOrNull()?.let { it::class }
                    )
                    assertEquals(
                            null,
                            runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
                    )
                }
                .put(7, 9).also {
                    assertTrue(it.canUndo)
                    assertTrue(it.isWon)
                    assertEquals(BackHandPlayer, it.player)
                    assertEquals(Coordinate(7, 9), it.focus)
                    assertEquals(FirstHandPlayer, it.winner)
                    assertEquals(
                            null,
                            runCatching { it.undo() }.exceptionOrNull()?.let { it::class }
                    )
                    assertEquals(
                            IllegalStateException::class,
                            runCatching { it.put(1, 1) }.exceptionOrNull()?.let { it::class }
                    )
                }
    }

    @Test(expected = IllegalArgumentException::class)
    fun put_throwIfAlreadyPut() {
        Gomoku().put(1, 1)
                .put(1, 1)
    }

}