package ui.anwesome.com.kotlinrectdownmoverview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.rectdownmoverview.RectDownMoverView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RectDownMoverView.create(this, "Hi all people I am back")
    }
}
