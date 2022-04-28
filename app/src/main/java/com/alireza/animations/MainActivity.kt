package com.alireza.animations

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.alireza.animations.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var click = false
    private lateinit var binding: ActivityMainBinding
    lateinit var xAnimation: SpringAnimation
    lateinit var yAnimation: SpringAnimation
    lateinit var scaleGestureDetector: ScaleGestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postion()


        val animTranY = createSpringAnimation(
            binding.imageView, DynamicAnimation.TRANSLATION_Y,
            120f,
            SpringForce.STIFFNESS_MEDIUM,
            SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        ).addEndListener(object : DynamicAnimation.OnAnimationEndListener {
            override fun onAnimationEnd(
                animation: DynamicAnimation<*>?,
                canceled: Boolean,
                value: Float,
                velocity: Float
            ) {
                if (animation != null) {
                    if (animation.isRunning().not())
                        createSpringAnimation(
                            binding.imageView, DynamicAnimation.TRANSLATION_Y,
                            -120f,
                            SpringForce.STIFFNESS_MEDIUM,
                            SpringForce.DAMPING_RATIO_HIGH_BOUNCY
                        ).start()
                }
            }
        })

        val animTranX = createSpringAnimation(
            binding.imageView, DynamicAnimation.TRANSLATION_X,
            120f,
            SpringForce.STIFFNESS_MEDIUM,
            SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        )
        val animClass = createSpringAnimation(
            binding.imageView, DynamicAnimation.SCALE_X, 2f,
            SpringForce.STIFFNESS_MEDIUM,
            SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        )


        val animRotate = createSpringAnimation(
            binding.imageView, DynamicAnimation.ROTATION_X,
            0.5f,
            SpringForce.STIFFNESS_HIGH,
            SpringForce.DAMPING_RATIO_LOW_BOUNCY
        )
        val animScale = createSpringAnimation(
            binding.imageView, DynamicAnimation.SCALE_X,
            3f,
            SpringForce.STIFFNESS_HIGH,
            SpringForce.DAMPING_RATIO_LOW_BOUNCY
        )
        val animAlpha = createSpringAnimation(
            binding.imageView, DynamicAnimation.ALPHA,
            0.2f,
            SpringForce.STIFFNESS_HIGH,
            SpringForce.DAMPING_RATIO_LOW_BOUNCY
        )
        animAlpha.minimumVisibleChange = DynamicAnimation.MIN_VISIBLE_CHANGE_ALPHA

        val animTranFling = createFlingAnimation(
            binding.imageView, DynamicAnimation.TRANSLATION_Y,
            1000f,
            1.9f
        )
        val animScaleFling = createFlingAnimation(
            binding.imageView, DynamicAnimation.SCALE_X,
            30f,
            1.5f
        )

        binding.tranY.setOnClickListener {
            animTranY.start()
        }
        binding.scale.setOnClickListener {
            animScale.start()
        }
        binding.rotate.setOnClickListener {
            animRotate.start()
        }
        binding.tranX.setOnClickListener {
            animTranX.start()
        }
        binding.alpha.setOnClickListener {
            animAlpha.start()
        }
        binding.scaleFling.setOnClickListener {
            animScaleFling.start()
        }
        binding.tranYFling.setOnClickListener {
            animTranFling.start()
        }

    }

    private fun createFlingAnimation(
        view: View,
        property: DynamicAnimation.ViewProperty,
        velocity: Float,
        frictionValue: Float
    ): FlingAnimation {
        return FlingAnimation(view, property).apply {
            setStartVelocity(-velocity)
            friction = frictionValue
        }

    }


    private fun createSpringAnimation(
        view: View,
        property: DynamicAnimation.ViewProperty,
        finalPosition: Float,
        stiffness: Float,
        dampingRatio: Float
    ): SpringAnimation {
        val animation = SpringAnimation(view, property)
        val spring = SpringForce(finalPosition)
        spring.stiffness = stiffness
        spring.dampingRatio = dampingRatio
        animation.spring = spring
        return animation
    }

    @SuppressLint("ClickableViewAccessibility")
    fun postion() {
        // create X and Y animations for view's initial position once it's known
        binding.imageView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                xAnimation = createSpringAnimation(
                    binding.imageView,
                    SpringAnimation.X,
                    binding.imageView.x,
                    SpringForce.STIFFNESS_MEDIUM,
                    SpringForce.DAMPING_RATIO_HIGH_BOUNCY
                )
                yAnimation = createSpringAnimation(
                    binding.imageView,
                    SpringAnimation.Y,
                    binding.imageView.y,
                    SpringForce.STIFFNESS_MEDIUM,
                    SpringForce.DAMPING_RATIO_HIGH_BOUNCY
                )
                binding.imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        var dX = 0f
        var dY = 0f
        binding.imageView.setOnTouchListener { view, event ->
            when (event?.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    // capture the difference between view's top left corner and touch point
                    dX = view?.x!! - event.rawX
                    dY = view?.y!! - event.rawY

                    // cancel animations so we can grab the view during previous animation
                    xAnimation.cancel()
                    yAnimation.cancel()
                }
                MotionEvent.ACTION_MOVE -> {
                    //  a different approach would be to change the view's LayoutParams.
                    binding.imageView.x = event.rawX + dX
                    binding.imageView.y = event.rawY + dX
                    binding.imageView.requestLayout()
                }
                MotionEvent.ACTION_UP -> {
                    xAnimation.start()
                    yAnimation.start()
                }
            }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun scale() {
        // create scaleX and scaleY animations
        var scaleXAnimation = createSpringAnimation(
            binding.imageView, SpringAnimation.SCALE_X,
            1F, SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        )
        var scaleYAnimation = createSpringAnimation(
            binding.imageView, SpringAnimation.SCALE_Y,
            1F, SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        )

        setupPinchToZoom()

        binding.imageView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                scaleXAnimation.start()
                scaleYAnimation.start()
            } else {
                // cancel animations so we can grab the view during previous animation
                scaleXAnimation.cancel()
                scaleYAnimation.cancel()

                // pass touch event to ScaleGestureDetector
                scaleGestureDetector.onTouchEvent(event)
            }
            true
        }
    }

    private fun setupPinchToZoom() {
        var scaleFactor = 1f
        scaleGestureDetector = ScaleGestureDetector(this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scaleFactor *= detector.scaleFactor
                    binding.imageView.scaleX *= scaleFactor
                    binding.imageView.scaleY *= scaleFactor
                    return true
                }
            })
    }
}

