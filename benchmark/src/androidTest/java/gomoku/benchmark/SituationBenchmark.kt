package gomoku.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chrnie.gomoku.Board
import com.chrnie.gomoku.Coordinate
import com.chrnie.gomoku.Stone
import com.chrnie.gomoku.ai.evaluate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SituationBenchmark {

    @get:Rule
    val benchmark = BenchmarkRule()

    @Test
    fun testFullSituation() {
        val board = Board(15, 15)
            .put(6, 6, Stone.BLACK)
            .put(5, 5, Stone.WHITE)
            .put(5, 6, Stone.BLACK)
            .put(4, 6, Stone.WHITE)
            .put(6, 4, Stone.BLACK)
            .put(6, 5, Stone.WHITE)
            .put(4, 5, Stone.BLACK)
            .put(7, 4, Stone.WHITE)
            .put(6, 7, Stone.BLACK)
            .put(7, 5, Stone.WHITE)
        benchmark.measureRepeated { board.evaluate() }
    }

    @Test
    fun testIncrementSituation() {
        val board = Board(15, 15)
            .put(6, 6, Stone.BLACK)
            .put(5, 5, Stone.WHITE)
            .put(5, 6, Stone.BLACK)
            .put(4, 6, Stone.WHITE)
            .put(6, 4, Stone.BLACK)
            .put(6, 5, Stone.WHITE)
            .put(4, 5, Stone.BLACK)
            .put(7, 4, Stone.WHITE)
            .put(6, 7, Stone.BLACK)

        val prevSituation = board.evaluate()

        benchmark.measureRepeated {
            board.put(7, 5, Stone.WHITE).evaluate(prevSituation, Coordinate(7, 5))
        }
    }
}