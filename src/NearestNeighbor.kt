import java.util.*

/**
 * Created by Simon on 2/20/2017.
 */
/**
 * Driver for nearest neighbor heuristic. Calls recursively until tour is generated. The assignment
 * overcomplicated this by suggesting that the function read in each point iteratively from the input data, requiring
 * reassignment and distance calculations every time a node is displaced. Reading from the complete [pointList]
 * allows extremely quick NN checks across the data set, completing the tour significantly faster and better than
 * the initial implementation allowed.
 *
 * @property point The point to be inserted.
 */
tailrec fun Tour.insertNearest(point: Point) {
    //Inputs tour as argument into kd neighbor method to prevent seeing points multiple times.
    addPoint(point)
    neighborOf(masterKD, point, tour = this, makeList = false)
    point.next = point.nearest.first
    distance += point.nearest.second

    if (size != N) insertNearest(point.nearest.first)
}