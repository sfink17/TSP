import java.io.FileWriter
import java.math.BigDecimal
import java.math.MathContext

/**
 * Created by Simon on 2/16/2017.
 */

/**
 * Used for Greedy Tour output. This could easily be consolidated with
 * [Tour.draw], but there are some slight differences in the data structures,
 * and it is very low on my priority list.
 */
fun PrintTour(tour: Array<Int>) {

    val tourTime = getDeltaT(t1)
    t1 = System.currentTimeMillis() - t1

    StdDraw.enableDoubleBuffering()
    StdDraw.setPenRadius(.005)
    StdDraw.setXscale(0.0, bounds[0])
    StdDraw.setYscale(-25.0, bounds[1])
    var totalDist: Double = 0.0

    for (item in pointList) {
        StdDraw.point(item.coords[0], item.coords[1])
    }

    StdDraw.setPenRadius(.002)

    for (j in 0 until N - 1) {
        val c1 = pointList[tour[j + 1]].coords[0]
        val c2 = pointList[tour[j + 1]].coords[1]
        val d1 = pointList[tour[j]].coords[0]
        val d2 = pointList[tour[j]].coords[1]
        totalDist += dist(tour[j], tour[j+1])



        StdDraw.line(c1, c2, d1, d2)
    }
    totalDist += dist(tour[N - 1], tour[0])
    StdDraw.line(pointList[tour[N-1]].coords[0], pointList[tour[N-1]].coords[1], pointList[tour[0]].coords[0], pointList[tour[0]].coords[1])

    StdDraw.text(bounds[0]/2, -15.0, "Weight = ${BigDecimal(totalDist, MathContext(6))} ")
            //+ "Time = ${BigDecimal(tourTime, MathContext(3))} ms")


    StdDraw.show()

    //println("Time to draw = ${getDeltaT(ttemp)}")
    //println("Construction time: $t1 ms, total distance, $totalDist")
}

fun drawEdge(p1: Int, p2: Int) {
    StdDraw.enableDoubleBuffering()
    StdDraw.setPenRadius(.005)
    StdDraw.setXscale(0.0, bounds[0])
    StdDraw.setYscale(-25.0, bounds[1])

    StdDraw.point(pointList[p1].coords[0], pointList[p1].coords[1])
    StdDraw.point(pointList[p2].coords[0], pointList[p2].coords[1])

    StdDraw.setPenRadius(.002)
    StdDraw.line(pointList[p1].coords[0], pointList[p1].coords[1], pointList[p2].coords[0], pointList[p2].coords[1])
    StdDraw.show()
    Thread.sleep(10)
}