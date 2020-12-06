package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Gomoku
import org.openjdk.jmh.annotations.*

/**
 * Benchmark                Mode  Cnt  Score   Error  Units
 * GomokuExtBenchmark.next  avgt   25  1.007 ± 0.051   s/op
 */
class GomokuAlphaBetaBenchmark {

    companion object {
        private const val DEPTH = 4
    }

    @BenchmarkMode(Mode.AverageTime)
    @State(Scope.Benchmark)
    open class Case0 {
        private lateinit var gomoku: Gomoku

        @Setup
        fun prepare() {
            gomoku = Gomoku()
        }

        @Benchmark
        fun next() = gomoku.next(DEPTH)
    }

    @BenchmarkMode(Mode.AverageTime)
    @State(Scope.Benchmark)
    open class Case1 {
        private lateinit var gomoku: Gomoku

        @Setup
        fun prepare() {
            gomoku = Gomoku()
                .put(7, 7)
                .put(8, 8)
                .put(7, 9)
                .put(7, 8)
                .put(6, 8)
                .put(8, 6)
                .put(5, 7)
                .put(8, 10)
                .put(5, 9)
        }

        @Benchmark
        fun next() = gomoku.next(DEPTH)
    }

    @BenchmarkMode(Mode.AverageTime)
    @State(Scope.Benchmark)
    open class Case2 {
        private lateinit var gomoku: Gomoku

        @Setup
        fun prepare() {
            gomoku = Gomoku()
                .put(7, 7)
                .put(8, 8)
                .put(7, 9)
                .put(7, 8)
                .put(5, 7)
                .put(8, 9)
                .put(5, 9)
                .put(8, 7)
        }

        @Benchmark
        fun next() = gomoku.next(DEPTH)
    }

    @BenchmarkMode(Mode.AverageTime)
    @State(Scope.Benchmark)
    open class Case3 {
        private lateinit var gomoku: Gomoku

        @Setup
        fun prepare() {
            gomoku = Gomoku()
                .put(7, 6)
                .put(6, 6)
                .put(7, 7)
                .put(6, 7)
                .put(7, 8)
                .put(6, 8)
                .put(7, 9)
                .put(6, 9)
        }

        @Benchmark
        fun next() = gomoku.next(DEPTH)
    }

}