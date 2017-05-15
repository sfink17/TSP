import java.awt.Color
import java.util.*

/**
 * Created by Simon on 4/20/2017.
 */
class LKMoveHandler(val tree: TwoLevelTree){
    private lateinit var p: Array<Int>
    private lateinit var incl: Array<Int>
    private lateinit var q: Array<Int>
    private lateinit var t: Array<Int>
    private lateinit var bestNodeList: Array<Int>
    private val addedInChain: MutableList<Int>
    private var bestG2K: Double
    private var runningGain: Double
    private val moveStack: Stack<Array<Int>>

    init {
        this.moveStack = Stack()
        this.bestG2K = Double.NEGATIVE_INFINITY
        this.runningGain = 0.0
        this.addedInChain = mutableListOf()
    }

    fun tryKOptMove(t1: Int, t2: Int): Int {
        t = Array(8, { -1 })
        bestNodeList = Array(8, { -1 })
        t[0] = t1; t[1] = t2
        runningGain = kOptRec(runningGain + dist(t1, t2), 2)
        if (bestG2K != Double.NEGATIVE_INFINITY && runningGain <= 0){
            t = bestNodeList.copyOf()
            makeKOptMove(4)

            runningGain = bestG2K - dist(bestNodeList[7], bestNodeList[0])
            bestG2K = Double.NEGATIVE_INFINITY
        }
        addedInChain.addAll(bestNodeList)
        return bestNodeList[7]
    }

    fun getRunningGain() = runningGain
    fun getTotalMove() = addedInChain.filter { it != -1 }
    fun reverseMoves() {
        while (moveStack.isNotEmpty()){
            val move = moveStack.pop()
            tree.flip(move[0], move[3], move[2], move[1])
        }
        //PrintTour(getTour(tree))
    }

    /**
     * K opt move generator. Calls until a valid move has been found that results in
     * positive gain, or until all potential moves are exhausted. This version of
     * Lin Kernighan uses a 4-opt as its basic move.
     *
     * @property g0 Running gain within the current move.
     */

    private fun kOptRec(g0: Double, k: Int): Double {
        val t1 = t[0]
        val t2 = t[2 * k - 3]
        var t3: Int
        var t4: Int
        var g1: Double
        var g2: Double
        var g3: Double
        for (item in t2.getPoint().nearestK.filter { tree.next(it.first) != t2 &&
                tree.prev(it.first) != t2 && g0 - it.second > 0 && !Added(t2, it.first, k)}.
                sortedBy { Math.max(dist(it.first, tree.next(it.first)), dist(it.first, tree.prev(it.first))) - it.second }) {
            t3 = item.first
            g1 = g0 - item.second
            t[2 * k - 2] = t3
            val t40: Int
            val t41: Int
            if (dist(t3, tree.next(t3)) > dist(t3, tree.prev(t3))){
                t40 = tree.next(t3)
                t41 = tree.prev(t3)
            }
            else{
                t40 = tree.prev(t3)
                t41 = tree.next(t3)
            }
            for (i in 0..1) {
                if (i == 0) t4 = t40 else t4 = t41
                if (!Deleted(t3, t4, k)) {
                    t[2 * k - 1] = t4
                    g2 = g1 + dist(t3, t4)
                    g3 = g2 - dist(t4, t1)
                    if (k < 4) {
                        if (g3 > 0 && isValidMove(k)) {
                            makeKOptMove(k)
                            return g3
                        }
                        val gain = kOptRec(g2, k + 1)
                        if (gain > 0) return gain

                    }
                    else if (isValidMove(k)) {
                        if (g3 > 0){
                            makeKOptMove(k)
                            return g3
                        }
                        else if (bestG2K < g2 && Excludable(t3, t4)){
                            bestG2K = g2
                            bestNodeList = t.copyOf()
                        }
                    }
                }
            }

        }
        return 0.0
    }

    /**
     * Function to check if edge to be added has been added previously in current move.
     * Called during [kOptRec] to disqualify edges.
     */

    private fun Added(t1: Int, t2: Int, k: Int): Boolean {
        var i = 2 * k - 5
        while (i > 0) {
            if (t1 == t[i] && t2 == t[i + 1] || t1 == t[i + 1] && t2 == t[i])
                return true
            i -= 2
        }
        return false
    }

    /**
     * Function to check if edge to be removed has been removed previously in current move.
     * Called during [kOptRec] to disqualify edges.
     */

    private fun Deleted(t1: Int, t2: Int, k: Int): Boolean {
        var i = 2 * k - 3
        while (i > 0) {
            if (t1 == t[i] && t2 == t[i - 1] || t1 == t[i - 1] && t2 == t[i])
                return true
            i -= 2
        }
        return false
    }

    /**
     * Function to check if last edge to be removed has been added in the current sequence of moves.
     * Called during [kOptRec] to prevent repeating moves.
     */

    private fun Excludable(t1: Int, t2: Int): Boolean {
        for (i in 1..addedInChain.lastIndex step 2)
            if ((t1 == addedInChain[i] && t2 == addedInChain[(i + 1) % addedInChain.size]) ||
                    t2 == addedInChain[i] && t1 == addedInChain[(i + 1) % addedInChain.size])
                return false
        return true
    }

    private fun findPermutation(k: Int) {
        p = Array(k * 2, {0})
        for (i in 0 until k) {
            if (t[2*i + 1] == tree.next(t[2*i])) p[i] = 2*i else p[i] = 2*i + 1
        }
        p.sortWith(Comparator { t1, t2 -> tree.between(t[p[0]], t[t1], t[t2]) }, fromIndex = 1, toIndex = k)
        for (i in k*2 - 1 downTo 1 step 2){
            p[i-1] = p[i/2]
            if (p[i-1] and 1 == 0) p[i] = p[i-1] + 1 else p[i] = p[i-1] - 1
        }
        incl =  Array(k * 2, { if (it % 2 != 0) it + 1 else it - 1 })
        incl[0] = 2*k - 1; incl[2*k-1] = 0
        q = Array(k * 2, { p.indexOf(it) })
    }

    private fun isValidMove(k: Int): Boolean {
        //Traversal operation.
        var place = k * 2 - 1
        var count = 0
        findPermutation(k)
        do {
            place = q[incl[p[place]]]
            if (place % 2 == 0) place-- else place++
            count++
        } while (place != -1)
        return count == k
    }

    /**
     * Makes K opt move.
     */
    fun makeKOptMove(k: Int) {
        var best_i: Int = 0; var best_j: Int = 0; var bestScore: Int; var s: Int
        findPermutation(k)
        FindNextReversal@ while(true) {
            bestScore = -1
            for (i in 0..k * 2 - 3) {
                val j = q[incl[p[i]]]
                if ((i and 1) == 0) s = score(i + 1, j, k) else s = score(i, j - 1, k)
                if (j >= i + 2 && (i and 1) == (j and 1) && s > bestScore) {
                    bestScore = s; best_i = i; best_j = j
                }
            }
            if (bestScore >= 0) {
                val i = best_i
                val j = best_j
                if ((i and 1) == 0) {
                    //print2Opt(t[p[i]], t[p[i + 1]], t[p[j]], t[p[j - 1]])
                    flip(t[p[i]], t[p[i + 1]], t[p[j + 1]], t[p[j]])
                    reverse(i + 1, j)
                } else {
                    //print2Opt(t[p[i]], t[p[i + 1]], t[p[j]], t[p[j - 1]])
                    flip(t[p[i - 1]], t[p[i]], t[p[j]], t[p[j - 1]])
                    reverse(i, j - 1)
                }
                continue
            }
            for (i in 0..2*k-3) {
                val j = q[incl[p[i]]]
                if (j >= i + 2) {
                    //print2Opt(t[p[i]], t[p[i + 1]], t[p[j]], t[p[j - 1]])
                    flip(t[p[i]], t[p[i + 1]], t[p[j]], t[p[j - 1]])
                    reverse(i + 1, j - 1)
                    continue@FindNextReversal
                }
            }
            break
        }
        //PrintTour(getTour(tree))
    }

    private fun flip(a: Int, b: Int, c: Int, d: Int){
        tree.flip(a, b, c, d)
        val m: Array<Int> = Array(4, {0})
        m[0] = a; m[1] = b; m[2] = c; m[3] = d
        moveStack.push(m)
    }

    private fun reverse(a: Int, b: Int) {
        var i = a; var j = b
        while (i < j) {
            val pi = p[i]
            p[i] = p[j]
            q[p[i]] = i
            p[j] = pi
            q[p[j]] = j
            i++; j--
        }
    }

    private fun score(left: Int, right: Int, k: Int): Int {
        var count = 0; var place: Int
        reverse(left, right)
        for (i in 0..2*k-3) {
            place = q[incl[p[i]]]
            if (place >= i + 2 && (i and 1) == (place and 1))
                count++
        }
        reverse(left, right)
        return count
    }
}

fun print2Opt(a: Int, b: Int, c: Int, d: Int) {
    val p1 = a.getPoint()
    val p2 = b.getPoint()
    val p3 = c.getPoint()
    val p4 = d.getPoint()
    StdDraw.setPenRadius(StdDraw.getPenRadius()*8)
    StdDraw.point(p1.coords[0], p1.coords[1])
    StdDraw.point(p2.coords[0], p2.coords[1])
    StdDraw.point(p3.coords[0], p3.coords[1])
    StdDraw.point(p4.coords[0], p4.coords[1])
    StdDraw.setPenRadius(StdDraw.getPenRadius()*.25)
    StdDraw.setPenColor(Color.RED)
    StdDraw.line(p1.coords[0], p1.coords[1], p2.coords[0], p2.coords[1])
    StdDraw.line(p3.coords[0], p3.coords[1], p4.coords[0], p4.coords[1])
    StdDraw.setPenColor(Color.BLUE)
    StdDraw.line(p1.coords[0], p1.coords[1], p4.coords[0], p4.coords[1])
    StdDraw.line(p2.coords[0], p2.coords[1], p3.coords[0], p3.coords[1])
    StdDraw.setPenRadius(StdDraw.getPenRadius()*.5)
    StdDraw.setPenColor(Color.BLACK)
    StdDraw.show()
    Thread.sleep(20)
}