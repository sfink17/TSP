import com.sun.org.apache.xpath.internal.operations.Bool
import java.util.*

/**
 * Created by Simon on 2/2/2017.
 */

var satellites: Array<Int?> = arrayOf()


fun initSatellites() {
    if (satellites.size == 0) satellites = Array(N shl 1, {null})
}

fun addToSatellites(s1: Int, s2: Int){
    val d1: Int
    val d2: Int
    when {
        satellites[s1] == null && satellites[s2 + 1] == null -> {d1 = s1; d2 = s2}
        satellites[s1+1] == null && satellites[s2] == null -> {d1 = s2; d2 = s1}
        satellites[s1] == null && satellites[s2] == null -> {d1 = s1; d2 = s2 + 1}
        else -> {d1 = s1+1; d2 = s2}
    }
    satellites[d1] = d2
    satellites[d2 xor 1] = d1 xor 1
}


fun satellite(city: Int): Int = city shl 1