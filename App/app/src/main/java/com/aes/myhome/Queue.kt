package com.aes.myhome

import kotlinx.serialization.Serializable
import java.util.AbstractQueue

@Serializable
class Queue<T> : AbstractQueue<T>() {
    private val _elements: MutableList<T> = ArrayList()

    override val size: Int
        get() = _elements.size

    override fun iterator(): MutableIterator<T> {
        return _elements.iterator()
    }

    override fun offer(e: T): Boolean {
        _elements.add(0, e)
        return true
    }

    override fun poll(): T? {
        if (_elements.isEmpty()) {
            return null
        }
        return _elements.removeAt(0)
    }

    override fun peek(): T? {
        return _elements.getOrNull(0)
    }
}