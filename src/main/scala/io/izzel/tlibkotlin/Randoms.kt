package io.izzel.tlibkotlin

import io.izzel.taboolib.util.lite.Numbers
import java.util.concurrent.CopyOnWriteArrayList


/**
 * @author sky
 * @since 2020-10-1 10:43
 */
class Randoms<T>() {

    private val value = CopyOnWriteArrayList<Value<T>>()

    constructor(vararg element: Pair<T, Int>) {
        element.forEach {
            add(it.first, it.second)
        }
    }

    fun random(): T? {
        val sum = value.sumBy { it.index }
        if (sum > 0) {
            var m = 0
            val n = Numbers.getRandom().nextInt(sum)
            for (obj in value) {
                if (m <= n && n < m + obj.index) {
                    return obj.element
                }
                m += obj.index
            }
        }
        return null
    }

    fun add(element: T, index: Int = 1) {
        value.add(Value(element, index))
    }

    fun remove(element: T) {
        value.removeIf { it.element == element }
    }

    fun values(): MutableList<Value<T>> {
        return value
    }

    fun size(): Int {
        return value.size
    }

    data class Value<T>(val element: T, val index: Int)
}
