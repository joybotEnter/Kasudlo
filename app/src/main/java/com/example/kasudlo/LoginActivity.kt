package com.example.kasudlo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kasudlo.databinding.ActivityLoginBinding
import com.example.kasudlo.utils.NetworkUtils
import com.example.kasudlo.utils.OfflineCredentialStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isNurseTab = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db   = FirebaseFirestore.getInstance()

        setupMotto()

        // Already logged in — route directly without network call
        if (auth.currentUser != null) {
            val prefs = getSharedPreferences("kasudlo_prefs", Context.MODE_PRIVATE)
            val savedStudentNumber = prefs.getString("logged_student_number", null)
            if (savedStudentNumber != null) {
                navigate("nurse")
            } else {
                routeByRole(auth.currentUser!!.uid)
            }
            return
        }

        binding.btnTabNurse.setOnClickListener { switchTab(nurse = true) }
        binding.btnTabPatient.setOnClickListener { switchTab(nurse = false) }

        binding.btnLogin.setOnClickListener {
            if (isNurseTab) loginNurse() else loginPatient()
        }

        binding.tvRegister.setOnClickListener {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(
                    this,
                    "Registration requires an internet connection. Please connect and try again.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // ── Nurse Login ───────────────────────────────────────────────────────────

    private fun loginNurse() {
        val studentNum = binding.etStudentNumber.text.toString().trim()
        val password   = binding.etPassword.text.toString()

        if (studentNum.isEmpty()) {
            binding.etStudentNumber.error = "Enter your student number"
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Enter your password"
            return
        }

        setLoading(true)

        // ── Offline path ──────────────────────────────────────────────────────
        if (!NetworkUtils.isOnline(this)) {
            if (!OfflineCredentialStore.hasNurseCached(this, studentNum)) {
                setLoading(false)
                Toast.makeText(
                    this,
                    "No offline credentials found for this account. " +
                            "Please log in once while connected to the internet.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            if (OfflineCredentialStore.verifyNurse(this, studentNum, password)) {
                getSharedPreferences("kasudlo_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("logged_student_number", studentNum)
                    .apply()
                navigate("nurse")
            } else {
                setLoading(false)
                Toast.makeText(this, "Incorrect password.", Toast.LENGTH_LONG).show()
            }
            return
        }

        // ── Online path ───────────────────────────────────────────────────────
        db.collection("users")
            .whereEqualTo("studentNumber", studentNum)
            .whereEqualTo("role", "nurse")
            .limit(1)
            .get()
            .addOnSuccessListener { docs ->
                if (docs.isEmpty) {
                    setLoading(false)
                    binding.etStudentNumber.error = "Student number not found"
                    return@addOnSuccessListener
                }

                val nurseDoc  = docs.documents[0]
                val realEmail = nurseDoc.getString("email") ?: ""

                if (realEmail.isEmpty()) {
                    setLoading(false)
                    Toast.makeText(this, "Account setup incomplete. Contact admin.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                auth.signInWithEmailAndPassword(realEmail, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user!!.uid

                        // Cache credentials for future offline logins
                        OfflineCredentialStore.saveNurseCredentials(
                            this, studentNum, realEmail, password, uid
                        )

                        getSharedPreferences("kasudlo_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putString("logged_student_number", studentNum)
                            .apply()

                        navigate("nurse")
                    }
                    .addOnFailureListener {
                        setLoading(false)
                        Toast.makeText(this, "Incorrect password. Try again.", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, "Network error. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    // ── Patient Login ─────────────────────────────────────────────────────────

    private fun loginPatient() {
        val email    = binding.etPatientEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etPatientEmail.error = "Enter a valid email"
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Enter your password"
            return
        }

        setLoading(true)

        // ── Offline path ──────────────────────────────────────────────────────
        if (!NetworkUtils.isOnline(this)) {
            if (!OfflineCredentialStore.hasPatientCached(this, email)) {
                setLoading(false)
                Toast.makeText(
                    this,
                    "No offline credentials found for this account. " +
                            "Please log in once while connected to the internet.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            if (OfflineCredentialStore.verifyPatient(this, email, password)) {
                navigate("patient")
            } else {
                setLoading(false)
                Toast.makeText(this, "Incorrect email or password.", Toast.LENGTH_LONG).show()
            }
            return
        }

        // ── Online path ───────────────────────────────────────────────────────
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user!!.uid
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        val role = doc.getString("role") ?: ""
                        when (role) {
                            "patient" -> {
                                // Cache credentials for future offline logins
                                OfflineCredentialStore.savePatientCredentials(
                                    this, email, password, uid
                                )
                                navigate("patient")
                            }
                            "nurse" -> {
                                auth.signOut()
                                setLoading(false)
                                Toast.makeText(this, "This is a nurse account. Please use the Nurse tab.", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                auth.signOut()
                                setLoading(false)
                                Toast.makeText(this, "Unknown account type. Contact admin.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .addOnFailureListener {
                        // Try Firestore cache before giving up
                        db.collection("users").document(uid).get(Source.CACHE)
                            .addOnSuccessListener { doc ->
                                val role = doc.getString("role") ?: ""
                                if (role == "patient") {
                                    OfflineCredentialStore.savePatientCredentials(
                                        this, email, password, uid
                                    )
                                    navigate("patient")
                                } else {
                                    auth.signOut()
                                    setLoading(false)
                                    Toast.makeText(this, "Error verifying account. Try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                auth.signOut()
                                setLoading(false)
                                Toast.makeText(this, "Error verifying account. Try again.", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, "Incorrect email or password.", Toast.LENGTH_LONG).show()
            }
    }

    // ── Role routing (for already-authenticated users) ────────────────────────

    private fun routeByRole(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc -> applyRole(doc) }
            .addOnFailureListener {
                // Fall back to Firestore cache
                db.collection("users").document(uid).get(Source.CACHE)
                    .addOnSuccessListener { doc -> applyRole(doc) }
                    .addOnFailureListener {
                        auth.signOut()
                        Toast.makeText(this, "Could not verify session. Please log in again.", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun applyRole(doc: com.google.firebase.firestore.DocumentSnapshot) {
        val role = doc.getString("role") ?: ""
        when (role) {
            "nurse" -> {
                val studentNumber = doc.getString("studentNumber") ?: ""
                getSharedPreferences("kasudlo_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("logged_student_number", studentNumber)
                    .apply()
                navigate("nurse")
            }
            "patient" -> navigate("patient")
            else -> auth.signOut()
        }
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    private fun navigate(role: String) {
        val intent = if (role == "nurse") {
            Intent(this, DashboardActivity::class.java)
        } else {
            Intent(this, PatientDashboardActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoading(loading: Boolean) {
        binding.btnLogin.isEnabled      = !loading
        binding.btnTabNurse.isEnabled   = !loading
        binding.btnTabPatient.isEnabled = !loading
        binding.btnLogin.text = when {
            loading    -> "Logging in..."
            isNurseTab -> "Log In as Nurse"
            else       -> "Log In as Patient"
        }
    }

    private fun switchTab(nurse: Boolean) {
        isNurseTab = nurse
        binding.layoutNurse.visibility        = if (nurse) View.VISIBLE else View.GONE
        binding.layoutPatient.visibility      = if (nurse) View.GONE    else View.VISIBLE
        binding.layoutRegisterLink.visibility = if (nurse) View.GONE    else View.VISIBLE
        binding.btnLogin.text = if (nurse) "Log In as Nurse" else "Log In as Patient"

        binding.btnTabNurse.setTextColor(
            ContextCompat.getColor(this, if (nurse) R.color.mottoBlue else R.color.textDark)
        )
        binding.btnTabPatient.setTextColor(
            ContextCompat.getColor(this, if (nurse) R.color.textDark else R.color.mottoGreen)
        )
        binding.btnTabNurse.setBackgroundResource(
            if (nurse) R.drawable.tab_active_bg else android.R.color.transparent
        )
        binding.btnTabPatient.setBackgroundResource(
            if (nurse) android.R.color.transparent else R.drawable.tab_active_bg
        )
        binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(
            this, if (nurse) R.color.mottoBlue else R.color.mottoGreen
        )
        binding.btnTabNurse.elevation   = if (nurse) 4f else 0f
        binding.btnTabPatient.elevation = if (nurse) 0f else 4f

        binding.etStudentNumber.text?.clear()
        binding.etPatientEmail.text?.clear()
        binding.etPassword.text?.clear()
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