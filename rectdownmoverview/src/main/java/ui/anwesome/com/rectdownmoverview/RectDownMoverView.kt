package ui.anwesome.com.rectdownmoverview

/**
 * Created by anweshmishra on 02/03/18.
 */
import android.app.Activity
import android.content.*
import android.graphics.*
import android.view.*
import java.util.concurrent.ConcurrentLinkedQueue

val colors:Array<Int> = arrayOf(Color.parseColor("#009688"), Color.parseColor("#00BCD4"), Color.parseColor("#c62828"), Color.parseColor("#7B1FA2"))
class RectDownMoverView(ctx : Context, var text : String) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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
                stopcb(scale)
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
    data class ContainerState(var n : Int, var j : Int = 0, var dir : Int = 1) {
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
            paint.textSize = size/3
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
    data class RectDownContainer(var text : String, var w : Float, var h : Float) {
        val rectDowns : ConcurrentLinkedQueue<RectDown> = ConcurrentLinkedQueue()
        val state = ContainerState(text.split(" ").size)
        init {
            val textParts = text.split(" ")
            if(textParts.size > 0) {
                var size = h / (2 * textParts.size)
                for (i in 0..textParts.size - 1) {
                    rectDowns.add(RectDown(i, textParts[i], size))
                }
            }
        }
        fun draw(canvas : Canvas, paint : Paint) {
            var i = state.j
            canvas.save()
            canvas.translate(w/2, h/40)
            while(i >= 0) {
                rectDowns.at(i)?.draw(canvas, paint)
                i--
            }
            canvas.restore()
        }
        fun startUpdating(startcb : () -> Unit) {
            rectDowns.at(state.j)?.startUpdating(startcb)
        }
        fun update(stopcb : (Float) -> Unit) {
            rectDowns.at(state.j)?.update {
                state.incrementCounter()
                stopcb(it)
            }
        }
    }
    data class Renderer(var view : RectDownMoverView, var time : Int = 0) {
        var rectDownContainer : RectDownContainer ?= null
        val animator = Animator(view)
        fun render(canvas : Canvas, paint : Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                rectDownContainer = RectDownContainer(view.text, w, h)
            }
            canvas.drawColor(Color.parseColor("#212121"))
            rectDownContainer?.draw(canvas, paint)
            time++
            animator.animate {
                rectDownContainer?.update {
                    animator.stop()
                }
            }
        }
        fun handleTap() {
            rectDownContainer?.startUpdating {
                animator.start()
            }
        }
    }
    companion object {
        fun create(activity : Activity, text : String) : RectDownMoverView {
            val view = RectDownMoverView(activity, text)
            activity.setContentView(view)
            return view
        }
    }
}
fun ConcurrentLinkedQueue<RectDownMoverView.RectDown>.at(index : Int) : RectDownMoverView.RectDown? {
    var i = 0
    forEach {
        if(i == index) {
            return it
        }
        i++
    }
    return null
}