package com.chrnie.gomoku

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.util.*

class CoordMappingTest {

  @Test
  fun testMapping() {
    val r = Random(System.currentTimeMillis())
    val delta = 2
    val centerX = 5
    val centerY = 5

    val a = arrayOf(centerX - delta, centerY + delta)
    val b = arrayOf(centerX, centerY + delta)
    val c = arrayOf(centerX + delta, centerY + delta)
    val d = arrayOf(centerX - delta, centerY)
    val e = arrayOf(centerX, centerY)
    val f = arrayOf(centerX + delta, centerY)
    val g = arrayOf(centerX - delta, centerY - delta)
    val h = arrayOf(centerX, centerY - delta)
    val i = arrayOf(centerX + delta, centerY - delta)

    testMapping(CoordMapping.Rotate0, centerX, centerY, delta, b, h, d, f)
    testMapping(CoordMapping.Rotate45, centerX, centerY, delta, a, i, g, c)
    testMapping(CoordMapping.Rotate90, centerX, centerY, delta, d, f, h, b)
    testMapping(CoordMapping.Rotate135, centerX, centerY, delta, g, c, i, a)
  }

  private fun testMapping(
    mapping: CoordMapping,
    centerX: Int, centerY: Int, delta: Int,
    top: Array<Int>, bottom: Array<Int>,
    left: Array<Int>, right: Array<Int>
  ) {
    val out = arrayOf(-1, -1)
    mapping.map(centerX, centerY, 0, delta, out)
    assertArrayEquals(top, out)
    mapping.map(centerX, centerY, 0, -delta, out)
    assertArrayEquals(bottom, out)
    mapping.map(centerX, centerY, -delta, 0, out)
    assertArrayEquals(left, out)
    mapping.map(centerX, centerY, delta, 0, out)
    assertArrayEquals(right, out)
  }
}