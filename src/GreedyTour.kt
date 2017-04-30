import com.sun.org.apache.xpath.internal.axes.SubContextList
import java.util.*

/**
 * Created by Simon on 2/2/2017.
 */
var first = true
/**
 * Driving function for [constructGreedyTour]. Constructs KD tree given [pointList] and sorts it for use in
 * construction. Finding all tour edges is expensive, both in memory and time, and usually results in little gain.
 * Because of this, only the [K_NEAREST] edges are found for each point. 5 is usually sufficient, but if generated
 * edges are used as candidate lists for [LinKernighan], 15-20 may be desired.
 */
fun getShortestEdges(points: List<Point> = pointList): List<Triple<Int, Int?, Double>>{

    val root = kdTree(points)
    val edges: MutableSet<Triple<Int, Int?, Double>> = mutableSetOf()
    if (first) { println("Time to make tree = ${getDeltaT(ttemp)}"); ttemp = System.currentTimeMillis()}
    for (node in points) {
        neighborOf(root, node, makeList = first)
        if (first) {
            for (item in node.nearestK) {
                var curr = Triple(node.index, item.first, item.second)
                if (curr.first > curr.second) curr = curr.copy(first = curr.second, second = curr.first)
                edges.add(curr)
            }
        }
        else {
            var curr = Triple(node.index, node.nearest.first.index, node.nearest.second)
            if (curr.first > curr.second) curr = curr.copy(first = curr.second, second = curr.first)
            edges.add(curr)
        }
    }
    val edgeList: List<Triple<Int, Int?, Double>> = edges.toList()
    if (first) {
        println("Time to take neighbors = ${getDeltaT(ttemp)}"); ttemp = System.currentTimeMillis(); first = false
    }
    return edgeList.sortedBy { it.third }
}

/**
 * Constructor for greedy tour. Uses different tour format and data structure than NN and NI. The tour is usually worse than
 * that generated by Nearest Insertion, but takes less time and is preferred for the basis of the LK heuristic because it, in general,
 * shares more edges with the optimal tour.
 *
 * Similar to Kruskal's algorithm in that it adds the smallest valid edge at each iteration. Valid edges are those that do not connect
 * nodes that are degree 2, and that will not create a partial tour.
 *
 * @return Returns tour ordered by position.
 */

fun constructGreedyTour(): Pair<Array<Int>, Double> {
    initSatellites()
    var cost = 0.0
    var edges = getShortestEdges()
    val tour: Array<GreedyNode> = Array(N, {GreedyNode()})
    val complete: BitSet = BitSet(N)
    var degreeTotal: Int = 0
    while (degreeTotal < 2*N - 2) {
        for (i in 0..edges.lastIndex) {

            //The following is a candidate check for each edge.

            val i1 = edges[i].first
            val i2 = edges[i].second!!
            val n1 = tour[i1]
            val n2 = tour[i2]
            val d1 = n1.degree
            val d2 = n2.degree

            //If neither node is degree 2 and the end of n1's segment is not n2, proceed.
            if ((d1 != 2 && d2 != 2) && ((n1.end != i2) || n1.end == null)) {
                //The following conditionally updates segment ends.
                if (d1 == d2){
                    if (d1 == 0){ n1.end = i2; n2.end = i1}
                    else { val temp = n1.end; tour[n1.end!!].end = n2.end; tour[n2.end!!].end = temp}
                }
                else if (d1 == 1) { n2.end = n1.end; tour[n1.end!!].end = i2}
                else { n1.end = n2.end; tour[n2.end!!].end = i1}

                addToSatellites(satellite(i1), satellite(i2))
                cost += edges[i].third
                //drawEdge(edges[i].first, edges[i].second!!)

                //Indicates degree 2 nodes to hasten constriction of subsequent candidate list.
                if (++n1.degree == 2) complete[i1] = true
                if (++n2.degree == 2) complete[i2] = true
                degreeTotal += 2
            }

        }
        val tempPoints: MutableList<Point> = mutableListOf()
        for (bit in 0 until N) {
            if (!complete[bit]) tempPoints.add(pointList[bit])
        }
        //Replaces candidate list with list consisting of nodes with degree < 2. Makes KD neighbor search much faster.
        val tempEdges = getShortestEdges(tempPoints)
        if (edges.size == tempEdges.size) edges = getShortestEdges(tempPoints.subList(0, tempPoints.lastIndex))
        else edges = tempEdges
    }
    if (complete.cardinality() != N) {
        val l1 = complete.nextClearBit(0); val l2 = complete.previousClearBit(N-1)
        addToSatellites(l1 shl 1, l2 shl 1)
        cost += dist(l1, l2)
    }
    var last = 0
    val orderedTour: Array<Int> = Array(N, {last = satellites[last]!!; last shr 1})
    println("Time to make tour = ${getDeltaT(ttemp)}")
    return Pair(orderedTour, cost)
}

/**
 * Computes distance between points. Takes input list indices as arguments.
 */

fun dist(a: Int, b: Int): Double {
    val x = pointList[a].coords
    val y = pointList[b].coords
    return Math.sqrt((y[0] - x[0]) * (y[0] - x[0]) + (y[1] - x[1]) * (y[1] - x[1]))
}

/**
 * Computes distance between points. Takes [Point]s as arguments.
 */

fun dist(a: Point, b: Point): Double {
    if (a == b) return 0.0
    val x = a.coords
    val y = b.coords
    return Math.sqrt(Math.pow((y[0] - x[0]), 2.0) + Math.pow((y[1] - x[1]), 2.0))
}

/**
 * Used to hash edge cache for easy distance lookups. Not used currently due to significant addition of overhead.
 */

fun CantorPair(x: Int, y: Int): Long {
    val a: Int
    val b: Int
    if (x > y) {b = x; a = y} else {a = x; b = y}
    return ((a + b) * (a + b + 1) / 2 + a).toLong()
}