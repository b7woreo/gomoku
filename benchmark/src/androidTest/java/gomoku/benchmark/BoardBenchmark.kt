package gomoku.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chrnie.gomoku.Board
import com.chrnie.gomoku.Stone
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoardBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun putStone() {
        val board = Board(15, 15)
        benchmarkRule.measureRepeated { board.put(0, 0, Stone.BLACK) }
    } 

    @Test
    fun getStone() {
        val board = Board(15, 15)
        benchmarkRule.measureRepeated { board.get(0, 0) }
    }
}