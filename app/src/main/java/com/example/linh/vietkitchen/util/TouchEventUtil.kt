package com.example.linh.vietkitchen.util

import android.graphics.Point
import android.graphics.PointF
import timber.log.Timber

/**
 * Created by linh on 15/02/2017.
 */

object TouchEventUtil {
    val MOVE_RIGHT = 1
    val MOVE_LEFT = MOVE_RIGHT shl 1
    val MOVE_DOWN = MOVE_RIGHT shl 2
    val MOVE_UP = MOVE_RIGHT shl 3

    // Use dx and dy to determine the direction
    fun determineDirection(dx: Float, dy: Float): Int {
        return if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0)
                MOVE_RIGHT
            else
                MOVE_LEFT
        } else {
            if (dy > 0)
                MOVE_DOWN
            else
                MOVE_UP
        }
    }

    /**
     *
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     * @param point1 the position of the first point
     * @param point2 the position of the second point
     */
    fun determineDirection(point1: PointF, point2: PointF): Int {
        return determineDirection(point1.x, point1.y, point2.x, point2.y)
    }

    /**
     *
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    fun determineDirection(x1: Float, y1: Float, x2: Float, y2: Float): Int {
        val angle = getAngle(x1, y1, x2, y2)
        val a = getSlope(x1, x2, x2, y2)
        Timber.e("determineDirection $angle")
        Timber.e("determineDirection slope $a")
        return getDirection(angle)
    }

    /**
     *
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    private fun getAngle(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        val rad = Math.atan2((y1 - y2).toDouble(), (x2 - x1).toDouble()) + Math.PI
        return (rad * 180 / Math.PI + 180) % 360
    }

    fun getSlope(x1: Float, y1: Float, x2: Float, y2: Float): Int {
        val angle = Math.toDegrees(Math.atan2((y1 - y2).toDouble(), (x2 - x1).toDouble()))
        return if (angle > 45 && angle <= 135)
            // top
            MOVE_UP
        else if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
            // left
            MOVE_LEFT
        else if (angle < -45 && angle >= -135)
            // down
            MOVE_DOWN
        else if (angle > -45 && angle <= 45)
            //right
            MOVE_RIGHT
        else 0
    }


    fun calculateMovementDistance(p1: PointF, p2: PointF): Double {
        return Math.sqrt(Math.pow((p1.x - p2.x).toDouble(), 2.0) + Math.pow((p1.y - p2.y).toDouble(), 2.0))
    }


    /**
     * Returns a direction given an angle.
     * Directions are defined as follows:
     *
     * Up: [45, 135]
     * Right: [0,45] and [315, 360]
     * Down: [225, 315]
     * Left: [135, 225]
     *
     * @param angle an angle from 0 to 360 - e
     * @return the direction of an angle
     */
    private fun getDirection(angle: Double): Int {
        return if (inRange(angle, 45, 135)) {
            MOVE_UP
        } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
            MOVE_RIGHT
        } else if (inRange(angle, 225, 315)) {
            MOVE_DOWN
        } else {
            MOVE_LEFT
        }
    }

    /**
     * @param angle an angle
     * @param init the initial bound
     * @param end the final bound
     * @return returns true if the given angle is in the interval [init, end).
     */
    private fun inRange(angle: Double, init: Int, end: Int): Boolean {
        return angle >= init && angle < end
    }
}
