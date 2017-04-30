/**
 * Created by Simon on 2/9/2017.
 */
import java.util.Random

val rand = Random(System.currentTimeMillis())
var PQueue: MutableList<kdNode> = mutableListOf()

/**
 * Used when [neighborOf] is directed to make neighbor lists. Adds [index] to [Point.nearestK] if
 * [distance] is less than distance value of the furthest point in the list.
 *
 * @param
 */
fun MutableList<Pair<Int, Double>>.addIfBetter(index: Int, distance: Double): Double{
    if (distance < this.last().second) {this.remove(this.last()); this.add(Pair(index, distance)); this.sortBy { it.second }}

    return this.last().second
}

/**
 * Tail recursive nearest neighbor function. Traverses KD tree and finds [k] nearest neighbors
 * not in [tour]. If [k] == 1, uses [Point.nearest] to prevent overwriting current neighbor lists.
 */
tailrec fun neighborOf(node: kdNode, point: Point, best: Double = bounds[1]*bounds[0],
                       k: Int = K_NEAREST, goingUp: Boolean = false,
                       makeList: Boolean = true, tour: Tour? = null){
    val dimLoc: Double
    if (node.isLeaf){
        var locBest: Double
        var newK = k

        if (point != node.self && !(tour != null && tour.inList[node.self!!.index])) {
            locBest = dist(point.index, node.self!!.index)
            if (makeList) {
                when {
                    newK > 1 -> {
                        point.nearestK.add(Pair(node.self.index, locBest))
                        locBest = best
                        newK--
                    }
                    newK == 1 -> {
                        point.nearestK.add(Pair(node.self.index, locBest))
                        newK--
                    }
                    newK <= 0 -> {
                        if (newK == 0) {
                            point.nearestK.sortBy { it.second }; newK--
                        }
                        locBest = point.nearestK.addIfBetter(node.self.index, locBest)
                    }
                }
            }
            else if (locBest > best) locBest = best else point.nearest = Pair(node.self, locBest)
        }
        else locBest = best

        if (PQueue.size > 0) {
            val newNode = PQueue.removeAt(PQueue.lastIndex)
            neighborOf(newNode, point, locBest, newK, true, makeList = makeList, tour = tour)
        }
    }
    else if (!goingUp) {
        dimLoc = point.coords[node.axis]
        PQueue.add(node)
        if (dimLoc < node.divide) neighborOf(node.leftChild!!, point, best, k, makeList = makeList, tour = tour)
        else neighborOf(node.rightChild!!, point, best, k, makeList = makeList, tour = tour)
    }
    else {
        dimLoc = point.coords[node.axis]
        when {
            (dimLoc < node.divide && (node.divide - dimLoc) < best) -> neighborOf(node.rightChild!!, point, best, k, makeList = makeList, tour = tour)
            (dimLoc >= node.divide && (dimLoc - node.divide) < best) -> neighborOf(node.leftChild!!, point, best, k, makeList = makeList, tour = tour)
            else -> {
                if (PQueue.size > 0) {
                    val newNode : kdNode = PQueue.removeAt(PQueue.lastIndex)
                    neighborOf(newNode, point, best, k, true, makeList = makeList, tour = tour)
                }
            }
        }
    }
}

/**
 * Generates a root [kdNode] at the median of the input point list.
 *
 * @return The root node.
 */

var maxDepth = 0

fun kdTree(nodelist: List<Point>, depth: Int = 0, rect: kdRect = kdRect()): kdNode{
    val axis = depth % 2
    if (depth > maxDepth) maxDepth = depth
    if (nodelist.size == 1) return kdNode(nodelist[0].coords[axis], axis, null, null, true, nodelist[0], inRect = rect)

    /*
    This is used to approximate the median of particularly large lists.
    A sorting function is called to find the median, and N*log(N) complexity for such instances
    is prohibitively slow. Instead, the median of 100 random points is used.
    */
    if (nodelist.size > 200) {
        maxDepth = 0
        val pointSet: MutableSet<Point> = mutableSetOf()
        var i = 0
        while (i < 100){
            val notDup = pointSet.add(nodelist[rand.nextInt(nodelist.size-1)])
            if (!notDup) i--
            i++
        }
        val pointList = pointSet.sortedBy { it.coords[axis] }
        val pPoint = pointList[50]
        val medPoint = pPoint.coords[axis]
        return kdNode(
                medPoint,
                axis,
                kdTree(nodelist.filter { it.coords[axis] < medPoint }, depth+1, rect.focus(medPoint, axis, 0)),
                kdTree(nodelist.filter { it.coords[axis] >= medPoint }, depth+1, rect.focus(medPoint, axis, 1)),
                inRect = rect
        )
    }
    else {
        val templist = nodelist.sortedBy { it.coords[axis] }
        val median = nodelist.size shr 1
        val medPoint = templist[median].coords[axis]
        return kdNode(
                medPoint,
                axis,
                kdTree(templist.subList(0, median), depth+1, rect.focus(medPoint, axis, 0)),
                kdTree(templist.subList(median, templist.size), depth+1, rect.focus(medPoint, axis, 1)),
                inRect = rect
            )
    }
}

/**
 * Convenience function for Pair/[/] notation. Kotlin doesn't ship with this.
 */

operator fun Pair<Double, Double>.get(index: Int): Double{
    if (index == 0) return this.first
    else if (index == 1) return this.second
    else return 0.0
}