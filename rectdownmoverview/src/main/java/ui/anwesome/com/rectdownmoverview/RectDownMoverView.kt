package ui.anwesome.com.rectdownmoverview

/**
 * Created by anweshmishra on 02/03/18.
 */
import android.content.*
import android.graphics.*
import android.view.*
val colors:Array<Int> = arrayOf(Color.parseColor("#009688"), Color.parseColor("#00BCD4"), Color.parseColor("#c62828"), Color.parseColor("#7B1FA2"))
class RectDownMoverView(ctx : Context) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas : Canvas) {

    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {
        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if(Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            if(dir == 0f) {
                dir = 1 - 2 * scale
                startcb()
            }
        }
    }
    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(updatecb : () -> Unit) {
            if(animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    data class ContainerState(var n : Int, var j : Int = 0, var dir : Int = 0) {
        fun incrementCounter() {
            j += dir
            if(j == n || j == -1) {
                j -= dir
                dir *= -1
            }
        }
    }
    data class RectDown(var i : Int, var text : String, var size: Float) {
        val state = State()
        fun draw(canvas : Canvas, paint : Paint) {
            val y = size * i - size
            canvas.save()
            canvas.translate(size/2, size/10 + y + size * state.scale)
            paint.color = colors[i % colors.size]
            canvas.drawRect(RectF(-size/2 , - size/2 , size/2, size/2), paint)
            paint.color = Color.WHITE
            paint.textSize = size/10
            val tw = paint.measureText(text)
            canvas.drawText(text, -tw/2, size/30, paint)
            canvas.restore()
        }
        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }
}