import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * Created by Simon on 2/16/2017.
 */

/**
 * Module driver. Currently calls GUI, but this can be replaced with the commented section if input from command line is
 * desired.
 */
fun main(args: Array<String>) {

   // SimpleGUI()
    val heuristics = listOf("nn", "ni", "greedy", "sa")
    try {
        if (args[1] in heuristics) toPoints(FileInputStream(File("src/instances/" + args[0])), args[0], args[1])
        else println("Must enter a valid heuristic")
    }
    catch (f: FileNotFoundException) {
        println("Must enter a valid filename")
    }




}