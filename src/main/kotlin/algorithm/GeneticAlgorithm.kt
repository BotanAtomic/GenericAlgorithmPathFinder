package algorithm

import entity.Move
import entity.Path
import javafx.geometry.Point2D
import tornadofx.runAsync
import tornadofx.ui
import kotlin.math.floor
import kotlin.random.Random

val populationSize = 200
val breedersRatio = 0.2f;
val breedersCount = floor(populationSize * breedersRatio).toInt()
val mutationRate = 0.2f;

fun isBusy(point: Point2D, map: Array<IntArray>): Boolean {
    return map[point.x.toInt()][point.y.toInt()] > 0
}

fun isValidPoint(move: Move, point: Point2D, size: Int, map: Array<IntArray>): Boolean {

    if (point.x < 0 || point.y < 0) return false

    if (point.x >= size || point.y >= size) return false

    if (isBusy(point, map)) return false

    if (move.point.x != 0.0 && move.point.y != 0.0) {
        val initialPoint = point.subtract(move.point)

        if (isBusy(initialPoint.add(0.0, move.point.y), map)) return false
        if (isBusy(initialPoint.add(move.point.x, 0.0), map)) return false
    }

    return true;
}

fun fillRandomPath(path: Path, size: Int, map: Array<IntArray>, end: Point2D, start: Point2D) {
    while (true) {
        val currentMove = Move.values().random()
        val currentPoint = path.currentPoint.add(currentMove.point)

        if (currentPoint == end) {
            path.add(currentMove)
            break;
        }

        if (!isValidPoint(currentMove, currentPoint, size, map))
            break;

        path.add(currentMove)
    }
    println("Fill path with size ${path.size()}")
}

fun findSolution(
    size: Int,
    max: Int,
    map: Array<IntArray>,
    start: Point2D,
    end: Point2D,
    draw: () -> Unit
): Pair<Path?, Int> {
    var path: Path? = null

    var generation = 1

    var population: MutableList<Path> = MutableList(populationSize) { Path(start) }

    population.forEach { fillRandomPath(it, size, map, end, start) }

    while (true) {
        val scoredPopulation = HashMap<Path, Int>(populationSize)

        population.forEach {
            scoredPopulation[it] = it.distance(end)
        }

        val breeders = scoredPopulation
            .toList()
            .sortedBy { it.second }
            .subList(0, breedersCount)
            .map { it.first }

        val best = breeders.firstOrNull()

        println("Best of generation : ${best?.size()} / ${best?.distance(end)}")

        if (best != null && best.distance(end) <= 1 && best.size() < max) {
            path = best
            break;
        } else if (best != null && generation % 10 == 0) {
            println("Draw ??")

            runAsync {
                for ((i, values) in map.iterator().withIndex())
                    for ((j, v) in values.iterator().withIndex())
                        if (v < 0)
                            map[i][j] = 0

                best.points.forEach {
                    if (map[it.x.toInt()][it.y.toInt()] == 0) {
                        map[it.x.toInt()][it.y.toInt()] = -1
                    }
                }
            } ui {
                draw()
            }
        }

        val newPopulation: MutableList<Path> = ArrayList(populationSize)

        for (i in 1..populationSize) {
            val parent1 = breeders.random()
            var parent2 = breeders.random()

            while (parent1 == parent2)
                parent2 = breeders.random()

            val child = parent1.crossOver(parent2, size, map, end, max)

            if (Random.nextFloat() < mutationRate) {
                for (j in 0..5) {
                    if (child.moves.isNotEmpty()) {
                        val randomIndex = (0 until child.moves.size).random()
                        child.moves[randomIndex] = Move.values().random()
                    }
                }
            }

            newPopulation.add(child)
        }

        println("Next generation $generation")

        population = newPopulation
        generation++
    }

    return Pair(path, generation)
}