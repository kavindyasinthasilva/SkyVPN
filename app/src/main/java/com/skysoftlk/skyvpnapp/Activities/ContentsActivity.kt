package com.skysoftlk.skyvpnapp.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.skysoftlk.skyvpnapp.R
import com.skysoftlk.skyvpnapp.speed.Speed
import es.dmoral.toasty.Toasty

abstract class ContentsActivity : NavigationActivity() {

    private var mLastRxBytes: Long = 0
    private var mLastTxBytes: Long = 0
    private var mLastTime: Long = 0
    private var mSpeed: Speed? = null

    var lottieAnimationView: LottieAnimationView? = null
    var vpnToastCheck = true
    var handlerTraffic: Handler? = null

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

    @JvmField
    protected var tvConnectionStatus: TextView? = null
    var ivVpnDetail: ImageView? = null
    var timerTextView: TextView? = null
    var connectBtnTextView: ImageView? = null
    var connectionStateTextView: TextView? = null
    var rcvFree: RecyclerView? = null
    var footer: RelativeLayout? = null
    lateinit var sharedPreferences :SharedPreferences

    private var timer: Handler? = Handler(Looper.getMainLooper())
    private var connectionStartTime = 0L
    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (STATUS == "CONNECTED") {
                val duration = System.currentTimeMillis() - connectionStartTime
                timerTextView!!.text = formatMilliSecondsToTime(duration)
                timer?.postDelayed(this, 1000)
            }
        }
    }

    @JvmField
    var imgFlag: ImageView? = null

    @JvmField
    var flagName: TextView? = null

    protected var STATUS: String? = "DISCONNECTED"

    fun getVpnStatus(): String? {
        return STATUS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textDownloading = findViewById(R.id.downloading)

        textUploading = findViewById(R.id.uploading)

        tvConnectionStatus = findViewById(R.id.connection_status)

        ivVpnDetail = findViewById(R.id.vpn_details)

        timerTextView = findViewById(R.id.tv_timer)

        connectBtnTextView = findViewById(R.id.connect_btn)

        connectionStateTextView = findViewById(R.id.connection_state)

        imgFlag = findViewById(R.id.flag_image)

        flagName = findViewById(R.id.flag_name)

        footer = findViewById(R.id.footer)

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

        findViewById<View>(R.id.vpn_location)?.setOnClickListener {
            showServerList()
        }

        ivVpnDetail?.setOnClickListener {
            showServerList()
        }

        flagName?.setOnClickListener {
            showServerList()
        }
    }

    protected fun addTimer() {
        connectionStartTime = System.currentTimeMillis()
        timer?.removeCallbacks(updateTimerRunnable)
        timer?.post(updateTimerRunnable)
    }

    protected fun stopTimer() {
        timer?.removeCallbacks(updateTimerRunnable)
        timerTextView!!.text = "00:00:00"
    }

    private fun formatMilliSecondsToTime(milliseconds: Long): String? {
        val seconds = (milliseconds / 1000).toInt() % 60
        val minutes = (milliseconds / (1000 * 60) % 60).toInt()
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
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
        // If we have a cached IP fetched recently (within 5 minutes), use it
        if (cachedIp != null && System.currentTimeMillis() - lastIpFetchTime < 300000) {
            tvIpAddress?.text = cachedIp
            return
        }

        // Avoid concurrent fetches
        if (isFetchingIp) return

        // China-friendly IP check services (Cloudflare and some local ones if needed)
        val services = listOf(
            "https://1.1.1.1/cdn-cgi/trace", // Cloudflare (often works)
            "https://api.ipify.org",
            "https://checkip.amazonaws.com/",
            "https://ifconfig.me/ip"
        )
        
        tryFetchIp(services, 0)
    }

    private fun tryFetchIp(services: List<String>, index: Int) {
        if (index >= services.size) {
            isFetchingIp = false
            tvIpAddress?.setText(getString(R.string.app_name))
            return
        }

        isFetchingIp = true
        val urlip = services[index]
        Log.d("IP_CHECK", "Trying $urlip")

        val stringRequest = StringRequest(Request.Method.GET, urlip, { response ->
            var ip = response?.trim() ?: ""
            
            // Special handling for Cloudflare trace output
            if (urlip.contains("1.1.1.1")) {
                val lines = ip.split("\n")
                for (line in lines) {
                    if (line.startsWith("ip=")) {
                        ip = line.substring(3)
                        break
                    }
                }
            }
            
            if (ip.isNotEmpty()) {
                Log.d("IP_CHECK", "Success from $urlip: $ip")
                cachedIp = ip
                lastIpFetchTime = System.currentTimeMillis()
                isFetchingIp = false
                tvIpAddress?.text = cachedIp
            } else {
                tryFetchIp(services, index + 1)
            }
        }) { e ->
            Log.e("IP_CHECK", "Failed $urlip: ${e.message}")
            tryFetchIp(services, index + 1)
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            8000, // Shorter timeout per service
            0,
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
        super.onDestroy()
        stopTimer()
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

    protected fun btnConnectDisconnect() {
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
        
        // Only invalidate cache and fetch on actual connection/disconnection events
        // to avoid redundant network calls during intermediate states (AUTH, WAIT, etc.)
        val isSignificantChange = (status == "CONNECTED" || status == "DISCONNECTED")
        if (isSignificantChange) {
            lastIpFetchTime = 0 
        }

        when (status) {
            "CONNECTED" -> {
                STATUS = "CONNECTED"
                textDownloading!!.visibility = View.VISIBLE
                textUploading!!.visibility = View.VISIBLE
                connectBtnTextView!!.isEnabled = true
                connectionStateTextView!!.setText(R.string.connected)
                timerTextView!!.visibility = View.VISIBLE
                hideConnectProgress()
                
                // Fetch IP only once when connected
                showIP()

                connectBtnTextView!!.visibility = View.VISIBLE
                tvConnectionStatus!!.text = "Selected"
                lottieAnimationView!!.visibility = View.GONE
                Toasty.success(this@ContentsActivity, "Server Connected", Toast.LENGTH_SHORT).show()

                addTimer()
            }
            "AUTH" -> {
                STATUS = "AUTHENTICATION"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                connectionStateTextView!!.setText(R.string.auth)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "WAIT" -> {
                STATUS = "WAITING"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                connectionStateTextView!!.setText(R.string.wait)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "RECONNECTING" -> {
                STATUS = "RECONNECTING"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                connectionStateTextView!!.setText(R.string.recon)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "LOAD" -> {
                STATUS = "LOAD"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                connectionStateTextView!!.setText(R.string.connecting)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "ASSIGN_IP" -> {
                STATUS = "LOAD"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                connectionStateTextView!!.setText(R.string.assign_ip)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "GET_CONFIG" -> {
                STATUS = "LOAD"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                connectionStateTextView!!.setText(R.string.config)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "USERPAUSE" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus!!.text = "Not Selected"

                connectBtnTextView!!.setImageResource(R.drawable.ic_on_off)
                tvConnectionStatus!!.text = "Not Selected"
                connectionStateTextView!!.setText(R.string.paused)
                stopTimer()
            }
            "NONETWORK" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus!!.text = "Not Selected"
                showIP()

                connectBtnTextView!!.setImageResource(R.drawable.ic_on_off)
                tvConnectionStatus!!.text = "Not Selected"
                connectionStateTextView!!.setText(R.string.nonetwork)
                stopTimer()
            }
            "DISCONNECTED" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus!!.text = "Not Selected"
                timerTextView!!.visibility = View.INVISIBLE
                hideConnectProgress()
                showIP()

                connectBtnTextView!!.setImageResource(R.drawable.ic_on_off)
                tvConnectionStatus!!.text = "Not Selected"
                connectionStateTextView!!.setText(R.string.disconnected)
                stopTimer()
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
            stopTimer()
        }
        builder.setNegativeButton(
                "Cancel"
        ) { _, _ ->
            showMessage("VPN Remains Connected", "success")
        }
        builder.show()
    }



    companion object {
        protected val TAG = MainActivity::class.java.simpleName

        // Static cache to share IP between activities and avoid redundant fetches
        @JvmStatic
        private var cachedIp: String? = null
        @JvmStatic
        private var lastIpFetchTime: Long = 0
        @JvmStatic
        private var isFetchingIp = false
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

        // Ensure UI updates happen on main thread to prevent crashes/ANRs
        handler.post {
            textDownloading?.text = byteinKb
            textUploading?.text = byteoutKb
        }
    }

    fun showInterstitialAndConnect() {
        prepareVpn()
    }

    protected fun showServerList() {
        startActivity(Intent(this, Servers::class.java))
    }

    protected abstract fun checkSelectedCountry()
    protected abstract fun prepareVpn()
    protected abstract fun disconnectFromVpn()
}