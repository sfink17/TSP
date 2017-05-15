import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * Created by Simon on 3/6/2017.
 */


class TwoLevelTree(points: Array<Int>) {
    private val nodeList: List<ChildNode>
    private val NUMBER_OF_SEGMENTS: Int
    private val CHILD_REVERSE_CUTOFF: Int

    init {
        //val segmentSize = Math.min(Math.sqrt(points.size.toDouble()).toInt(), 200)
        val segmentSize = Math.min(Math.sqrt(points.size.toDouble()).toInt(), 200)
        this.CHILD_REVERSE_CUTOFF = 3*segmentSize/4
        val mutableNodes: Array<ChildNode?> = Array(points.size, {null})
        
        val firstNode = ChildNode(points[0], 0)
        var lastNode =  firstNode
        val firstPar = ParentNode(segmentSize, 0)
        var lastPar = firstPar
        firstNode.parent = firstPar
        firstPar.leftChild = firstNode
        mutableNodes[points[0]] = firstNode
        
        for (i in 1..points.lastIndex){
            val node = ChildNode(points[i], i % segmentSize)
            if (i % segmentSize == 0) {
                lastPar.rightChild = lastNode
                val temp = ParentNode(segmentSize, lastPar.index + 1)
                temp.prev = lastPar
                temp.leftChild = node
                lastPar.next = temp
                lastPar = temp
            }
            node.parent = lastPar
            mutableNodes[points[i]] = node
            node.prev = lastNode
            lastNode.next = node
            lastNode = node
        }
        lastPar.rightChild = lastNode
        lastNode.next = firstNode
        firstNode.prev = lastNode
        lastPar.next = firstPar
        firstPar.prev = lastPar

        this.NUMBER_OF_SEGMENTS = lastPar.index + 1
        nodeList = mutableNodes.filterNotNull()
    }


    fun next(p: Int): Int = nodeList[p].next.city
    fun prev(p: Int): Int = nodeList[p].prev.city
    fun between(a: Int, b: Int, c: Int) = _between(nodeList[a], nodeList[b], nodeList[c])
    fun flip(a: Int, b: Int, c: Int, d: Int) {
        if (nodeList[a].next == nodeList[b])
            _flip(nodeList[a], nodeList[b], nodeList[c], nodeList[d])
        else
            _flip(nodeList[c], nodeList[d], nodeList[a], nodeList[b])
    }

    

    private fun _between(a: ChildNode, b: ChildNode, c: ChildNode): Int {
        if (b == a) return 1
        if (c == a) return -1
        if (b == c) return 0
        val i2 = relativeParentOrder(a, b)*N + childOrder(b)
        val i3 = relativeParentOrder(a, c)*N + childOrder(c)
        if (i2 < i3) return -1
        else return 1
    }

    private fun _flip(a: ChildNode, b: ChildNode, c: ChildNode, d: ChildNode) {
        if (b == d || a == c) return
        if (b.parent == d.parent && childOrder(d) > childOrder(b)) reverseChildren(b, d)
        else if (a.parent == c.parent && childOrder(a) > childOrder(c)) reverseChildren(c, a)
        else splitMergeAndReverse(a, b, c, d)
    }

    private fun reverseChildren(b: ChildNode, d: ChildNode){
        if (b == b.parent.leftChild && d == b.parent.rightChild) reverseParents(b.parent, b.parent)
        else if (childOrder(d) - childOrder(b) < CHILD_REVERSE_CUTOFF) {
            val a = b.prev
            val c = d.next
            var t1: ChildNode; val t2: ChildNode
            if (b.index < d.index) { t1 = b; t2 = d }
            else {t1 = d; t2 = b }

            if (t1 == t1.parent._leftChild) t1.parent._leftChild = t2
            else if (t2 == t2.parent._rightChild) t2.parent._rightChild = t1
            val i = t2.index - t1.index
            for (j in -i..i step 2) {
                t1.index -= j
                t1.reverse()
                t1 = t1._prev
            }
            reconnectChildren(a, b, c, d)
        }
        else {
            if (b != b.parent.leftChild) mergePrevFrom(b.prev)
            if (d != d.parent.rightChild) mergeNextFrom(d.next)
            reverseParents(b.parent, d.parent)
        }
    }

    private fun reverseParents(b: ParentNode, d: ParentNode){
        if (b == d) {
            b.reverse = !b.reverse
            b.prev.rightChild.next = b.leftChild
            b.next.leftChild.prev = b.rightChild
            b.rightChild.next = b.next.leftChild
            b.leftChild.prev = b.prev.rightChild
        }
        else {
            val a = b.prev
            val c = d.next
            var t = b
            while (t.prev != d) {
                t = t.next
                t.prev.reverse()
            }
            reconnect(a, b, c, d)
        }
    }

    private fun <T> reconnectChildren(a: Node<T>, b: Node<T>, c: Node<T>, d: Node<T>){
        a.next = d as T; d.prev = a as T
        b.next = c as T; c.prev = b as T
    }

    private fun reconnect(a: ParentNode, b: ParentNode, c: ParentNode, d: ParentNode){
        a.next = d; a.rightChild.next = d.leftChild
        d.prev = a; d.leftChild.prev = a.rightChild
        b.next = c; b.rightChild.next = c.leftChild
        c.prev = b; c.leftChild.prev = b.rightChild
        var t = d
        while (t.prev != b) {
            t.index = t.prev.index + 1
            if (t.index == NUMBER_OF_SEGMENTS) t.index = 0
            t = t.next
        }
    }

    private fun splitMergeAndReverse(a: ChildNode, b: ChildNode, c: ChildNode, d: ChildNode){
        if (a.parent == b.parent){
            if (childOrder(a) < a.parent.size / 2) {
                mergePrevFrom(a)
                if (a.parent == c.parent) { reverseChildren(c, a); return }
            }
            else {
                mergeNextFrom(b)
                if (b.parent == d.parent) { reverseChildren(b, d); return }
            }
        }
        if (d.parent == c.parent){
            if (childOrder(d) < d.parent.size / 2) {
                mergePrevFrom(d)
                if (b.parent == d.parent) { reverseParents(b.parent, d.parent); return }
            }
            else {
                mergeNextFrom(c)
                if (a.parent == c.parent) { reverseParents(c.parent, a.parent); return }
            }
        }
        if (relativeParentOrder(b, d) < relativeParentOrder(c, a)) reverseParents(b.parent, d.parent)
        else reverseParents(c.parent, a.parent)
    }

    private fun mergePrevFrom(a: ChildNode){
        val p0 = a.parent.prev; val p1 = a.parent
        var n = p1.leftChild
        p1.size -= childOrder(a) + 1
        p0.size += childOrder(a) + 1
        p0.rightChild = a
        p1.leftChild = a.next
        while (n.prev != a) {
            joinWithPrevParent(n)
            n = n.next
        }
    }

    private fun mergeNextFrom(a: ChildNode){
        val p0 = a.parent.next; val p1 = a.parent
        var n = p1.rightChild
        p0.size += p1.size - childOrder(a)
        p1.size = childOrder(a)
        p0.leftChild = a
        p1.rightChild = a.prev
        while (n.next != a) {
            joinWithNextParent(n)
            n = n.prev
        }
    }

    private fun joinWithNextParent(n: ChildNode){
        if (n.parent.reverse != n.parent.next.reverse) n.reverse()
        n.parent = n.parent.next
        if (n.parent.reverse) n.index = n.next.index + 1
        else n.index = n.next.index - 1
    }

    private fun joinWithPrevParent(n: ChildNode){
        if (n.parent.reverse != n.parent.prev.reverse) n.reverse()
        n.parent = n.parent.prev
        if (n.parent.reverse) n.index = n.prev.index - 1
        else n.index = n.prev.index + 1
    }


    private fun relativeParentOrder(a: ChildNode, b: ChildNode): Int {
        val order = b.parent.index - a.parent.index
        if (order < 0 || (order == 0 && childOrder(b) < childOrder(a)) ) return order + NUMBER_OF_SEGMENTS else return order
    }

    private fun childOrder(n: ChildNode) = Math.abs(n.index - n.parent.leftChild.index)

    /**
     * Child node in two level tree.
     * @property index Relative index within segment.
     * @property next Directional "next" pointer.
     * @property prev Directional "previous" pointer.
     * @property parent Parent pointer.
     * @property city Encapsulated city.
     */

    private interface Node<T> {
        var index: Int
        var next: T
        var prev: T
        fun reverse()
    }

    private class ChildNode(val city: Int, override var index: Int) : Node<ChildNode> {
        lateinit var _next: ChildNode
        lateinit var _prev: ChildNode
        override var next: ChildNode
            get() = if (!parent.reverse) _next else _prev
            set(value) = if (!parent.reverse) _next = value else _prev = value
        override var prev: ChildNode
            get() = if (!parent.reverse) _prev else _next
            set(value) = if (!parent.reverse) _prev = value else _next = value
        lateinit var parent: ParentNode

        override fun reverse() {
            val temp = _next
            _next = _prev
            _prev = temp
        }
    }

    /**
     * Parent node in two level tree.
     * @property index Relative index within tour.
     * @property next Directional "next" pointer.
     * @property prev Directional "previous" pointer.
     * @property reverse The point of the whole thing. Flipping this reverses the child segment with constant complexity.
     */

    private class ParentNode(var size: Int, override var index: Int, var reverse: Boolean = false ) : Node<ParentNode> {

        override lateinit var next: ParentNode
        override lateinit var prev: ParentNode

        lateinit var _leftChild: ChildNode
        lateinit var _rightChild: ChildNode

        var leftChild: ChildNode
            get() = if (!reverse) _leftChild else _rightChild
            set(value) = if (!reverse) _leftChild = value else _rightChild = value
        var rightChild: ChildNode
            get() = if (!reverse) _rightChild else _leftChild
            set(value) = if (!reverse) _rightChild = value else _leftChild = value

        override fun reverse() {
            reverse = !reverse
            val temp = next
            next = prev
            prev = temp
        }
    }


}
fun main(args: Array<String>){
    N = 100
    val reader: BufferedReader = BufferedReader(InputStreamReader(FileInputStream("src/instances/tsp100.txt")))
    var lines = false
    var i = 0
    val tempList: MutableList<Point> = mutableListOf()
    reader.forEachLine {
        if (it != "") {
            val stringPair = it.trim().replace(Regex("\\s+"), " ").split(Regex("""\s"""))
            if (lines == false) {
                bounds = Pair(stringPair[0].toDouble(), stringPair[1].toDouble())
                lines = true
            }
            else {
                tempList.add(Point(Pair(stringPair[0].toDouble(), stringPair[1].toDouble()), i))
                i++
            }
        }
    }
    pointList = tempList.toList()
    val tree = TwoLevelTree(constructGreedyTour().first)
    for (j in 0..1000) {

        val a = StdRandom.uniform(0, N - 1)
        val b = tree.next(a)
        var d: Int
        var c: Int
        do {
            d = StdRandom.uniform(0, N - 1)
            c = tree.next(d)
        } while (d == a || d == b || c == a)
        if (tree.between(a, b, c) == 1) {
            tree.between(a, b, c)
            println("Failure: b reported not between a and c")
        }
        if (tree.between(b, a, c) == -1){
            tree.between(b, a, c)
            println("Failure: a reported between b and c")

        }
        tree.flip(a, b, c, d)
    }
    val start = StdRandom.uniform(0, N - 1)
    var n = start
    var total = 0
    for (k in 0 until N) {
        total += n
        n = tree.next(n)
    }
    if (n != start) println("Failure: traversal did not cycle properly")
    if (total != (N*(N-1))/2) println("Failure: traversal did not visit every node")

}