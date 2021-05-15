package com.chrnie.gomoku.ai.algorithm

import kotlin.math.log2
import kotlin.math.sqrt

abstract class UCTNode<NODE : UCTNode<NODE>>(private val parent: NODE? = null) {

    protected abstract fun children(): List<NODE>

    protected abstract fun simulate(): SimulateNode
    
    private var quality: Int = 0
    private var times: Int = 0

    private val pendingExpendNode by lazy { children().toMutableList().apply { shuffle() } }

    fun search(timeoutMillis: Long): NODE {
        val deadline = System.currentTimeMillis() + timeoutMillis
        while (deadline < System.currentTimeMillis()) {
            val expendNode = treePolicy()
            val heuristicValue = defaultPolicy(expendNode)
            backup(heuristicValue)
        }
        IntArray(10)
        return bestChild()
    }

    private fun treePolicy(): UCTNode<NODE> {
        var node = this
        while (node.children().isNotEmpty()) {
            val candidate = node.pendingExpendNode.removeFirstOrNull()
            if (candidate != null) return candidate
            node = bestChild()
        }
        return node
    }

    private fun defaultPolicy(starter: UCTNode<NODE>): Int {
        var node = starter.simulate()
        while (!node.isTerminal()) {
            node = node.next()
        }
        return node.heuristicValue()
    }

    private fun backup(heuristicValue: Int) {
        var node: UCTNode<NODE>? = this
        while (node != null) {
            node.quality += heuristicValue
            node.times += 1
            node = parent
        }
    }

    private fun bestChild(): NODE {
        return children().maxByOrNull { child ->
            (child.quality / child.times.toDouble()) / ((1 / sqrt(2.0)) * sqrt(2.0 * log2(times.toDouble()) / child.times))
        } ?: throw IllegalStateException()
    }

}

interface SimulateNode {

    fun isTerminal(): Boolean

    fun heuristicValue(): Int

    fun next(): SimulateNode

}

