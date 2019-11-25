package entity

import algorithm.isValidPoint
import javafx.geometry.Point2D
import java.lang.Integer.max
import java.util.*

class Path(private val start: Point2D) {

    val moves: LinkedList<Move> = LinkedList()
    val points: LinkedList<Point2D> = LinkedList()

    var currentPoint = start

    fun add(move: Move) {
        currentPoint = currentPoint.add(move.point)
        moves.add(move)
        points.add(currentPoint)
    }

    fun distance(end: Point2D): Int {
        if (points.isEmpty()) return start.distance(end).toInt()
        return points.last.distance(end).toInt();
    }

    fun size(): Int {
        return moves.size
    }

    fun rebuildPath() {
        currentPoint = start
        points.clear()

        moves.forEach {
            currentPoint = currentPoint.add(it.point)
            points.add(currentPoint)
        }
    }

    fun crossOver(
        second: Path,
        mapSize: Int,
        map: Array<IntArray>,
        end: Point2D,
        max: Int
    ): Path {
        val path = Path(start)

        for (i in 0 until arrayOf(max(size(), second.size()), max).random()) {
            val moveList = when {
                i % 2 == 0 && i < moves.size -> moves
                i < second.moves.size -> second.moves
                i < moves.size -> moves
                else -> null
            }

            val currentMove = when (moveList) {
                null -> Move.values().random()
                else -> moveList[i]
            }

            val currentPoint = path.currentPoint.add(currentMove.point)

            if (!isValidPoint(currentMove, currentPoint, mapSize, map))
                continue;

            path.add(currentMove)

            if (currentPoint == end) break;
        }

        return path
    }

}
