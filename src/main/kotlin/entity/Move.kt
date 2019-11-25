package entity

import javafx.geometry.Point2D

enum class Move(val point: Point2D) {
    NORTH(Point2D(0.0, -1.0)),
    WEST(Point2D(-1.0, 0.0)),
    SOUTH(Point2D(0.0, 1.0)),
    EST(Point2D(1.0, 0.0)),
    NORTH_WEST(Point2D(-1.0, -1.0)),
    NORTH_EST(Point2D(1.0, -1.0)),
    SOUTH_WEST(Point2D(-1.0, 1.0)),
    SOUTH_EST(Point2D(1.0, 1.0)),
}