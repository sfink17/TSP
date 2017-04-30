import java.util.*

/**
 * Created by Simon on 4/20/2017.
 */
class LKMoveHandler(val t1: Int, var t2: Int, val tree: TwoLevelTree){
    private var p: Array<Int> = arrayOf()
    private var seq: Array<Int> = arrayOf()
    private lateinit var nodeList: Array<Int>
    private lateinit var bestNodeList: Array<Int>
    private val addedInChain: MutableList<Int> = mutableListOf()
    private var bestGain: Double = Double.NEGATIVE_INFINITY
    private var runningGain: Double = dist(t1, t2)

    fun make4OptMove(): Int {
        nodeList = Array(8, { -1 })
        bestNodeList = Array(8, { -1 })
        nodeList[0] = t1; nodeList[1] = t2
        runningGain = kOptRec(runningGain, 2)
        if (bestNodeList[7] != -1 && runningGain <= 0){
            makeKOptMove(4)
        }
        addedInChain.addAll(bestNodeList)
        return bestNodeList[7]
    }

    fun getRunningGain(): Double = runningGain

    /**
     * K opt move generator. Calls until a valid move has been found that results in
     * positive gain, or until all potential moves are exhausted. This version of
     * Lin Kernighan uses a 4-opt as its basic move.
     *
     * @property g0 Running gain within the current move.
     */

    private tailrec fun kOptRec(g0: Double, k: Int): Double {
        val t1 = nodeList[0]
        val t2 = nodeList[2 * k - 3]
        var t3: Int
        var t4: Int
        var g1: Double
        var g2: Double
        var g3: Double
        for (item in t2.getPoint().nearestK.filter { tree.next(it.first) != t2 &&
                tree.prev(it.first) != t2 && !nodeList.contains(it.first)}) {
            t3 = item.first
            g1 = g0 - item.second
            if (g1 > 0 && !Added(t2, t3, k)) {
                nodeList[2 * k - 2] = t3
                for (i in 0..1) {
                    if (i == 0) t4 = tree.next(t3) else t4 = tree.prev(t3)
                    if (nodeList.contains(t4)) continue
                    if (!Deleted(t3, t4, k)) {
                        nodeList[2 * k - 1] = t4
                        g2 = g1 + dist(t3, t4)
                        g3 = g2 - dist(t4, t1)
                        if (isValidMove(k)) {
                            if (g3 > 0) {
                                makeKOptMove(k)
                                return g3
                            }
                            if (k == 4 && bestGain < g3 && Excludable(t3, t4)) {
                                bestGain = g3
                                bestNodeList = nodeList
                            }
                        } else if (k < 4) return kOptRec(g2, k + 1)
                    }
                }
            }
        }
        return bestGain
    }

    /**
     * Function to check if edge to be added has been added previously in current move.
     * Called during [kOptRec] to disqualify edges.
     */

    private fun Added(t1: Int, t2: Int, k: Int): Boolean {
        var i = 2 * k - 5
        while (i > 0) {
            if (t1 == nodeList[i] && t2 == nodeList[i + 1] || t1 == nodeList[i + 1] && t2 == nodeList[i])
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
            if (t1 == nodeList[i] && t2 == nodeList[i - 1] || t1 == nodeList[i - 1] && t2 == nodeList[i])
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

    private fun isValidMove(k: Int): Boolean {
        var ctr = 0
        if (nodeList.copyOfRange(0, k * 2).distinct().size != k*2) return false
        p = Array(k * 2, {
            if (it % 2 == 0) {
                if (tree.next(nodeList[it]) == nodeList[it + 1]) ctr = it else ctr = it + 1
                ctr
            } else ctr xor 1
        })
        seq = Array(k * 2, {0})

        p.sortWith(Comparator { t1, t2 -> tree.between(nodeList[p[0]], nodeList[t1], nodeList[t2]) }, fromIndex = 2)
        val incl: Array<Int> =  Array(k * 2, { if (it % 2 != 0) it + 1 else it - 1 })
        incl[0] = 2*k - 1
        incl[2*k-1] = 0
        val q: Array<Int> = Array(k * 2, { p.indexOf(it) })

        //Traversal operation.
        var place = k * 2
        var count = 0

        while (place != 0) {
            seq[k * 2 - 1 - count * 2] = p[place - 1]
            seq[k * 2 - 2 - count * 2] = incl[p[place - 1]]
            place = (q[incl[p[place - 1]]] + 1) xor 1
            count++
        }
        return count == k
    }

    /**
     * Makes K opt move.
     */

    private fun makeKOptMove(k: Int) {

        val subpathPolarity: MutableList<Int> = mutableListOf()
        var p1 = 0
        var p2 = 0
        val smaller: Int
        for (i in 1..seq.lastIndex step 2) {
            val polarity: Int
            if (p.after(seq[i], seq[(i + 1) % seq.size])) {polarity = 1; p1 += tree.parRange(nodeList[p[i]], nodeList[p[(i + 1) % p.size]]) }
            else {polarity = -1; p2 += tree.parRange(nodeList[p[i]], nodeList[p[(i + 1) % p.size]])}
            subpathPolarity.add(polarity)
        }

        if (p1 > p2) smaller = -1 else smaller = 1

        val nextwise: Boolean
        if (nodeList[1] == tree.next(nodeList[0])) nextwise = true else nextwise = false

        for (i in 0..subpathPolarity.lastIndex) {
            if (subpathPolarity[i] == smaller) {
                tree.splitMerge(nodeList[p[i * 2 + 1]], nodeList[p[(i * 2 + 2) % p.size]])
                tree.reversePath(nodeList[p[i * 2 + 1]], nodeList[p[(i * 2 + 2) % p.size]])
            }
        }

        for (i in 0 until k) {
            tree.connect(nodeList[seq[i*2]], nodeList[seq[i*2 + 1]], nextwise)
            tree.reIndex()
        }

    }

    private fun Array<Int>.after(a: Int, b: Int): Boolean {
        for (i in a..this.lastIndex) if (this[i] == b) return true
        return false
    }
}