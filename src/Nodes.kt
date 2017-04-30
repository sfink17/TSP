import java.util.*

/**
 * Created by Simon on 2/16/2017.
 */

/**
 * Container for basic point data. Holds coordinate and distance related properties.
 *
 * @property coords The (X, Y) coordinates of this point.
 * @property index The index of the point in the input list. Used to generate pointers in edge list construction.
 * @property nearestK Used for neighbor lists in LK heuristic and as basis for minimal shortest edge list in Greedy heuristic.
 * @property degree Currently used for nearest neighbor heuristic. NN node will get its own class later.
 */


class Point(val coords: Pair<Double, Double>, val index: Int) {
    var nearestK: MutableList<Pair<Int, Double>> = mutableListOf()
    var nearest: Pair<Point, Double> = Pair(this, 0.0)
    var next: Point? = null
}

fun Int.getPoint() = pointList[this]

/**
 * Container for node in KD tree, used in neighbor searches.
 *
 * @constructor Creates a KD node, with all child nodes accessible from the root.
 *              [divide] specifies the splitting coordinate on the [axis].
 *              [leftChild] and [rightChild] hold descendants.
 *              [isLeaf] and [self] are used in neighbor checks.
 */

class kdNode(val divide: Double, val axis: Int, val leftChild: kdNode?, val rightChild: kdNode?,
             val isLeaf: Boolean = false, val self: Point? = null, val inRect: kdRect)

class kdRect(var minY: Double = Double.NEGATIVE_INFINITY, var maxY: Double = Double.POSITIVE_INFINITY,
             var minX: Double = Double.NEGATIVE_INFINITY, var maxX: Double = Double.POSITIVE_INFINITY, point: Point? = null){

    init {
        if (point != null){
            minX = point.coords[0]; minY = point.coords[1]; maxX = point.coords[0]; maxY = point.coords[1]
        }
    }

    fun focus(coord: Double, dir: Int, axis: Int): kdRect {
        if (axis == 0){
            if (dir == 0) return kdRect(minY, maxY, minX, coord)
            else return kdRect(minY, maxY, coord, maxX)
        }
        else {
            if (dir == 0) return kdRect(minY, coord, minX, maxX)
            else return kdRect(coord, maxY, minX, maxX)
        }
    }

    fun contains(rect: kdRect): Boolean{
        if (minY <= rect.minY && maxY >= rect.maxY && minX <= rect.minX && maxX >= rect.maxX) return true
        return false
    }

    override fun toString(): String{
        return "X: $minX-$maxX, Y: $minY-$maxY"
    }
}

/**
 * Simple node with relevant properties for greedy heuristic.
 *
 * @property degree Degree in tour, used to qualify modified Kruskal's algorithm.
 * @property end Pointer to opposite end of tour segment. Used to check for close.
 */

class GreedyNode(var degree: Int = 0, var end: Int? = null)

val dontLookBits: BitSet = BitSet(N)