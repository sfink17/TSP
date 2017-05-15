import java.math.BigDecimal
import java.math.MathContext

/**
 * Created by Simon on 4/21/2017.
 */

class Annealer(points: Array<Int>, var cost: Double) {
    val tree: TwoLevelTree
    val BETA = 0.99995
    val T_THRESHOLD = 0.000001
    val COST_THRESHOLD = cost * 0.85
    var T = 10.0
    var bestDist: Double

    init {
        var counter = 0
        var counterTH = 100
        this.tree = TwoLevelTree(points)
        bestDist = cost

        while (T > T_THRESHOLD && bestDist > COST_THRESHOLD){
            if (counter++ > counterTH) {
                counter = 0
                StdDraw.clear()
                StdDraw.text(bounds[0]*.2, bounds[1] * 0.95, "T = ${BigDecimal(T, MathContext(8))}")
                PrintTour(this.getTour(), false)
                if (T < .0035) counterTH = 1000
                if (T < .001) counterTH = 5000
            }
            val c1 = StdRandom.uniform(0, N)
            val c2 = tree.next(c1)
            val c3List = c2.getPoint().nearestK.filter { it.first != c1 && tree.prev(it.first) != c2}
            val c3 = c3List[StdRandom.uniform(0, c3List.size)].first
            trySwap(c1, c2, c3, tree.prev(c3))
        }
        if (T < T_THRESHOLD) println("exited from cooling")
        if (bestDist < COST_THRESHOLD) println("exited after goal met, best: $bestDist")
    }

    fun getTour(): Array<Int> {
        var point = 0
        return Array(N, {point = tree.next(point); point})
    }

    private fun trySwap(c1: Int, c2: Int, c3: Int, c4: Int){

        var gain = 0.0
        gain += dist(c1, c2) + dist(c3, c4)
        gain -= dist(c2, c3) + dist(c4, c1)

        if (gain > 0 ) {
            make2Opt(c1, c2, c3, c4, gain)
            if (cost < bestDist) { bestDist = cost }
        }
        else if (StdRandom.uniform(0.0, 1.0) < probability(-gain / cost)) {
            make2Opt(c1, c2, c3, c4, gain)
        }
    }

    private fun probability(delta: Double) = Math.pow(Math.E, - delta / T)

    private fun make2Opt(a: Int, b: Int, c: Int, d: Int, gain: Double){
        tree.flip(a, b, c, d)
        cost -= gain
        T *= BETA
    }
}