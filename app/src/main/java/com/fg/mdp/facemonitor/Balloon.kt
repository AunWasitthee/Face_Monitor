package com.fg.mdp.facemonitor

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class Balloon : RelativeLayout, AnimatorListener, AnimatorUpdateListener {
    private var mAnimator: ValueAnimator? = null
    private var mListener: BalloonListener? = null
    private var mPopped = false

    constructor(context: Context) : super(context)
    constructor(context: Context, url: String, name: String, time: String) : super(context) {

        mListener = context as BalloonListener?
        //        this.setImageResource(R.drawable.balloon);
//
//        this.setColorFilter(color);
//        val rawWidth = rawHeight / 2
//        val dpHeight: Int = PixelHelper.pixelsToDp(rawHeight, context)
//        val dpWidth: Int = PixelHelper.pixelsToDp(rawWidth, context)
        //        RelativeLayout RlMain = new RelativeLayout(context);
//
//        TextView tv = new TextView(context);
//        tv.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//        tv.setText("555");
//
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth, dpHeight);
        val LLMain = LinearLayout(context)
        LLMain.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        LLMain.orientation = LinearLayout.VERTICAL
//        LLMain.setBackgroundColor(Color.RED)
        LLMain.gravity = Gravity.CENTER


        val RLimg = RelativeLayout(context)
        RLimg.layoutParams = LayoutParams(
            300,
            300
        )
        RLimg.setBackgroundColor(Color.BLUE)
        RLimg.setBackgroundResource(R.drawable.ic_balloon)

        LLMain.gravity = Gravity.CENTER
        val layoutParamsImg = LayoutParams(
            200,
            200
        )
        layoutParamsImg.addRule(CENTER_IN_PARENT)

        val img = ImageView(context)
//        img.setImageResource(R.drawable.ic_balloon)
        img.layoutParams = layoutParamsImg
//        img.setImageResource(R.drawable.ic_2171)

        Glide
            .with(context)
            .load(url).circleCrop().listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("onLoadFailed", "Enter")
                    Log.d("onLoadFailed", e.toString())

//                        val builder = AlertDialog.Builder(systemInfo.getMainActivity())
//                        builder.setMessage("ไม่สามารถโหลดรูปได้")
//                        builder.setPositiveButton("ok") { dialog, id -> }
//
//                        builder.show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })

            .diskCacheStrategy(
                DiskCacheStrategy.NONE
            )
            .skipMemoryCache(true) //                .centerCrop()
            .placeholder(R.drawable.ic_balloon)
            .into(img)

//        Glide
//            .with(context)
//            .load(R.drawable.ic_2171).circleCrop().listener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
//                    Log.d("onLoadFailed", "Enter")
//                    Log.d("onLoadFailed", e.toString())
//
////                        val builder = AlertDialog.Builder(systemInfo.getMainActivity())
////                        builder.setMessage("ไม่สามารถโหลดรูปได้")
////                        builder.setPositiveButton("ok") { dialog, id -> }
////
////                        builder.show()
//                    return false
//                }
//
//                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
//                    return false
//                }
//
//            })
//            .diskCacheStrategy(
//                DiskCacheStrategy.NONE)
//            .skipMemoryCache(true)
//            //                .centerCrop()
//                .placeholder(R.drawable.ic_balloon)
//            .into(img)

        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        RLimg.addView(img)

        val tv = TextView(context)
        tv.text = name
        tv.setTextColor(Color.BLACK)
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.setShadowLayer(1.5f, -1f, 1f, Color.LTGRAY)
//        tv.textSize = 30f
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.result_font))
        layoutParams.addRule(CENTER_HORIZONTAL)
        tv.layoutParams = layoutParams

        val tv2 = TextView(context)
        tv2.text = time
        tv2.setTextColor(Color.BLACK)
        tv2.setTypeface(tv2.typeface, Typeface.BOLD)
        tv.setShadowLayer(1.5f, -1f, 1f, Color.LTGRAY)
        layoutParams.addRule(CENTER_HORIZONTAL)
        tv2.layoutParams = layoutParams

        LLMain.addView(RLimg)
        LLMain.addView(tv)
        LLMain.addView(tv2)

        this.addView(LLMain)
    }

    fun releaseBalloon(screenHeight: Int, duration: Int) {
        mAnimator = ValueAnimator()
        mAnimator!!.duration = duration.toLong()
        mAnimator!!.setFloatValues(screenHeight.toFloat(), 0f)
        mAnimator!!.interpolator = LinearInterpolator()
        mAnimator!!.setTarget(this)
        mAnimator!!.addListener(this)
        mAnimator!!.addUpdateListener(this)
        mAnimator!!.start()
    }

    override fun onAnimationStart(animator: Animator) {}
    override fun onAnimationEnd(animator: Animator) {
        if (!mPopped) {
            mListener!!.popBalloon(this, false)
        }
    }

    override fun onAnimationCancel(animator: Animator) {
        if (!mPopped) {
            mListener!!.popBalloon(this, false)
        }
    }

    override fun onAnimationRepeat(animator: Animator) {}
    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        y = (valueAnimator.animatedValue as Float)
    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        if (!mPopped && event.action == MotionEvent.ACTION_DOWN) {
//            mListener!!.popBalloon(this, true)
//            mPopped = true
//            mAnimator!!.cancel()
//        }
//        //        return super.onTouchEvent(event);
//        return true
//    }

    fun setPopped(popped: Boolean) {
        mPopped = popped
        if (popped) {
            mAnimator!!.cancel()
        }
    }

    interface BalloonListener {
        fun popBalloon(balloon: Balloon?, userTouch: Boolean)
    }
}