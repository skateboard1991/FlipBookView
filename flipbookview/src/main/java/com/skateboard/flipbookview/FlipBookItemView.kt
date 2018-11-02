package com.skateboard.flipbookview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class FlipBookItemView(context: Context, attributeSet: AttributeSet?) : FrameLayout(context, attributeSet) {

    private var lastTouchX=0f

    private var lastTouchY=0f

    private var pointA = Point()

    private var pointB = Point()

    private var pointE = Point()

    private var pointK = Point()

    private var pointC = Point()

    private var pointL = Point()

    private var pointD = Point()

    private var pointF = Point()

    private var pointG = Point()

    private var pointH = Point()

    private var pointI = Point()

    private var pointJ = Point()

    private var path = Path()


    private var pageColor = Color.WHITE
        set(value) {
            field = value
            edgePaint.color = value
            if (isAttachedToWindow) {
                postInvalidate()
            }
        }

    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val TOUCH_TOP = 0

    private val TOUCH_MID = 1

    private val TOUCH_END = 2

    private var touchPos = -1


    constructor(context: Context) : this(context, null)

    init {

        if (attributeSet != null) {
            pareAttrs(attributeSet)
        }
        initPaint()
    }

    private fun pareAttrs(attributeSet: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FlipBookItemView)
        pageColor = typedArray.getColor(R.styleable.FlipBookItemView_page_color, pageColor)
        typedArray.recycle()
    }

    private fun initPaint() {
        edgePaint.color = pageColor
    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX=event.x
                lastTouchY=event.y
                when {
                    event.y < height / 3 -> this.touchPos = TOUCH_TOP
                    event.y > height - height / 3 -> this.touchPos = TOUCH_END
                    else -> this.touchPos = TOUCH_MID
                }

            }

            MotionEvent.ACTION_MOVE -> {
                calPoints(event.x,event.y)
                if(pointG.x<=0)
                {
                    calPoints(lastTouchX,lastTouchY)
                }
                else{
                    lastTouchX=event.x
                    lastTouchY=event.y
                }
                postInvalidate()
            }

            MotionEvent.ACTION_UP -> {



            }
        }

        return true
    }



    private fun calPoints(x: Float,y:Float) {
        pointA.x = x
        pointA.y = y

        calPointB()

        pointE.x = (pointA.x + pointB.x) / 2
        pointE.y = (pointA.y + pointB.y) / 2

        pointK.x = pointE.x
        pointK.y = pointB.y

        pointC.x = pointK.x - (pointK.y - pointE.y) * (pointK.y - pointE.y) / (pointB.x - pointK.x)
        pointC.y = pointB.y


        pointG.x = pointC.x - (pointB.x - pointC.x) / 2
        pointG.y = pointB.y

        pointL.x = pointB.x
        pointL.y = pointE.y

        pointD.x = pointB.x
        pointD.y = pointE.y - (pointL.x - pointE.x) * (pointL.x - pointE.x) / (pointB.y - pointL.y)

        pointF.x = (pointA.x + pointE.x) / 2
        pointF.y = (pointA.y + pointE.y) / 2

        pointH.x = pointB.x
        pointH.y = pointD.y - (pointB.y - pointD.y) / 2

        pointI = getIntercetPoint(pointA, pointC, pointH, pointG)

        pointJ = getIntercetPoint(pointA, pointD, pointH, pointG)
    }

    private fun calPointB() {
        when {
            this.touchPos == TOUCH_END -> {
                pointB.x = width.toFloat()
                pointB.y = height.toFloat()
            }
            this.touchPos == TOUCH_TOP -> {
                pointB.x = width.toFloat()
                pointB.y = 0f
            }
            else -> {
                pointB.x = pointA.x
                pointB.y = height.toFloat()
            }
        }

    }

    private fun getIntercetPoint(point1: Point, point2: Point, point3: Point, point4: Point): Point {

        var x1 = point1.x
        var y1 = point1.y
        var x2 = point2.x
        var y2 = point2.y
        var x3 = point3.x
        var y3 = point3.y
        var x4 = point4.x
        var y4 = point4.y

        var pointX =
            ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1)) / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4))
        var pointY =
            ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4)) / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4))

        return Point(pointX, pointY)
    }

    override fun draw(canvas: Canvas?) {

        canvas?.let {
            it.save()
            calPath()
            it.clipPath(path, Region.Op.XOR)
            super.draw(it)
            it.restore()
        }
    }

    private fun calPath() {
        path.reset()
        path.moveTo(pointA.x, pointA.y)
        path.lineTo(pointI.x, pointI.y)
        path.quadTo(pointC.x, pointC.y, pointG.x, pointG.y)
        path.lineTo(pointB.x, pointB.y)
        path.lineTo(pointH.x, pointH.y)
        path.quadTo(pointD.x, pointD.y, pointJ.x, pointJ.y)
        path.lineTo(pointA.x, pointA.y)
    }


}