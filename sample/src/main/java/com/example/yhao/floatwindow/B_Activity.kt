package com.example.yhao.floatwindow

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import com.example.yhao.fixedfloatwindow.R

class B_Activity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)
        title = "B"
    }

    fun change(view: View) {
        startActivity(Intent(this, C_Activity::class.java))
    }

    fun back(view: View) {
        finish()
    }
}
