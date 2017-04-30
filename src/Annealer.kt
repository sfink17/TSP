import java.math.BigDecimal
import java.math.MathContext

/**
 * Created by Simon on 4/21/2017.
 */

class Annealer(points: Array<Int>, var cost: Double) {
    val points: Array<Int>
    val BETA = 0.99995
    val T_THRESHOLD = 0.000001
    val COST_THRESHOLD = cost * 0.85
    var T = 10.0
    var bestTour: Array<Int>
    var bestDist: Double

    init {
        var counter = 0
        var counterTH = 100
        this.points = points.copyOf()
        this.bestTour = points.copyOf()
        bestDist = cost
        var lastBest = bestDist

        while (T > T_THRESHOLD && bestDist > COST_THRESHOLD){
            if (counter++ > counterTH) {
                counter = 0
                StdDraw.clear()
                StdDraw.text(bounds[0]*.2, bounds[1] * 0.95, "T = ${BigDecimal(T, MathContext(8))}")
                PrintTour(this.points)
                if (T < .0035) counterTH = 1000
                if (T < .001) counterTH = 10000
            }
            val c1 = StdRandom.uniform(0, N)
            var c2: Int
            do { c2 = StdRandom.uniform(0, N) }
                while (c2 == c1)
            trySwap(c1, c2)
            //if (bestDist < lastBest) {lastBest = bestDist; counter = 0}
            //else if (counter++ > 1000000) {println("counter exceeded"); break}
        }
        if (T < T_THRESHOLD) println("exited from cooling")
        if (bestDist < COST_THRESHOLD) println("exited after goal met, best: $bestDist")
    }

    fun getTour(): Array<Int> = bestTour

    private fun probability(delta: Double) = Math.pow(Math.E, - delta / T)
    private fun trySwap(c1: Int, c2: Int){
        testNeighbor(c1, c2)

        val gain = cost - totalDistance(points)
       // var gain = 0.0

        /*
        for (i in -1..1 step 2) {
            val n1 = wrap(c1 + i); val n2 = wrap(c2 + i)
            gain += dist(points[c1], points[n1])
            gain += dist(points[c2], points[n2])
            gain -= dist(points[c1], points[n2])
            gain -= dist(points[c2], points[n1])
        }
        testNeighbor(c1, c2)
        println(cost - totalDistance(points) - gain)
        testNeighbor(c1, c2)
        */

        if (gain > 0 ) {
            toNeighbor(c1, c2, gain)
            if (cost < bestDist) { bestDist = cost; bestTour = points.copyOf() }
        }
        else if (StdRandom.uniform(0.0, 1.0) < probability(-gain / cost)) {
            toNeighbor(c1, c2, gain)
        }
        else testNeighbor(c1, c2)
    }

    private fun testNeighbor(c1: Int, c2: Int){
        val temp = points[c1]; points[c1] = points[c2]; points[c2] = temp
    }

    private fun toNeighbor(c1: Int, c2: Int, gain: Double){
        //val temp = points[c1]; points[c1] = points[c2]; points[c2] = temp
        cost -= gain
        T *= BETA
    }

    private fun wrap(i: Int) = if (i == -1) N - 1 else if (i == N) 0 else i
    private fun totalDistance(points: Array<Int>): Double {
        var totalDist = 0.0
        for (i in 0..points.lastIndex - 1)
            totalDist += dist(points[i], points[i+1])
        return totalDist + dist(points[N - 1], points[0])
    }
}