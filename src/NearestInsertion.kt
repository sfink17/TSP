import java.util.*

/**
 * Created by Simon on 2/20/2017.
 */
/**
 * Driver for smallest insertion heuristic. Again, the suggested implementation was extremely
 * inefficient, so I threw the data in the KD tree again. This heuristic uses the root KD node
 * to find the [K_NEAREST] neighbors of each subsequent node that exist in the tour already,
 * and inserts the node into the tour where it will result in the least increase of weight.
 *
 * Brute forcing this took upwards of 45 seconds for the usa13509 instance, so I had to cut that
 * time down somehow. I tested differences in [K_NEAREST] between 5 and 1000 (which gave me the
 * same result as the brute force), and it seems that after 15, the gains are pretty negligible.
 *
 * After adding the KD tree to the implementation, tour construction time dropped from ~47s to ~1.1s
 * and total weight rose from ~45,075 to ~45,240. Further of a couple tenths of a percent can be achieved
 * by increasing [K_NEAREST], at the expense of a couple seconds.
 */
tailrec fun Tour.insertSmallest(point: Point){
    if (size < 3) {
        addPoint(point)
        if (size == 2) {distance += dist(this[0], point ); this[0].next = point}
        if (size == 3) {distance += dist(this[0], point) + dist(this[1], point ); this[1].next = point; point.next = this[0]}
        insertSmallest(pointList[point.index + 1])
    }

    else {
        var bestGain: Pair<Point, Double> = Pair(this[0], Double.NEGATIVE_INFINITY)
        neighborOf(masterKD, point, tour = this)
        var gain: Double
        for (item in point.nearestK) {
            val p1 = pointList[item.first]
            gain = dist(p1, p1.next!! ) - (dist(p1, point ) + dist(point, p1.next!! ) )
            if (gain > bestGain.second) bestGain = Pair(p1, gain)
        }

        addPoint(point)
        point.next = bestGain.first.next
        bestGain.first.next = point

        distance -= bestGain.second
        if (size != N) insertSmallest(pointList[point.index + 1])
    }
}