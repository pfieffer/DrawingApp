package com.kidsdrawingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO(Step 2 : The default brush size is set programmatically when the app is launched)
        //START
        drawing_view.setSizeForBrush(20.toFloat()) // Setting the default brush size to drawing view.
        // END
    }
}