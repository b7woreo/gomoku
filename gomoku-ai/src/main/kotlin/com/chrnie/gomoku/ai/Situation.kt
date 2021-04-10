package com.chrnie.gomoku.ai

import com.chrnie.gomoku.*
import kotlin.math.max

interface Situation {
    fun heuristicValue(stone: Stone): Int
}

fun Board.evaluate(prev: Situation? = null, focus: Coordinate? = null): Situation {
    check(prev is SituationImpl?)
    return if (prev != null && focus != null) incrementSituate(prev, focus)
    else fullEvaluate()
}

private fun Board.fullEvaluate(): SituationImpl {
    return SituationImpl() +
            (0 until width)
                .map { Coordinate(it, 0) }
                .map { fullEvaluate(it, Direction.TOP) }
                .reduce { prev, SituationImpl -> prev + SituationImpl }
                .also { it } +

            (0 until height).map { Coordinate(0, it) }
                .map { fullEvaluate(it, Direction.RIGHT) }
                .reduce { prev, SituationImpl -> prev + SituationImpl }
                .also { it } +
            
            ((0 until width).map { Coordinate(it, 0) }
                .map { fullEvaluate(it, Direction.TOP_RIGHT) }
                .reduce { prev, SituationImpl -> prev + SituationImpl } +
            (1 until height).map { Coordinate(0, it) }
                .map { fullEvaluate(it, Direction.TOP_RIGHT) }
                .reduce { prev, SituationImpl -> prev + SituationImpl })
                .also { it } +

            ((0 until width).map { Coordinate(it, Gomoku.BOARD_HEIGHT - 1) }
                .map { fullEvaluate(it, Direction.BOTTOM_RIGHT) }
                .reduce { prev, SituationImpl -> prev + SituationImpl } +
                    (0 until height - 1).map { Coordinate(0, it) }
                .map { fullEvaluate(it, Direction.BOTTOM_RIGHT) }
                .reduce { prev, SituationImpl -> prev + SituationImpl })
                .also { it } 
}

private fun Board.fullEvaluate(starter: Coordinate, direction: Direction): SituationImpl {
    var situation = SituationImpl()
    var continues = 0
    var prev: Stone? = null
    var dead = true

    for (delta in 0..Int.MAX_VALUE) {
        val coordinate = starter.move(direction, delta)
        if (!Gomoku.isCoordinateInBoard(coordinate)) {
            if (prev != null) {
                situation += SituationImpl(prev, continues, true, 1)
            }
            break
        }

        val stone = this[coordinate]

        if (stone == prev) {
            continues += 1
            continue
        }

        if (prev != null) {
            if (stone == null || !dead) {
                situation += SituationImpl(prev, continues + 1, dead || stone != null, 1)
            }
        }

        dead = (prev != null) || delta == 0
        prev = stone
        continues = 0
    }

    return situation
}

private fun Board.incrementSituate(prev: SituationImpl, focus: Coordinate): SituationImpl {
    return incrementSituate(
        incrementSituate(
            incrementSituate(
                incrementSituate(
                    prev, focus, Direction.TOP
                ), focus, Direction.RIGHT
            ), focus, Direction.TOP_RIGHT
        ), focus, Direction.TOP_LEFT
    )
}

private fun Board.incrementSituate(
    prev: SituationImpl,
    focus: Coordinate,
    direction: Direction
): SituationImpl {
    val focusStone = checkNotNull(this[focus])
    val (positiveStone, positiveCount, positiveDead) = countContinues(focus, direction)
        ?: Triple(focusStone, 0, false)
    val (negativeStone, negativeCount, negativeDead) = countContinues(focus, direction.reverse())
        ?: Triple(focusStone, 0, false)

    val increment = if (positiveStone == focusStone && negativeStone == focusStone) {
        if (positiveDead && negativeDead) SituationImpl()
        else SituationImpl(
            focusStone,
            positiveCount + negativeCount + 1,
            positiveDead || negativeDead,
            1
        )
    } else {
        val positiveSituationImpl = if (positiveStone != focusStone) {
            if (positiveDead) SituationImpl()
            else SituationImpl(positiveStone, positiveCount, true, 1)
        } else {
            if (positiveDead) SituationImpl()
            else SituationImpl(positiveStone, positiveCount + 1, true, 1)
        }

        val negativeSituationImpl = if (negativeStone != focusStone) {
            if (negativeDead) SituationImpl()
            else SituationImpl(negativeStone, negativeCount, true, 1)
        } else {
            if (negativeDead) SituationImpl()
            else SituationImpl(negativeStone, negativeCount + 1, true, 1)
        }

        positiveSituationImpl + negativeSituationImpl
    }

    return prev + increment -
            SituationImpl(positiveStone, positiveCount, positiveDead, 1) -
            SituationImpl(negativeStone, negativeCount, negativeDead, 1)
}

private fun Board.countContinues(
    focus: Coordinate,
    direction: Direction
): Triple<Stone, Int, Boolean>? {
    val originCoordinate = focus.move(direction, 1)
    if(!Gomoku.isCoordinateInBoard(originCoordinate)) return null
    val originStone = this[originCoordinate] ?: return null
    
    for (delta in 2..Int.MAX_VALUE) {
        val coordinate = focus.move(direction, delta)
        if (!Gomoku.isCoordinateInBoard(coordinate)) return Triple(originStone, delta - 1, true)
        
        val stone = this[coordinate]
        if (stone == originStone) continue
        return Triple(originStone, delta - 1, stone != null)
    }

    throw IllegalStateException("Unreachable")
}

private class SituationImpl constructor(stone: Stone, continuous: Int, dead: Boolean, count: Int) :
    Situation {

    constructor() : this(Stone.BLACK, 5, false, 0)

    private val countMap = IntArray(20)

    init {
        if(continuous in 1..5){
            countMap[indexOf(stone, continuous, dead)] = count
        }
    }

    override fun heuristicValue(stone: Stone): Int {
        return (1 until 5).sumBy { continues ->
            (countMap[indexOf(stone, continues, true)] * ScoreTable[continues, true]) +
                    (countMap[indexOf(stone, continues, false)] * ScoreTable[continues, false])
        }
    }

    operator fun plus(other: SituationImpl): SituationImpl {
        val new = SituationImpl()
        for (i in countMap.indices) {
            new.countMap[i] = (this.countMap[i] + other.countMap[i])
        }
        return new
    }

    operator fun minus(other: SituationImpl): SituationImpl {
        val new = SituationImpl()
        for (i in countMap.indices) {
            new.countMap[i] = max(0, (this.countMap[i] - other.countMap[i]))
        }
        return new
    }

    private fun indexOf(stone: Stone, continuous: Int, dead: Boolean): Int {
        check(continuous in 1..5)
        return when (stone) {
            Stone.BLACK -> 0
            Stone.WHITE -> 10
        } + (continuous - 1) * 2 + (if (dead) 0 else 1)
    }

}