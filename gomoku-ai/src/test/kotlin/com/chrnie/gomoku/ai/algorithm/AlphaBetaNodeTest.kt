package com.chrnie.gomoku.ai.algorithm

import junit.framework.AssertionFailedError
import junit.framework.TestCase.assertEquals
import org.junit.Test

class AlphaBetaNodeTest {

    @Test
    fun `test search`() {

        val expect = Node(
            children = sequenceOf(
                Node(1),
                Node(children = sequenceOf(Node(2), Node()))
            )
        )
        val result = Node(
            children = sequenceOf(
                Node(children = sequenceOf(Node(0))),
                Node(children = sequenceOf(Node(-1), Node())),
                expect
            )
        ).search(5)

        assertEquals(1, result.size)
        assertEquals(expect, result[0])
    }

    private class Node(val value: Int? = null, val children: Sequence<Node>? = null) :
        AlphaBetaNode<Node>() {

        override fun isTerminal(): Boolean = children == null

        override fun heuristicValue(): Int = value ?: throw AssertionFailedError("No value")

        override fun children(): Sequence<Node> = children ?: throw AssertionFailedError("No children")

    }


}