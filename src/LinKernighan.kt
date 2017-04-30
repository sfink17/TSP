import com.sun.org.apache.xpath.internal.operations.Bool
import java.util.*

/**
 * Created by Simon on 2/17/2017.
 */

/**
 * Lin Kernighan chain ejection heuristic. Based partially on leading implementation by Keld Helsgaun.
 * This initial function acts as a driver for inner functions, calling [initKOpt] until no improvements can
 * be made.
 *
 * @property dontLookBits Used to speed up tour. The driver function won't try to improve on nodes that didn't yield
 *                        improvements before.
 */

fun LinKernighan(points: Array<Int>): Array<Int> {
    val tree = TwoLevelTree(points)
    var doneIfZero = N
    var place = 0
    var bestGain = 0.0
    var tourGain = 0.0

    var pointer = 0
    val dontLookBits = Array(N, {false})
    //val currentEdges: Array<Pair<Int, Int>> = Array(N, { Pair(0, 0) })
    //for (item in pointList) currentEdges[item.index] = Pair(item.node.next.index, item.node.prev.index)
    var t1 = points[0]
    var t2: Int
    var time = System.currentTimeMillis()
    while (doneIfZero > 0) {
        if (pointer == N - 1) pointer = 0 else pointer++
        if (dontLookBits[t1]) {
            doneIfZero--
            t1 = points[pointer]
            continue
        }

        //Calls initKOpt for each edge connecting current node. Will try K-opt moves up to K = 28.
        val first = tree.next(t1)
        val second = tree.prev(t1)
        var runningGain: Double = 0.0
        for (j in 0..1) {
            if (j == 0) t2 = first else t2 = second
            var tries = 0
            var handler =  LKMoveHandler(t1, t2, tree)
            do {
                t2 = handler.make4OptMove()
                runningGain = handler.getRunningGain()
                tries++
            } while (runningGain <= 0 && t2 != -1 && tries < 6)
        }
        //Setter for dontLookBit. If no improvement, sets true.
        tourGain += runningGain
        if (bestGain < tourGain) bestGain = tourGain
        if (runningGain <= 0) {
            dontLookBits[place] = true; doneIfZero--
        } else doneIfZero = N
        t1 = points[pointer]
        if (System.currentTimeMillis() - time > 1000) {time = System.currentTimeMillis(); println("$bestGain, $tourGain")}
    }

    var last = t1
    return Array(N, {last = tree.next(last); last})
}