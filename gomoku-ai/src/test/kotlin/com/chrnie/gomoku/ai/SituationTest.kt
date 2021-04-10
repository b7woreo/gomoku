package com.chrnie.gomoku.ai

import com.chrnie.gomoku.Coordinate
import com.chrnie.gomoku.Stone
import com.chrnie.gomoku.toBoard
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(TestParameterInjector::class)
class SituationTest {

    @Test
    fun `full evaluate`(@TestParameter(valuesProvider = FullEvaluateValueProvider::class) fullEvaluateValue: FullEvaluateValue) {
        fullEvaluateValue.boardString
            .toBoard(15, 15)
            .evaluate()
            .run {
                assertEquals(fullEvaluateValue.blackValue, this.heuristicValue(Stone.BLACK))
                assertEquals(fullEvaluateValue.whiteValue, this.heuristicValue(Stone.WHITE))
            }
    }
    

    @Test
    fun `increment evaluate`(@TestParameter(valuesProvider = IncrementEvaluateValueProvider::class) incrementEvaluateValue: IncrementEvaluateValue) {
        val preSituation = incrementEvaluateValue.preBoardString.toBoard(15, 15).evaluate()
        incrementEvaluateValue
            .boardString
            .toBoard(15, 15)
            .evaluate(preSituation, incrementEvaluateValue.focus)
            .run {
                assertEquals(incrementEvaluateValue.blackValue, this.heuristicValue(Stone.BLACK))
                assertEquals(incrementEvaluateValue.whiteValue, this.heuristicValue(Stone.WHITE))
            }
    }
    
    class FullEvaluateValueProvider : TestParameter.TestParameterValuesProvider {

        override fun provideValues(): List<FullEvaluateValue> {
            return listOf(
                FullEvaluateValue(
                    ".",
                    0,0
                ),
                FullEvaluateValue(
                    """
                        x
                     """.trimIndent(),
                    40,0
                ),
                FullEvaluateValue(
                   """
                        o .
                        . x
                   """.trimIndent() ,
                    40, 40
                ),
                FullEvaluateValue(
                    """
                       o .
                       x x
                    """.trimIndent(),
                    160, 40
                ),
                FullEvaluateValue(
                    """
                       . . .
                       . o .
                       o x x
                    """.trimIndent(),
                    70, 160
                ),
                FullEvaluateValue(
                    """
                       . . x
                       . o .
                       o x x
                    """.trimIndent(),
                    110, 70
                ),
                FullEvaluateValue(
                    """
                       . . x
                       . o o
                       o x x
                    """.trimIndent(),
                    110,180
                ),
                FullEvaluateValue(
                    """
                       . . x 
                       x o o 
                       o x x 
                    """.trimIndent(),
                    230, 90
                ),
                FullEvaluateValue(
                    """
                       . . x o
                       x o o .
                       o x x .
                    """.trimIndent(),
                    230, 120
                ),
                FullEvaluateValue(
                    """
                       . . x o
                       x o o .
                       o x x .
                       . . x .
                    """.trimIndent(),
                    1150, 120
                ),
                FullEvaluateValue(
                    """
                       . . x o
                       x o o o
                       o x x .
                       . . x .
                    """.trimIndent(),
                    1150, 320
                ),
            )
        }
    }

    data class FullEvaluateValue(
        val boardString: String,
        val blackValue: Int,
        val whiteValue: Int,
    )
    
    class IncrementEvaluateValueProvider: TestParameter.TestParameterValuesProvider{
        override fun provideValues(): List<IncrementEvaluateValue> {
            return listOf(
                IncrementEvaluateValue(
                    """
                        .
                    """.trimIndent(),
                    """
                        x
                    """.trimIndent(),
                    Coordinate(7, 7),
                    40, 0
                ),
                
                IncrementEvaluateValue(
                    """
                       . .
                       . x
                    """.trimIndent(),
                    """
                       o .
                       . x
                    """.trimIndent(),
                    Coordinate(6, 6),
                    40, 40
                ),
                IncrementEvaluateValue(
                    """
                        o .
                        . x
                     """.trimIndent(),
                    """
                       o .
                       x x
                    """.trimIndent(),
                    Coordinate(6, 7),
                    160, 40
                ),
                IncrementEvaluateValue(
                    """
                       . . . 
                       . o . 
                       . x x 
                    """.trimIndent(),
                    """
                       . . .
                       . o .
                       o x x
                    """.trimIndent(),
                    Coordinate(6, 8),
                    70, 160
                ),
                IncrementEvaluateValue(
                    """
                       . . .
                       . o .
                       o x x
                    """.trimIndent(),
                    """
                       . . x
                       . o .
                       o x x
                    """.trimIndent(),
                    Coordinate(8, 6),
                    110, 70
                ),
                IncrementEvaluateValue(
                    """
                       . . x
                       . o .
                       o x x
                    """.trimIndent(),
                    """
                       . . x
                       . o o
                       o x x
                    """.trimIndent(),
                    Coordinate(8, 7),
                    110, 180
                ),
                IncrementEvaluateValue(
                    """
                       . . x .
                       . o o .
                       o x x .
                    """.trimIndent(),
                    """
                       . . x .
                       x o o .
                       o x x .
                    """.trimIndent(),
                    Coordinate(5, 6),
                    230, 90
                ),
                IncrementEvaluateValue(
                    """
                       . . x .
                       x o o .
                       o x x .
                    """.trimIndent(),
                    """
                       . . x o
                       x o o .
                       o x x .
                    """.trimIndent(),
                    Coordinate(8, 5),
                    230, 120
                ),
                IncrementEvaluateValue(
                    """
                       . . x o
                       x o o .
                       o x x .
                       . . . .
                    """.trimIndent(),
                    """
                       . . x o
                       x o o .
                       o x x .
                       . . x .
                    """.trimIndent(),
                    Coordinate(7, 8),
                    1150, 120
                ),
                IncrementEvaluateValue(
                    """
                       . . x o
                       x o o .
                       o x x .
                       . . x .
                    """.trimIndent(),
                    """
                       . . x o
                       x o o o
                       o x x .
                       . . x .
                    """.trimIndent(),
                    Coordinate(8, 6),
                    1150, 320
                ),
            )
        }
    }
    
    data class IncrementEvaluateValue(
        val preBoardString: String,
        val boardString: String,
        val focus: Coordinate,
        val blackValue: Int,
        val whiteValue: Int,
    )
}