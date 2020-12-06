package com.chrnie.gomoku.ai.algorithm

abstract class AlphaBetaNode<NODE : AlphaBetaNode<NODE>> {

    protected abstract fun isTerminal(): Boolean

    protected abstract fun heuristicValue(): Int

    protected abstract fun children(): Sequence<NODE>

    fun alphaBeta(depth: Int): List<NODE> =
        children().fold(Pair(Int.MIN_VALUE, emptySequence<NODE>())) { (maxValue, sequence), node ->
            val value = node.alphaBeta(depth - 1, maxValue, Int.MAX_VALUE, false)
            when {
                value > maxValue -> Pair(value, sequenceOf(node))
                value == maxValue -> Pair(maxValue, sequence + node)
                else -> Pair(maxValue, sequence)
            }
        }.let { (_, sequence) -> sequence.toList() }

    private fun alphaBeta(depth: Int, alpha: Int, beta: Int, maximizingPlayer: Boolean): Int {
        if (depth == 0 || isTerminal()) {
            return heuristicValue()
        }

        return if (maximizingPlayer) {
            var a = alpha
            var value = Int.MIN_VALUE

            for (node in children()) {
                value = Math.max(value, node.alphaBeta(depth - 1, a, beta, false))
                a = Math.max(a, value)
                if (beta <= a) {
                    break
                }
            }

            value
        } else {
            var b = beta
            var value = Int.MAX_VALUE

            for (node in children()) {
                value = Math.min(value, node.alphaBeta(depth - 1, alpha, b, true))
                b = Math.min(b, value)
                if (b <= alpha) {
                    break
                }
            }

            value
        }
    }
}