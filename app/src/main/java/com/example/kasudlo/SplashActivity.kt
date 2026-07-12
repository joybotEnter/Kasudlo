package com.example.kasudlo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ivLogo     = findViewById<ImageView>(R.id.ivLogo)
        val ivLogoText = findViewById<ImageView>(R.id.ivLogoText)
        val tvMotto    = findViewById<TextView>(R.id.tvMotto)
        val btnStart   = findViewById<Button>(R.id.btnStart)

        setupMotto()

        // Load animations
        val animLogo   = AnimationUtils.loadAnimation(this, R.anim.splash_logo)
        val animText   = AnimationUtils.loadAnimation(this, R.anim.splash_text)
        val animMotto  = AnimationUtils.loadAnimation(this, R.anim.splash_motto)
        val animButton = AnimationUtils.loadAnimation(this, R.anim.splash_button)

        // Make visible then animate
        ivLogo.visibility     = android.view.View.VISIBLE
        ivLogoText.visibility = android.view.View.VISIBLE
        tvMotto.visibility    = android.view.View.VISIBLE
        btnStart.visibility   = android.view.View.VISIBLE

        ivLogo.startAnimation(animLogo)
        ivLogoText.startAnimation(animText)
        tvMotto.startAnimation(animMotto)
        btnStart.startAnimation(animButton)

        val auth = FirebaseAuth.getInstance()

        btnStart.setOnClickListener {
            if (auth.currentUser != null) {
                val prefs = getSharedPreferences("kasudlo_prefs", Context.MODE_PRIVATE)
                val savedStudentNumber = prefs.getString("logged_student_number", null)
                val intent = if (savedStudentNumber != null) {
                    Intent(this, DashboardActivity::class.java)
                } else {
                    Intent(this, PatientDashboardActivity::class.java)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            finish()
        }
    }

    private fun setupMotto() {
        val full = "\u201cKnowledge Sharing through Active Participation and Unified Leadership for Open Communication\u201d"
        val spannable = SpannableString(full)

        val green  = ContextCompat.getColor(this, R.color.mottoGreen)
        val dark   = ContextCompat.getColor(this, R.color.textDark)
        val blue   = ContextCompat.getColor(this, R.color.mottoBlue)
        val orange = ContextCompat.getColor(this, R.color.mottoOrange)
        val red    = ContextCompat.getColor(this, R.color.mottoRed)

        spannable.setSpan(ForegroundColorSpan(green),  1,  18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(dark),   19, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(blue),   27, 47, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(dark),   48, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(orange), 52, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(dark),   71, 74, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(red),    75, 93, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        findViewById<TextView>(R.id.tvMotto).text = spannable
    }
}