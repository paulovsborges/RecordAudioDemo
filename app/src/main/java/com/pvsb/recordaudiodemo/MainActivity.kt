package com.pvsb.recordaudiodemo

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    enum class LockerState {
        LOCKED,
        UNLOCKED
    }

    private var lockerState: LockerState = LockerState.UNLOCKED
        set(value) {
            field = value
            handleNewLockerState(value)
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mic = findViewById<ImageView>(R.id.ivMicrophone)

        mic.setOnTouchListener { _, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_DOWN && lockerState == LockerState.LOCKED) {
                lockerState = LockerState.UNLOCKED
            }

            if (motionEvent.action == MotionEvent.ACTION_MOVE && lockerState == LockerState.UNLOCKED) {
                if ((motionEvent.y / 100) < 0.5) {
                    lockerState = LockerState.LOCKED
                }
            }
            true
        }
    }

    private fun handleNewLockerState(newState: LockerState) {

        val locker = findViewById<ConstraintLayout>(R.id.clLockRecorder)
        val lockerIcon = findViewById<ImageView>(R.id.ivLocker)
        val lockerArrow = findViewById<ImageView>(R.id.ivLockerArrow)

        val applyAnim: (Boolean) -> Unit = fun(isLocked: Boolean) {

            val animDuration = if (isLocked) {
                250
            } else {
                100
            }

            val height = if (isLocked) {
                40
            } else {
                56
            }

            (locker.background as TransitionDrawable).apply {
                if (isLocked) {
                    startTransition(animDuration)
                } else {
                    reverseTransition(animDuration)
                }
            }

            ValueAnimator.ofInt(locker.height, height.toPx).apply {
                addUpdateListener {
                    val newValue = it.animatedValue as Int
                    val params = locker.layoutParams
                    params.height = newValue
                    locker.layoutParams = params
                }

                duration = animDuration.toLong()
                start()
            }
        }

        when (newState) {
            LockerState.LOCKED -> {
                lockerIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_locked))
                lockerArrow.visibility = View.GONE
                applyAnim(true)
            }

            LockerState.UNLOCKED -> {
                lockerIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_unlocked))
                lockerArrow.visibility = View.VISIBLE
                applyAnim(false)
            }
        }
    }
}

val Int.toPx get() = (this * Resources.getSystem().displayMetrics.density).toInt()