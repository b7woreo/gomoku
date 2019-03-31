package com.chrnie.gomoku

import org.openjdk.jmh.annotations.*

/**
 * Benchmark                 Mode  Cnt          Score          Error  Units
 * BoardBenchmark.getStone  thrpt   25  293189518.899 ± 13016923.705  ops/s
 * BoardBenchmark.putStone  thrpt   25   66205301.634 ±  2883510.663  ops/s
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
open class BoardBenchmark {

    private lateinit var board: Board

    @Setup
    fun prepare() {
        board = Board(15, 15)
    }

    @Benchmark
    fun putStone() = board.put(0, 0, Chessman.BLACK)

    @Benchmark
    fun getStone() = board.get(0, 0)
}