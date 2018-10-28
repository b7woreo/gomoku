package com.chrnie.gomoku.ai.algorithm

abstract class AlphaBetaNode<NODE : AlphaBetaNode<NODE>> {

  protected abstract fun enter()

  protected abstract fun exit()

  protected abstract fun isTerminal(): Boolean

  protected abstract fun heuristicValue(): Int

  protected abstract fun child(): Iterable<NODE>

  fun alphaBeta(depth: Int): List<NODE> {
    return child().map { Pair(it, it.alphaBeta(depth - 1, Int.MIN_VALUE, Int.MAX_VALUE, false)) }
      .let {
        it.asSequence()
          .map { (_, score) -> score }
          .max()
          .let { maxScore ->
            it.asSequence()
              .filter { (_, score) -> score == maxScore }
              .map { (node, _) -> node }
              .toList()
          }
      }
  }

  private fun alphaBeta(depth: Int, alpha: Int, beta: Int, maximizingPlayer: Boolean): Int = runInLifecycle {
    if (depth == 0 || isTerminal()) {
      return heuristicValue()
    }

    var a = alpha
    var b = beta
    return if (maximizingPlayer) {
      var value = Int.MIN_VALUE

      for (node in child()) {
        value = Math.max(value, node.alphaBeta(depth - 1, a, b, false))
        a = Math.max(a, value)
        if (b <= a) {
          break
        }
      }

      value
    } else {
      var value = Int.MAX_VALUE

      for (node in child()) {
        value = Math.min(value, node.alphaBeta(depth - 1, a, b, true))
        b = Math.min(b, value)
        if (b <= a) {
          break
        }
      }

      value
    }
  }

  private inline fun <R> runInLifecycle(block: () -> R): R = try {
    enter()
    block()
  } finally {
    exit()
  }
}