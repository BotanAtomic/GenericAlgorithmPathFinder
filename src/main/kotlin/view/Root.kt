package view

import algorithm.findSolution
import javafx.geometry.Point2D
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import tornadofx.View
import tornadofx.clear
import kotlin.math.roundToInt

class Root : View() {

    override val root: AnchorPane by fxml("/views/root.fxml")

    private val canvas: AnchorPane by fxid()

    private val cellSizeInput: TextField by fxid("cellWidth")

    private var cellSize: Int = 0;

    private val info: Text by fxid()

    private var map: Array<IntArray> = Array(cellSize) { IntArray(cellSize) { 0 } }

    private var generation: Int = 0

    private var start: Point2D = Point2D.ZERO
    private var end: Point2D = Point2D.ZERO

    init {
        canvas.setOnMouseClicked {
            if (cellSize == 0)
                return@setOnMouseClicked;

            val step = canvas.width / cellSize

            val x = (it.x / step).toInt()
            val y = (it.y / step).toInt()


            onCanvasEvent(
                x, y, when (it.button) {
                    MouseButton.PRIMARY -> 1
                    MouseButton.SECONDARY -> 2
                    MouseButton.MIDDLE -> 3
                    else -> 0
                }
            )
        }
    }

    private fun onCanvasEvent(x: Int, y: Int, value: Int) {
        println("Event $value on [$x / $y]")

        if (value > 1) {
            for ((i, values) in map.iterator().withIndex()) {
                for ((j, v) in values.iterator().withIndex()) {
                    if (v == value) {
                        map[i][j] = 0
                    }
                }
            }
            map[x][y] = value;
            if (value == 2)
                start = Point2D(x.toDouble(), y.toDouble())
            else
                end = Point2D(x.toDouble(), y.toDouble())
        } else {
            map[x][y] = when (map[x][y]) {
                0 -> 1
                else -> 0
            }
        }

        draw()
    }

    fun draw() {
        val step = canvas.width / cellSize

        canvas.children.removeIf { it !is Line }

        for ((i, values) in map.iterator().withIndex()) {
            for ((j, value) in values.iterator().withIndex()) {
                if (value > 0) {
                    val block = Rectangle()

                    block.fill = when (value) {
                        1 -> Color.BLACK
                        2 -> Color.GREEN
                        3 -> Color.RED
                        else -> Color.BEIGE
                    }

                    block.apply {
                        x = i * step
                        y = j * step
                        width = step
                        height = step
                    }

                    canvas.add(block);
                }
            }
        }

        info.text = "Distance : ${start.distance(end).roundToInt()}\n\n" +
                "Generation: $generation"
    }

    fun initCanvas() {
        canvas.clear()
        val width = canvas.width;
        val height = canvas.height;

        if (cellSizeInput.text.isNullOrBlank() || cellSizeInput.text.toIntOrNull() == null) {
            cellSizeInput.text = "20"
        }

        cellSize = cellSizeInput.text.toInt()

        println("Init with cell width $cellSize [$height/$width]")

        val step = width / cellSize
        for (i in 0 until cellSize) {
            canvas.add(Line().apply {
                startX = (i * step)
                startY = 0.0
                endY = height
                endX = startX
            })
            canvas.add(Line().apply {
                startX = 0.0
                startY = (i * step)
                endY = startY
                endX = width
            })
        }

        map = Array(cellSize) { IntArray(cellSize) { 0 } }

        onCanvasEvent(1, 1, 3)
        onCanvasEvent(cellSize - 2, cellSize - 2, 2)
    }

    fun findPath() {
        println("Before $start")
        val solution = findSolution(
            size = cellSize,
            map = map,
            start = start,
            end = end,
            draw = { draw() }
        )
        start = solution.first
        generation = solution.second
        println("After $start")

        onCanvasEvent(start.x.toInt(), start.y.toInt(), 2)
    }
}