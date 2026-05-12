package com.skysoftlk.vpnapp.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdOptionsView
import com.facebook.ads.AdSettings
import com.facebook.ads.AdView
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdLayout
import com.facebook.ads.NativeAdListener
import com.facebook.ads.RewardedVideoAd
import com.facebook.ads.RewardedVideoAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.skysoftlk.vpnapp.R
import com.skysoftlk.vpnapp.Config
import com.skysoftlk.vpnapp.Utils.Constants
import com.skysoftlk.vpnapp.speed.Speed
import com.skysoftlk.vpnapp.ui.BaseDrawerActivity
import es.dmoral.toasty.Toasty
import ph.gemeaux.materialloadingindicator.MaterialCircularIndicator
import pl.droidsonroids.gif.GifImageView

abstract class ContentsActivity : BaseDrawerActivity() {

    private var mLastRxBytes: Long = 0
    private var mLastTxBytes: Long = 0
    private var mLastTime: Long = 0
    private var mSpeed: Speed? = null

    var lottieAnimationView: LottieAnimationView? = null
    var vpnToastCheck = true
    var handlerTraffic: Handler? = null
    private val adCount = 0

    private var mInterstitialAdMob: com.google.android.gms.ads.interstitial.InterstitialAd? = null
    private var loadingAd: Boolean? = false
    var frameLayout: RelativeLayout? = null
    var nativeAdLayout: NativeAdLayout? = null
    private var mRewardedAd: RewardedAd? = null
    @JvmField
   // var state: VPNState? = null

    var progressBarValue = 0
    var handler = Handler(Looper.getMainLooper())
    private val customHandler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L


    var tvIpAddress: TextView? = null
    var textDownloading: TextView? = null
    var textUploading: TextView? = null
    var tvConnectionStatus: TextView? = null
    var ivConnectionStatusImage: ImageView? = null
    var ivVpnDetail: ImageView? = null
    private lateinit var btnAddTime: RelativeLayout
    private lateinit var lytAddTime: CardView
    var timerTextView: TextView? = null
    var connectBtnTextView: ImageView? = null
    var connectionStateTextView: TextView? = null
    var rcvFree: RecyclerView? = null
    var footer: RelativeLayout? = null
    var gifImageView1: GifImageView? = null
    var gifImageView2: GifImageView? = null
    lateinit var sharedPreferences :SharedPreferences

    private var timer: CountDownTimer? = null
    private val twoHours = 7200000L
    private var timeLeft = 0L
    private var rewardedVideoAd: RewardedVideoAd? = null
    @JvmField
    var imgFlag: ImageView? = null

    @JvmField
    var flagName: TextView? = null

    //adMob native advance
    var facebookAdView: AdView? = null
    private var nativeAd: NativeAd? = null

    @JvmField
    var mInterstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd? = null

    @JvmField
    var facebookInterstitialAd: InterstitialAd? = null

    protected var STATUS: String? = "DISCONNECTED"

    fun getVpnStatus(): String? {
        return STATUS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textDownloading = findViewById(R.id.downloading)

        textUploading = findViewById(R.id.uploading)

        tvConnectionStatus = findViewById(R.id.connection_status)

        ivConnectionStatusImage = findViewById(R.id.connection_status_image)

        ivVpnDetail = findViewById(R.id.vpn_details)

        btnAddTime = findViewById(R.id.btnAddTime)

        lytAddTime = findViewById(R.id.lytAddTime)

        timerTextView = findViewById(R.id.tv_timer)

        connectBtnTextView = findViewById(R.id.connect_btn)

        connectionStateTextView = findViewById(R.id.connection_state)

        imgFlag = findViewById(R.id.flag_image)

        rcvFree = findViewById(R.id.rcv_free)


        flagName = findViewById(R.id.flag_name)

        footer = findViewById(R.id.footer)

        frameLayout = findViewById(R.id.fl_adplaceholder)

        nativeAdLayout = findViewById(R.id.native_ad_container)

        gifImageView1 = findViewById(R.id.gifImageView1)
        gifImageView2 = findViewById(R.id.gifImageView2)

        connectBtnTextView?.setOnClickListener {
            btnConnectDisconnect()
        }

        findViewById<View>(R.id.ic_crown).setOnClickListener {
            startActivity(Intent(this, UnlockAllActivity::class.java))
        }

        tvIpAddress = findViewById<TextView>(R.id.tv_ip_address)
        showIP()

//        Lottie animation to show animation in the project
        lottieAnimationView = findViewById(R.id.animation_view)

        ivVpnDetail?.setOnClickListener {
            if(Constants.FREE_SERVERS != "server" && Constants.PREMIUM_SERVERS != "")
                showServerList()
            else
                showMessage("Loading servers. Please try again", "")
        }

        flagName?.setOnClickListener {
            if(Constants.FREE_SERVERS != "server" && Constants.PREMIUM_SERVERS != "")
                showServerList()
            else
                showMessage("Loading servers. Please try again", "")
        }

        btnAddTime.setOnClickListener {
            showRewardedAdDialog("Watch an ad to add more time to your connection or purchase VIP")
        }

        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottom_navigation)
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.computer))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.battery))
        bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.cooling))

        bottomNavigation.show(1)
        bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                2 -> {
                    loadAds(it.id)
                }
                3 -> {
                    loadAds(it.id)
                }
                4 -> {
                    loadAds(it.id)
                }
            }
        }

        AdSettings.addTestDevice("a8498a8c-111d-4c26-bc48-c9ba6d019845");
    }

    private fun showRewardedAdDialog(msg: String) {
        val alertDialog = Dialog(this)
        alertDialog.setContentView(R.layout.dialog_add_time)
        alertDialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)))
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()

        val tvExtendTimeMsg = alertDialog.findViewById<TextView>(R.id.tvExtendTimeMsg)
        val purchase = alertDialog.findViewById<Button>(R.id.purchase)
        val btnAddTime = alertDialog.findViewById<Button>(R.id.btnAddTime)
        val progressIndicator = MaterialCircularIndicator(this)
        progressIndicator.setIndicatorColor(Color.parseColor("#1d89e4"))
        progressIndicator.setTrackCornerRadius(0)

        tvExtendTimeMsg.text = msg

        purchase.setOnClickListener {
            startActivity(
                Intent(
                   this@ContentsActivity,
                    UnlockAllActivity::class.java
                )
            )
            alertDialog.dismiss()
        }

        btnAddTime.setOnClickListener {

            alertDialog.dismiss()
            progressIndicator.show()

            if (MainActivity.type == "ad") {
                val adRequest = AdRequest.Builder().build()

                RewardedAd.load(this@ContentsActivity, MainActivity.indratech_toto_27640849_admob_reward,
                    adRequest, object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            mRewardedAd = null
                            Log.d("ADMOBTAG", "failed")
                            Toast.makeText(
                                this@ContentsActivity,
                                "ads Not Available Or Buy Subscription",
                                Toast.LENGTH_SHORT
                            ).show()
                            alertDialog.dismiss()
                            progressIndicator.dismiss()
                        }

                        override fun onAdLoaded(rewardedAd: RewardedAd) {
                            mRewardedAd = rewardedAd
                            progressIndicator.dismiss()
                            if (mRewardedAd != null) {
                                val activityContext: Activity = this@ContentsActivity
                                mRewardedAd!!.show(activityContext, OnUserEarnedRewardListener {
                                    Log.d("ADMOBTAG", "The user earned the reward.")
                                    addTimer(twoHours)
                                    alertDialog.dismiss()
                                })
                            } else {
                                Toast.makeText(
                                    this@ContentsActivity,
                                    "ads Not Available Or Buy Subscription",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            mRewardedAd!!.fullScreenContentCallback = object :
                                FullScreenContentCallback() {
                                override fun onAdShowedFullScreenContent() {
                                    Log.d("ADMOBTAG", "Ad was shown.")
                                    mRewardedAd = null
                                }

                                override fun onAdDismissedFullScreenContent() {
                                    Log.d("ADMOBTAG", "Ad was dismissed.")
                                    mRewardedAd = null
                                }
                            }
                        }
                    })
            } else {

                AudienceNetworkAds.initialize(this@ContentsActivity)

                rewardedVideoAd =
                    RewardedVideoAd(this@ContentsActivity, MainActivity.indratech_toto_27640849_fb_reward_id)

                val rewardedVideoAdListener: RewardedVideoAdListener =
                    object : RewardedVideoAdListener {
                        override fun onError(ad: Ad, error: AdError) {
                            progressIndicator.dismiss()
                            Toast.makeText(
                                this@ContentsActivity,
                                "ads Not Available Or Buy Subscription",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(
                                "FBTAG",
                                "Rewarded video ad failed to load: " + error.errorMessage
                            )
                            Log.e("FBTAG", MainActivity.indratech_toto_27640849_fb_reward_id)
                        }

                        override fun onAdLoaded(ad: Ad) {
                            progressIndicator.dismiss()
                            rewardedVideoAd!!.show()
                        }

                        override fun onAdClicked(ad: Ad) {
                            Log.d("FBTAG", "Rewarded video ad clicked!")
                        }

                        override fun onLoggingImpression(ad: Ad) {
                            Log.d("FBTAG", "Rewarded video ad impression logged!")
                        }

                        override fun onRewardedVideoCompleted() {
                            Log.d("FBTAG", "Rewarded video completed!")
                            addTimer(twoHours)
                            alertDialog.dismiss()
                        }

                        override fun onRewardedVideoClosed() {
                            Log.d("FBTAG", "Rewarded video ad closed!")
                        }
                    }
                rewardedVideoAd!!.loadAd(
                    rewardedVideoAd!!.buildLoadAdConfig()
                        .withAdListener(rewardedVideoAdListener).build()
                )
            }
        }
    }

    private fun addTimer(time: Long) {

        println("CHECKTIMER TEST")
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        timer = object : CountDownTimer(time + timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                timerTextView!!.text = formatMilliSecondsToTime(millisUntilFinished)
            }

            override fun onFinish() {
                disconnectFromVpn()
            }
        }.start()
    }

    private fun formatMilliSecondsToTime(milliseconds: Long): String? {
        val seconds = (milliseconds / 1000).toInt() % 60
        val minutes = (milliseconds / (1000 * 60) % 60).toInt()
        val hours = (milliseconds / (1000 * 60 * 60) % 24).toInt()
        return (twoDigitString(hours.toLong()) + ":" + twoDigitString(minutes.toLong()) + ":"
                + twoDigitString(seconds.toLong()))
    }

    private fun twoDigitString(number: Long): String? {
        if (number == 0L) {
            return "00"
        }
        return if (number / 10 == 0L) {
            "0$number"
        } else number.toString()
    }


    private val requestQueue by lazy { Volley.newRequestQueue(applicationContext) }

    private fun showIP() {
        val urlip = "https://api.ipify.org"

        val stringRequest =
                StringRequest(Request.Method.GET, urlip, { response ->
                    Log.d("IP_CHECK", "IP: $response")
                    tvIpAddress?.setText(response)
                })
                { e ->
                    Log.e("IP_CHECK", "Error fetching IP from $urlip: ${e.message}")
                    // Fallback to second service if first fails
                    showIPFallback()
                }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            15000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(stringRequest)
    }

    private fun showIPFallback() {
        val urlip = "https://checkip.amazonaws.com/"

        val stringRequest =
            StringRequest(Request.Method.GET, urlip, { response ->
                Log.d("IP_CHECK", "Fallback IP: $response")
                tvIpAddress?.setText(response)
            })
            { e ->
                Log.e("IP_CHECK", "Error fetching IP from fallback: ${e.message}")
                run {
                    tvIpAddress?.setText(getString(R.string.app_name))
                }
            }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            15000,
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(stringRequest)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        if (nativeAd != null) {
            nativeAd!!.destroy()
        }
        super.onDestroy()
    }

    private fun showOrHideAppendLayout() {
        if (footer!!.visibility == View.VISIBLE) {
            ivVpnDetail!!.setImageResource(R.drawable.ic_drop_down)
            footer!!.visibility = View.GONE
        } else {
            ivVpnDetail!!.setImageResource(R.drawable.ic_up)
            footer!!.visibility = View.VISIBLE
        }
    }

    private val mUIHandler = Handler(Looper.getMainLooper())
    val mUIUpdateRunnable: Runnable = object : Runnable {
        override fun run() {

            checkRemainingTraffic()
            mUIHandler.postDelayed(this, 10000)
        }
    }

    private fun btnConnectDisconnect() {
        if (STATUS != "DISCONNECTED") {
            disconnectAlert()
        } else {
            if (!Utility.isOnline(applicationContext)) {
                showMessage("No Internet Connection", "error")
            } else {
                checkSelectedCountry()
            }
        }
    }

    protected abstract fun checkRemainingTraffic()

    protected fun updateUI(status: String?) {
        if (status == null || status == STATUS) return
        when (status) {
            "CONNECTED" -> {
                STATUS = "CONNECTED"
                textDownloading!!.visibility = View.VISIBLE
                textUploading!!.visibility = View.VISIBLE
                gifImageView1!!.setBackgroundResource(R.drawable.gif)
                gifImageView2!!.setBackgroundResource(R.drawable.gif)
                connectBtnTextView!!.isEnabled = true
                connectionStateTextView!!.setText(R.string.connected)
                timerTextView!!.visibility = View.VISIBLE
                hideConnectProgress()
                showIP()

                Glide.with(this).load(R.drawable.gif).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.gif).into(gifImageView2!!)

                connectBtnTextView!!.visibility = View.VISIBLE
                tvConnectionStatus!!.text = "Selected"
                lottieAnimationView!!.visibility = View.GONE
                Toasty.success(this@ContentsActivity, "Server Connected", Toast.LENGTH_SHORT).show()

                if (!Config.vip_subscription && !Config.all_subscription) {
                    addTimer(twoHours)
                    lytAddTime.visibility = View.VISIBLE
                    showRewardedAdDialog("Watch Reward Ads to Increase 2 hours More Connection Time or Upgrade.")
                }
            }
            "AUTH" -> {
                STATUS = "AUTHENTICATION"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.auth)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "WAIT" -> {
                STATUS = "WAITING"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.wait)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "RECONNECTING" -> {
                STATUS = "RECONNECTING"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.recon)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "LOAD" -> {
                STATUS = "LOAD"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.connecting)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "ASSIGN_IP" -> {
                STATUS = "LOAD"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.assign_ip)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "GET_CONFIG" -> {
                STATUS = "LOAD"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.config)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "USERPAUSE" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus!!.text = "Not Selected"
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectBtnTextView!!.setImageResource(R.drawable.ic_on_off)
                tvConnectionStatus!!.text = "Not Selected"
                connectionStateTextView!!.setText(R.string.paused)
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
            }
            "NONETWORK" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus!!.text = "Not Selected"
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
                showIP()

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectBtnTextView!!.setImageResource(R.drawable.ic_on_off)
                tvConnectionStatus!!.text = "Not Selected"
                connectionStateTextView!!.setText(R.string.nonetwork)
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
            }
            "DISCONNECTED" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus!!.text = "Not Selected"
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
                timerTextView!!.visibility = View.INVISIBLE
                hideConnectProgress()
                showIP()

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectBtnTextView!!.setImageResource(R.drawable.ic_on_off)
                tvConnectionStatus!!.text = "Not Selected"
                connectionStateTextView!!.setText(R.string.disconnected)
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
                lytAddTime.visibility = View.GONE
            }
        }
    }

    protected fun hideConnectProgress() {
        connectionStateTextView!!.visibility = View.VISIBLE
    }

    protected fun disconnectAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Do you want to disconnect?")
        builder.setPositiveButton(
                "Disconnect"
        ) { _, _ ->
            disconnectFromVpn()
            STATUS = "DISCONNECTED"

            textDownloading!!.text = "0.0 kB/s"
            textUploading!!.text = "0.0 kB/s"

            showMessage("Server Disconnected", "success")
        }
        builder.setNegativeButton(
                "Cancel"
        ) { _, _ ->
            showMessage("VPN Remains Connected", "success")
        }
        builder.show()
    }

    private fun populateUnifiedNativeAdView(
            nativeAd:com.google.android.gms.ads.nativead.NativeAd,
            adView:NativeAdView
    ) {
        val mediaView: MediaView = adView.findViewById(R.id.ad_media)
        adView.mediaView = mediaView

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        (adView.headlineView as TextView).text = nativeAd.headline

        if (nativeAd.body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView!!.visibility = View.INVISIBLE
        } else {
            adView.priceView!!.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView!!.visibility = View.INVISIBLE
        } else {
            adView.storeView!!.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView!!.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView!!.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView!!.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }

    private fun refreshAd() {
        val adLoader = AdLoader.Builder(this, MainActivity.indratech_toto_27640849_aad_native_id)
                .forNativeAd { nativeAd ->
                    if (!Config.vip_subscription && !Config.all_subscription) {
                        frameLayout!!.visibility = View.VISIBLE
                    } else {
                        frameLayout!!.visibility = View.GONE
                    }
                    val adView = layoutInflater
                            .inflate(R.layout.ad_unified, null) as NativeAdView
                    if (canShowAd) {
                        populateUnifiedNativeAdView(nativeAd, adView)
                        frameLayout!!.removeAllViews()
                        frameLayout!!.addView(adView)
                    }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .build()

        adLoader.loadAd(
                AdRequest.Builder()
                        .build()
        )
    }

    private fun showServerList() {
        startActivity(Intent(this, Servers::class.java))
    }

    fun updateSubscription() {
        if (canShowAd) {
            //native
            Log.v("UPDATESUBS", "onStart----: ")
            if (MainActivity.type == "ad") {
                refreshAd()
            } else {
                val nativeAd: NativeAd = NativeAd(this, MainActivity.indratech_toto_27640849_fb_native_id)
                val nativeAdListener: NativeAdListener = object : NativeAdListener {
                    override fun onMediaDownloaded(ad: Ad) {}
                    override fun onError(ad: Ad, adError: AdError) {
                        Log.w("AdLoader", "" + MainActivity.indratech_toto_27640849_fb_native_id)
                        Log.w("AdLoader", "onAdFailedToLoad" + adError.errorMessage)
                    }

                    override fun onAdLoaded(ad: Ad) {
                        if (nativeAd == null || nativeAd !== ad) {
                            return
                        }
                        nativeAd.unregisterView()
                        if (!Config.vip_subscription && !Config.all_subscription) {
                            nativeAdLayout!!.visibility = View.VISIBLE
                        } else {
                            nativeAdLayout!!.visibility = View.GONE
                        }
                        val inflater = LayoutInflater.from(this@ContentsActivity)
                        val adView = inflater.inflate(
                                R.layout.native_banner_ad_layout,
                                nativeAdLayout,
                                false
                        ) as LinearLayout
                        nativeAdLayout!!.removeAllViews()
                        nativeAdLayout!!.addView(adView)
                        val adChoicesContainer: LinearLayout =
                                nativeAdLayout!!.findViewById<LinearLayout>(R.id.ad_choices_container)
                        val adOptionsView = AdOptionsView(this@ContentsActivity, nativeAd, nativeAdLayout)
                        adChoicesContainer.removeAllViews()
                        adChoicesContainer.addView(adOptionsView, 0)
                        val nativeAdIcon: com.facebook.ads.MediaView =
                                adView.findViewById(R.id.native_ad_icon)
                        val nativeAdTitle = adView.findViewById<TextView>(R.id.native_ad_title)
                        val nativeAdMedia: com.facebook.ads.MediaView =
                                adView.findViewById(R.id.native_ad_media)
                        val nativeAdSocialContext =
                                adView.findViewById<TextView>(R.id.native_ad_social_context)
                        val nativeAdBody = adView.findViewById<TextView>(R.id.native_ad_body)
                        val sponsoredLabel =
                                adView.findViewById<TextView>(R.id.native_ad_sponsored_label)
                        val nativeAdCallToAction =
                                adView.findViewById<Button>(R.id.native_ad_call_to_action)
                        nativeAdTitle.text = nativeAd.advertiserName
                        nativeAdBody.text = nativeAd.adBodyText
                        nativeAdSocialContext.text = nativeAd.adSocialContext
                        nativeAdCallToAction.visibility =
                                if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                        nativeAdCallToAction.text = nativeAd.adCallToAction
                        sponsoredLabel.text = nativeAd.sponsoredTranslation
                        val clickableViews: MutableList<View> = ArrayList()
                        clickableViews.add(nativeAdTitle)
                        clickableViews.add(nativeAdCallToAction)
                        nativeAd.registerViewForInteraction(
                                adView, nativeAdMedia, nativeAdIcon, clickableViews
                        )
                    }

                    override fun onAdClicked(ad: Ad) {}
                    override fun onLoggingImpression(ad: Ad) {}
                }
                nativeAd.loadAd(
                        nativeAd.buildLoadAdConfig()
                                .withAdListener(nativeAdListener)
                                .build()
                )
            }
        }
    }

    private fun loadAds(id: Int) {
        println("CHECKAD proceedToNextView")
        //interstitial
        if (canShowAd) {
            if (MainActivity.type == "ad") {
                if(loadingAd == false) {

                    val adRequest = AdRequest.Builder().build()
                    loadingAd = true

                    com.google.android.gms.ads.interstitial.InterstitialAd.load(this@ContentsActivity,
                            MainActivity.admob_interstitial_id,
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                                    // The mInterstitialAd reference will be null until
                                    // an ad is loaded.
                                    mInterstitialAdMob = interstitialAd
                                    Log.i("INTERSTITIAL", "onAdLoaded")
                                    loadingAd = false

                                    if (mInterstitialAdMob != null) {

                                        mInterstitialAdMob!!.show(this@ContentsActivity)

                                        mInterstitialAdMob!!.setFullScreenContentCallback(object :
                                                FullScreenContentCallback() {
                                            override fun onAdDismissedFullScreenContent() {
                                                // Called when fullscreen content is dismissed.
                                                Log.d("TAG", "The ad was dismissed.")
                                                proceedToNextView(id)
                                            }

                                            fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                                // Called when fullscreen content failed to show.
                                                Log.d("TAG", "The ad failed to show.")
                                                proceedToNextView(id)
                                            }

                                            override fun onAdShowedFullScreenContent() {
                                                // Called when fullscreen content is shown.
                                                // Make sure to set your reference to null so you don't
                                                // show it a second time.
                                                mInterstitialAdMob = null
                                                Log.d("TAG", "The ad was shown.")
                                            }
                                        })
                                    } else {
                                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                                        proceedToNextView(id)
                                    }
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    // Handle the error
                                    Log.i("INTERSTITIAL", loadAdError.message)
                                    proceedToNextView(id)
                                    loadingAd = false
                                    mInterstitialAdMob = null
                                }
                            })
                }
            } else {
                AudienceNetworkAds.initialize(this@ContentsActivity)
                val interstitialAdListener: InterstitialAdListener =
                        object : InterstitialAdListener {
                            override fun onInterstitialDisplayed(ad: Ad) {}
                            override fun onInterstitialDismissed(ad: Ad) {
                                proceedToNextView(id)
                            }

                            override fun onError(ad: Ad, adError: com.facebook.ads.AdError) {
                                Log.d(
                                        TAG,
                                        "An error occurred when loading ad ${adError.errorMessage}"
                                )
                                proceedToNextView(id)
                            }

                            override fun onAdLoaded(ad: Ad) {
                                facebookInterstitialAd!!.show()
                            }

                            override fun onAdClicked(ad: Ad) {}
                            override fun onLoggingImpression(ad: Ad) {}
                        }

                facebookInterstitialAd = InterstitialAd(
                        this@ContentsActivity,
                        MainActivity.indratech_toto_27640849_fb_interstitial_id
                )
                facebookInterstitialAd!!.loadAd(
                        facebookInterstitialAd!!
                                .buildLoadAdConfig()
                                .withAdListener(interstitialAdListener)
                                .build()
                )
            }
        } else {
            proceedToNextView(id)
        }
    }

    private val canShowAd: Boolean
        get() = MainActivity.indratech_toto_27640849_all_ads_on_off &&
                !Config.ads_subscription &&
                !Config.all_subscription &&
                !Config.vip_subscription

    private fun proceedToNextView(id: Int) {
        when (id) {
            2 -> {
                val intent = Intent(this@ContentsActivity, SpeedBoosterActivity::class.java)
                startActivity(intent)
            }
            3 -> {
                val intent = Intent(this@ContentsActivity, CPUCoolerActivity::class.java)
                startActivity(intent)
            }
            4 -> {
                val intent = Intent(this@ContentsActivity, BatteryActivity::class.java)
                startActivity(intent)
            }
        }

    }

    companion object {
        protected val TAG = MainActivity::class.java.simpleName
    }

    protected fun showMessage(msg: String?, type:String) {

        if(type == "success") {
            Toasty.success(
                    this@ContentsActivity,
                    msg + "",
                    Toast.LENGTH_SHORT
            ).show()
        } else if (type == "error") {
            Toasty.error(
                    this@ContentsActivity,
                    msg + "",
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            Toasty.normal(
                    this@ContentsActivity,
                    msg + "",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    open fun updateConnectionStatus(
            duration: String?,
            lastPacketReceive: String?,
            byteIn: String,
            byteOut: String
    ) {
        val byteinParts = byteIn.split("-").toTypedArray()
        val byteoutParts = byteOut.split("-").toTypedArray()

        val byteinKb = if (byteinParts.size > 1) byteinParts[1] else byteIn
        val byteoutKb = if (byteoutParts.size > 1) byteoutParts[1] else byteOut

        textDownloading!!.text = byteinKb
        textUploading!!.text = byteoutKb
       // timerTextView!!.text = duration
    }

    fun showInterstitialAndConnect() {
        println("CHECKAD prepare")
        if (MainActivity.indratech_toto_27640849_all_ads_on_off && !Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription) {
            if (MainActivity.type == "ad") {
                if (loadingAd == false) {

                    loadingAd = true
                    val adRequest = AdRequest.Builder().build()

                    com.google.android.gms.ads.interstitial.InterstitialAd.load(this@ContentsActivity,
                            MainActivity.admob_interstitial_id,
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                                    // The mInterstitialAd reference will be null until
                                    // an ad is loaded.
                                    mInterstitialAdMob = interstitialAd
                                    Log.i("INTERSTITIAL", "onAdLoaded")
                                    loadingAd = false

                                    if (mInterstitialAdMob != null) {

                                        mInterstitialAdMob!!.show(this@ContentsActivity)

                                        mInterstitialAdMob!!.setFullScreenContentCallback(object :
                                                FullScreenContentCallback() {
                                            override fun onAdDismissedFullScreenContent() {
                                                // Called when fullscreen content is dismissed.
                                                Log.d("TAG", "The ad was dismissed.")
                                                prepareVpn()
                                            }

                                            fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                                // Called when fullscreen content failed to show.
                                                Log.d("TAG", "The ad failed to show.")
                                                prepareVpn()
                                            }

                                            override fun onAdShowedFullScreenContent() {
                                                // Called when fullscreen content is shown.
                                                // Make sure to set your reference to null so you don't
                                                // show it a second time.
                                                mInterstitialAdMob = null
                                                Log.d("TAG", "The ad was shown.")
                                            }
                                        })
                                    } else {
                                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                                        prepareVpn()
                                    }
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    // Handle the error
                                    Log.i("INTERSTITIAL", loadAdError.message)
                                    loadingAd = false
                                    mInterstitialAdMob = null
                                }
                            })

                }

            } else if (MainActivity.type == "fb") {
                AudienceNetworkAds.initialize(this@ContentsActivity)

                val interstitialAdListener: InterstitialAdListener =
                        object : InterstitialAdListener {
                            override fun onInterstitialDisplayed(ad: Ad) {}
                            override fun onInterstitialDismissed(ad: Ad) {
                                prepareVpn()
                            }

                            override fun onError(
                                    ad: Ad,
                                    adError: com.facebook.ads.AdError
                            ) {
                                Log.v("CHECKADS", adError.errorMessage)
                                prepareVpn()
                            }

                            override fun onAdLoaded(ad: Ad) {
                                facebookInterstitialAd!!.show()
                                Log.v("CHECKADS", "loaded")
                            }

                            override fun onAdClicked(ad: Ad) {}
                            override fun onLoggingImpression(ad: Ad) {}
                        }

                facebookInterstitialAd = InterstitialAd(
                        this@ContentsActivity,
                        MainActivity.indratech_toto_27640849_fb_interstitial_id
                )
                facebookInterstitialAd!!.loadAd(
                        facebookInterstitialAd!!.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener).build()
                )

            } else {
                prepareVpn()
            }
        } else {
            prepareVpn()
        }
    }

    protected abstract fun checkSelectedCountry()
    protected abstract fun prepareVpn()
    protected abstract fun disconnectFromVpn()
}