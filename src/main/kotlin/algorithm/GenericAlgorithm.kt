package algorithm

import javafx.geometry.Point2D
import java.util.*
import kotlin.math.floor

val populationSize = 200
val breedersRatio = 0.2f;
val breedersCount = floor(populationSize * breedersRatio).toInt()
val mutationRate = 0.1f;

fun findSolution(size: Int, map: Array<IntArray>, start: Point2D, end: Point2D, draw: () -> Unit): Pair<Point2D, Int> {
    var finalStart = start

    var generation = 0

    val population: Array<LinkedList<Point2D>> = Array(populationSize) { LinkedList<Point2D>() }

    while (true) {

    }


    return Pair(finalStart, generation)
}