package com.fg.mdp.facemonitor

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fg.mdp.facemonitor.connections.CallAPIService
import com.fg.mdp.facemonitor.model.ImageBgModel
import com.fg.mdp.facemonitor.model.MqttMsgModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity(), Balloon.BalloonListener {
    private val TAG = "mqttPublisher"

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var slidingImageAdapter: SlidingImageAdapter? = null

    private val MIN_ANIMATION_DELAY = 500
    private val MAX_ANIMATION_DELAY = 1500
    private val MIN_ANIMATION_DURATION = 1000
    private val MAX_ANIMATION_DURATION = 8000

    private var mContentView: ViewGroup? = null
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private val mBalloons: ArrayList<Balloon> = ArrayList()
    private var mBalloonsPopped = 0


    var mqttAndroidClient: MqttAndroidClient? = null
    private var mqttConnectOptions: MqttConnectOptions? = null

    private val server = "tcp://hr.freewillgroup.com:1883"
    private val clientId = "Teeboq" + System.currentTimeMillis()
    private val username = "mdc@freewill"
    private val password = "mdc@123"
    private var isConnect = false

    var msg: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        initializeMqttAndroidClient()


//        setAdapter()
        mContentView = findViewById<ViewGroup>(R.id.activity_main)
        setToFullScreen()
        val viewTreeObserver = mContentView!!.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // your code here. `this` should work
                    mContentView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    mScreenHeight = mContentView!!.height
                    mScreenWidth = mContentView!!.width
                }
            })
        }

        mContentView!!.setOnClickListener { setToFullScreen() }

        slidingImageAdapter = SlidingImageAdapter(supportFragmentManager)
        requestGetBG()

    }

    private fun setAdapter(msg: String) {
        var imageBG: ImageBgModel? = Gson().fromJson(msg, ImageBgModel::class.java)

//        var imageBG = ArrayList<Int>()
//        imageBG.add(R.drawable.bg1)
//        imageBG.add(R.drawable.bg2)
//        imageBG.add(R.drawable.bg3)
//        imageBG.add(R.drawable.bg4)
//        imageBG.add(R.drawable.bg5)
//        imageBG.add(R.drawable.bg6)

        pager.offscreenPageLimit = imageBG!!.imagebg.size
//        slidingImageAdapter = SlidingImageAdapter(supportFragmentManager)
        slidingImageAdapter!!.updateData(imageBG.imagebg)
        pager.adapter = slidingImageAdapter

        indicator.setViewPager(pager)
        pager?.startAutoScroll()
//        mTitlePageIndicator.setViewPager(viewPager)
    }

    private fun setToFullScreen() {
        val rootLayout = findViewById<ViewGroup>(R.id.activity_main)
        rootLayout.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onResume() {
        super.onResume()
        setToFullScreen()
    }

//    private fun startGame() {
//        setToFullScreen()
////        mGameStopped = false
//        startLevel()
//    }
//
//    private fun startLevel() {
//
//        BalloonLauncher(msg)
//
//        mBalloonsPopped = 0
//    }


    override fun popBalloon(balloon: Balloon?, userTouch: Boolean) {
        mBalloonsPopped++
        mContentView!!.removeView(balloon)
        mBalloons.remove(balloon)
    }

    private fun gameOver(allPinsUsed: Boolean) {
        Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
        for (balloon in mBalloons) {
            mContentView!!.removeView(balloon)
            balloon.setPopped(true)
        }
        mBalloons.clear()
//        mGameStopped = true
    }

    override fun onStop() {
        super.onStop()
        gameOver(false)
    }

    private fun BalloonLauncher(msg: String) {
        Log.d(TAG, msg)

        var dataMqtt: MqttMsgModel = Gson().fromJson(msg, MqttMsgModel::class.java)


        setToFullScreen()

        val mScreenWidth_ = mScreenWidth
        val maxDelay: Int = Math.max(
            MIN_ANIMATION_DELAY,
            MAX_ANIMATION_DELAY - (0) * 500
        )
        val minDelay = maxDelay / 2
//            var balloonsLaunched = 0
//            while (mPlaying && balloonsLaunched < BALLOONS_PER_LEVEL) {
        //  Get a random horizontal position for the next balloon
        val random = Random()
        val xPosition = mScreenWidth_.minus(200).let { random.nextInt(it) }
//        publishProgress(xPosition)
//            balloonsLaunched++
        //              Wait a random number of milliseconds before looping
//        val delay = random.nextInt(minDelay) + minDelay
//        try {
//            Thread.sleep(delay.toLong())
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }


        launchBalloon(xPosition, dataMqtt.pic_path, dataMqtt.empid, dataMqtt.timepstamp)
    }

    private fun launchBalloon(x: Int?, url: String, name: String, time: String) {


        val balloon = Balloon(this@MainActivity, url, name, time)

//        val balloon = Balloon(this, "https://pngimage.net/wp-content/uploads/2018/05/decoration-png-cute-1.png", "Name","time")
        mBalloons.add(balloon)

        //      Set balloon vertical position and dimensions, add to container
        if (x != null) {
            balloon.x = x.toFloat()
        }
        balloon.y = mScreenHeight + balloon.height.toFloat()
        mContentView!!.addView(balloon)
        //      Let 'er fly
        val duration: Int = Math.max(
            MIN_ANIMATION_DURATION,
            MAX_ANIMATION_DURATION - 1 * 1000
        )
//        val duration: Int = Math.max(
//            MIN_ANIMATION_DURATION,
//            MAX_ANIMATION_DURATION - mLevel * 1000
//        )
        balloon.releaseBalloon(mScreenHeight, duration)

        mBalloonsPopped = 0

//        mGoButton!!.text = "Stop Game"

    }

//    override fun onTaskCompleted(xPosition: Int?) {
//        launchBalloon(xPosition)
//    }


    private fun initializeMqttAndroidClient() {
        Log.i(TAG, "initializeMqttAndroidClient")

        mqttAndroidClient = MqttAndroidClient(applicationContext, server, clientId)
        mqttAndroidClient!!.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(
                reconnect: Boolean,
                serverURI: String
            ) {
                Log.i(TAG, "connectComplete")
                if (reconnect) {
                    Log.w(TAG, "reconnect")
                    //                    showToast("Reconnected to : " + serverURI);
// Because Clean Session is true, we need to re-subscribe
//                    supportActionBar!!.title = getString(R.string.app_name)
                } else { //                    showToast("Connected to" + serverURI);
                    Log.w(TAG, "Connected $serverURI")
//                    supportActionBar!!.title = "Connected $serverURI"
                    isConnect = true
                    //                    onSubscribe("/FWG/TEST");
//                    onSubscribe("/FW/FaceInfo")
                }
                onSubscribe("/FW/FaceInfo")
                onSubscribe("/FW/FaceBG")

            }

            override fun connectionLost(cause: Throwable) { // addToHistory("The Connection was lost.");
                Log.w(TAG, "The Connection was lost.")
//                supportActionBar!!.setTitle(getString(R.string.app_name))
            }

            override fun messageArrived(
                topic: String,
                message: MqttMessage
            ) { //addToHistory("Incoming message: " + new String(message.getPayload()));
                Log.i(
                    TAG,
                    "Incoming topic: $topic"
                )
                Log.i(
                    TAG,
                    "Incoming message: " + String(message.payload)

                )
                msg = String(message.payload)
                if (topic.equals("/FW/FaceInfo")) {
//                    msg = String(message.payload)

                    BalloonLauncher(msg)
                } else if (topic.equals("/FW/FaceBG")) {
                    setAdapter(msg)
                }
                //                mAdapter.addMessage(new MessageModel(topic, new String(message.getPayload()), System.currentTimeMillis()));
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {}
        })
        mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions!!.setCleanSession(false)
        mqttConnectOptions!!.setUserName(username)
        mqttConnectOptions!!.setPassword(password.toCharArray())
        mqttConnectOptions!!.setKeepAliveInterval(30)
        mqttConnectOptions!!.setAutomaticReconnect(true)
        try { //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient!!.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions =
                        DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient!!.setBufferOpts(disconnectedBufferOptions)
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.i(
                        TAG,
                        "onFailure " + exception.message
                    )
                    //addToHistory("Failed to connect to: " + shareData.getServerBroker());
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    /*private void addToHistory(String mainText) {
        System.out.println("LOG: " + mainText);
        mAdapter.add(mainText);
        mAdapter.notifyDataSetChanged();
        mLayoutManager.scrollToPosition((mAdapter.getHistory().size() - 1));

        Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }*/
    private fun onSubscribe(topic: String) {
        try {
            mqttAndroidClient!!.subscribe(topic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.d("onSuccess", "ENTER")

                    //addToHistory("Subscribed! to topic: " + shareData.getSubscribeTopic());
//                    showToast("Subscribed! to topic: " + shareData.getSubscribeTopic());
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.d("onFailure", "ENTER")
                    //addToHistory("Failed to subscribe");
//                    showToast("Failed to subscribe");
                }
            })

        } catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing")
            ex.printStackTrace()
        }
    }


    fun requestGetBG() {
        CallAPIService.run {
            instance.requesGetBg().subscribe({ modelRes: Response<ImageBgModel> ->
                Log.d("modelRes", "ENTER")

                if (modelRes.isSuccessful) {
                    var res = modelRes.body()

                    Log.d("isSuccessful", "ENTER")
                    setAdapterFromApi(res)
//                    var imageBG: ImageBgModel? = Gson().fromJson(res., ImageBgModel::class.java)

                } else {

                    var errorBody = modelRes.errorBody()?.string()
                    Log.d("Response errorBody", errorBody.toString())
//                    errorInsuranceLeaderboard.value = errorBody
                }
            }, { e ->
                //                errorInsuranceLeaderboard.value = (e.message)
                Log.e("error", "requesRegister " + e.message)
            })

        }
    }

    private fun setAdapterFromApi(msg: ImageBgModel?) {
//        var imageBG: ImageBgModel? = Gson().fromJson(msg, ImageBgModel::class.java)

//        var imageBG = ArrayList<Int>()
//        imageBG.add(R.drawable.bg1)
//        imageBG.add(R.drawable.bg2)
//        imageBG.add(R.drawable.bg3)
//        imageBG.add(R.drawable.bg4)
//        imageBG.add(R.drawable.bg5)
//        imageBG.add(R.drawable.bg6)

        pager.offscreenPageLimit = msg!!.imagebg.size
//        slidingImageAdapter = SlidingImageAdapter(supportFragmentManager)
        slidingImageAdapter!!.updateData(msg.imagebg)
        pager.adapter = slidingImageAdapter

        indicator.setViewPager(pager)
        pager?.startAutoScroll()
//        mTitlePageIndicator.setViewPager(viewPager)
    }
}
