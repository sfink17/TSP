/**
 * Created by Simon on 5/3/2017.
 */

class ActiveQueue {
    private val active: Array<Int>
    private val removed: Array<Boolean>

    private var pointer: Int
    private var head: Int
    private var size: Int


    init {
        active = Array(N, {it})
        removed = Array(N, {false})
        size = N
        head = 0
        pointer = head
    }

    fun enqueue(a: Int) {
        if (removed[a]) {
            removed[a] = false
            active[--head] = a
            size++
        }
    }

    fun dequeue(): Int {
        if (pointer == N)
            pointer = head
        head++; size--; removed[pointer] = true
        return active[pointer++]
    }

    fun isEmpty() = size == 0

}