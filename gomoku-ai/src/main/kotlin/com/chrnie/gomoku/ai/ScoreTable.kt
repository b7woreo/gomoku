package com.chrnie.gomoku.ai

object ScoreTable {

    private val scoreTable = mapOf(
        1 to mapOf(false to 10, true to 10),
        2 to mapOf(false to 100, true to 10),
        3 to mapOf(false to 1000, true to 100),
        4 to mapOf(false to 10000, true to 1000),
        5 to mapOf(false to 100000, true to 100000)
    )

    operator fun get(continues: Int, dead: Boolean): Int {
        return scoreTable.let { it[continues] }?.let { it[dead] } ?: 0
    }

}