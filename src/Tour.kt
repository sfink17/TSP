import java.util.*

/**
 * Created by Simon on 2/20/2017.
 */

/**
 * Generic tour object with output functions. Inherits from [MutableList]
 *
 * @property pointList [Point] list the tour is based upon.
 * @property masterKD KD tree used in nearest neighbor.
 * @property distance Running measure of tour value.
 */

class Tour(pointList: MutableList<Point>, var masterKD: kdNode) : MutableList<Point> by pointList{
    val inList: BitSet = BitSet(N)
    var distance: Double = 0.0

    init {
        StdDraw.enableDoubleBuffering()

        StdDraw.setPenRadius(.005)
        StdDraw.setXscale(0.0, bounds[0])
        StdDraw.setYscale(-25.0, bounds[1])
    }

    /*
    var plane = kdRect(point = this[0])


    fun updatePlane(coords: Pair<Double, Double>){
        if (coords.first > plane.maxX) plane.maxX = coords.first
        else if (coords.first < plane.minX) plane.minX = coords.first

        if (coords.second > plane.maxY) plane.maxY = coords.second
        else if (coords.first < plane.minY) plane.minY = coords.second
    }
    */

    /**
     * Displays timing information and outputs visual tour representation using [StdDraw].
     */

    fun show() {
        println("Time to draw = ${getDeltaT(ttemp)}")
        println("Total distance: $distance")
        StdDraw.show()
    }

    /**
     * Draws the tour. Currently only called once, at end of tour generation. Will update for dynamic drawing later.
     */

    fun draw() {
        val tourTime = getDeltaT(ttemp)
        //println("Time to make tour = $tourTime")
        StdDraw.clear()

        ttemp = System.currentTimeMillis()
        t1 = ttemp - t1

        StdDraw.enableDoubleBuffering()
        StdDraw.setPenRadius(.005)
        StdDraw.setXscale(0.0, bounds[0])
        StdDraw.setYscale(-25.0, bounds[1])

        for (item in this) StdDraw.point(item.coords[0], item.coords[1])

        StdDraw.show()

        StdDraw.setPenRadius(.002)

        var curr = this[0]
        for (j in 0 until this.size) {
            val next = curr.next
            if (next == null) continue
            val c1 = next.coords[0]
            val c2 = next.coords[1]
            val d1 = curr.coords[0]
            val d2 = curr.coords[1]

            StdDraw.line(c1, c2, d1, d2)
            curr = next
            StdDraw.show()
            Thread.sleep(5)
        }
        StdDraw.line(this.last().coords[0], this.last().coords[1], this[0].coords[0], this[0].coords[1])
        StdDraw.text(bounds[0]/2, -15.0, "Weight = $distance, Time = $tourTime ms")
    }

     fun addPoint(point: Point) {

         this.add(point)
         inList.flip(point.index)
     }
}

