import sun.misc.Queue
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
    var tourGain = 0.0

    val dontLookBits = ActiveQueue()
    var t1: Int
    var t2: Int
    while (!dontLookBits.isEmpty()) {
        t1 = dontLookBits.dequeue()

        //Calls initKOpt for each edge connecting current node. Will try K-opt moves up to K = 28.
        val first = tree.next(t1)
        val second = tree.prev(t1)
        var runningGain: Double
        for (j in 0..1) {
            if (j == 0) t2 = first
            else {
                if (second != tree.next(t1) && second != tree.prev(t1)) continue
                t2 = second
            }
            var tries = 0
            val handler = LKMoveHandler(tree)
            do {
                t2 = handler.tryKOptMove(t1, t2)
                runningGain = handler.getRunningGain()
                tries++
            } while (runningGain <= 0 && t2 != -1 && tries < 6)
            if (runningGain > 0) {
                tourGain += runningGain
                handler.getTotalMove().forEach { dontLookBits.enqueue(it) }
            }
            else if (runningGain < 0){
                handler.reverseMoves()
            }
            //if (System.currentTimeMillis() - time > 1000) {time = System.currentTimeMillis(); println("$bestGain, $tourGain")}
        }
    }
    println("local optimum found")
    return getTour(tree)
}

fun getTour(tree: TwoLevelTree): Array<Int> {
    var place = 0
    return Array(N, {place = tree.next(place); place})
}