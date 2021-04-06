package gomoku.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chrnie.gomoku.Gomoku
import com.chrnie.gomoku.ai.next
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GomokuAlphaBetaBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun case0() {
        val gomoku = Gomoku()
        benchmarkRule.measureRepeated { gomoku.next(DEPTH) }
    }

    @Test
    fun case1() {
        val gomoku = Gomoku()
            .put(7, 7)
            .put(8, 8)
            .put(7, 9)
            .put(7, 8)
            .put(6, 8)
            .put(8, 6)
            .put(5, 7)
            .put(8, 10)
            .put(5, 9)
        benchmarkRule.measureRepeated { gomoku.next(DEPTH) }
    }

    @Test
    fun case2() {
        val gomoku = Gomoku()
            .put(7, 7)
            .put(8, 8)
            .put(7, 9)
            .put(7, 8)
            .put(5, 7)
            .put(8, 9)
            .put(5, 9)
            .put(8, 7)
        benchmarkRule.measureRepeated { gomoku.next(DEPTH) }
    }

    @Test
    fun case3() {
        val gomoku = Gomoku()
            .put(7, 6)
            .put(6, 6)
            .put(7, 7)
            .put(6, 7)
            .put(7, 8)
            .put(6, 8)
            .put(7, 9)
            .put(6, 9)
        benchmarkRule.measureRepeated { gomoku.next(DEPTH) }
    }

    companion object {
        private const val DEPTH = 4
    }

}