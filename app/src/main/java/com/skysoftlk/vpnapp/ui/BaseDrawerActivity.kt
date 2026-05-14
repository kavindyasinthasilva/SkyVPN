package com.skysoftlk.vpnapp.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import androidx.drawerlayout.widget.DrawerLayout
import com.skysoftlk.vpnapp.Activities.MainActivity
import com.skysoftlk.vpnapp.R
import com.skysoftlk.vpnapp.Utils.ChinaUtils

abstract class BaseDrawerActivity : AppCompatActivity() {
    private lateinit var manager :ReviewManager
    protected var toolbar: Toolbar? = null
        private set

    @get:LayoutRes
    protected abstract val layoutRes: Int

    private var mDrawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes) // set content

        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val category = findViewById<ImageView?>(R.id.category)
        category?.setOnClickListener {
            mDrawerLayout?.openDrawer(GravityCompat.START, true)
        }

        manager = ReviewManagerFactory.create(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupNavView()
    }

    override fun onBackPressed() {
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupNavView() {
        if (navigationView == null) {
            return
        }

        navigationView?.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            val id = menuItem.itemId
            Handler().postDelayed({ handleDrawerClick(id) }, 300)
            mDrawerLayout?.closeDrawers()
            true
        }

    }

    private fun handleDrawerClick(menuId: Int) {
        when (menuId) {
            R.id.nav_home -> {
                if (this !is MainActivity) {
                    val intent = Intent(this, com.skysoftlk.vpnapp.Activities.MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }
            }
            R.id.nav_servers -> {
                startActivity(Intent(this, com.skysoftlk.vpnapp.Activities.Servers::class.java))
            }
            R.id.nav_premium -> {
                startActivity(Intent(this, com.skysoftlk.vpnapp.Activities.UnlockAllActivity::class.java))
            }
            R.id.nav_helpus -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:support@indratech.in")
                intent.putExtra(Intent.EXTRA_EMAIL, (R.string.support_email))
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.support_email_subject)
                intent.putExtra(Intent.EXTRA_TEXT, R.string.support_email_extra_text)
                try {
                    startActivity(Intent.createChooser(intent, "send mail"))
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(this, "No mail app found!!!", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Toast.makeText(this, "Unexpected Error!!!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_rate -> {
                if (ChinaUtils.isLikelyInChina(this)) {
                    // Open a generic help page or local store if in China
                    Toast.makeText(this, "Rating not available in this region", Toast.LENGTH_SHORT).show()
                } else {
                    startReviewFlow()
                }
            }
            R.id.nav_share -> {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app")
                    val shareText = if (ChinaUtils.isLikelyInChina(this)) {
                        "Try this VPN App! Contact support for more info."
                    } else {
                        "I'm using this Free VPN App, it's provide all servers free https://play.google.com/store/apps/details?id=" + this.packageName
                    }
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                    startActivity(Intent.createChooser(shareIntent, "choose one"))
                } catch (e: Exception) {
                }
            }
            R.id.nav_policy -> {
                val uri = Uri.parse(resources.getString(R.string.privacy_policy_link)) // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        mDrawerLayout?.closeDrawer(GravityCompat.START)
    }


    private fun startReviewFlow() {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(this, reviewInfo)
            } else {
                rateUs()
            }
        }
    }

    override fun getSystemService(name: String): Any? {
        if (Context.ACCESSIBILITY_SERVICE == name) {
            return try {
                super.getSystemService(name)
            } catch (t: Throwable) {
                Log.e("BaseDrawerActivity", "Failed to get AccessibilityManagerService", t)
                null
            }
        }
        return super.getSystemService(name)
    }

    private fun rateUs() {
        val uri = Uri.parse("market://details?id=" + this.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flag to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + this.packageName)))
        }
    }

}