package com.example.kasudlo

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kasudlo.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var passwordVisible = false
    private var confirmVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnTogglePassword.setOnClickListener {
            passwordVisible = !passwordVisible
            binding.etPassword.transformationMethod = if (passwordVisible)
                HideReturnsTransformationMethod.getInstance()
            else
                PasswordTransformationMethod.getInstance()
            binding.btnTogglePassword.setImageResource(
                if (passwordVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
            )
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.btnToggleConfirm.setOnClickListener {
            confirmVisible = !confirmVisible
            binding.etConfirmPassword.transformationMethod = if (confirmVisible)
                HideReturnsTransformationMethod.getInstance()
            else
                PasswordTransformationMethod.getInstance()
            binding.btnToggleConfirm.setImageResource(
                if (confirmVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
            )
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text.length)
        }

        binding.btnRegister.setOnClickListener { registerPatient() }

        // Goes back to LoginActivity
        binding.tvLogin.setOnClickListener { finish() }
    }

    private fun registerPatient() {
        val name     = binding.etUsername.text.toString().trim()
        val email    = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm  = binding.etConfirmPassword.text.toString()

        // ── Validation ───────────────────────────────────────────────
        if (name.isEmpty()) {
            binding.etUsername.error = "Full name is required"
            binding.etUsername.requestFocus()
            return
        }
        if (name.length < 2) {
            binding.etUsername.error = "Enter your full name"
            binding.etUsername.requestFocus()
            return
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Enter a valid email"
            binding.etEmail.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.etPassword.error = "Min. 6 characters"
            binding.etPassword.requestFocus()
            return
        }
        if (confirm != password) {
            binding.etConfirmPassword.error = "Passwords do not match"
            binding.etConfirmPassword.requestFocus()
            return
        }

        setLoading(true)

        // ── Step 1: Create Firebase Auth account ─────────────────────
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user!!.uid

                // ── Step 2: Save to users collection ─────────────────
                val userDoc = hashMapOf(
                    "uid"       to uid,
                    "name"      to name,
                    "email"     to email,
                    "role"      to "patient",   // always patient from this screen
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("users").document(uid).set(userDoc)
                    .addOnSuccessListener {

                        // ── Step 3: Create individual_profiles stub so nurse list shows the patient immediately ──
                        val profileDoc = hashMapOf(
                            "uid"       to uid,
                            "name"      to name,      // visible in nurse's Individual Profile list right away
                            "age"       to "",
                            "address"   to "",
                            "contact"   to "",
                            "condition" to "",
                            "updatedAt" to System.currentTimeMillis()
                        )

                        db.collection("individual_profiles").document(uid).set(profileDoc)
                            .addOnSuccessListener {
                                // ── Step 4: Save to patients collection ───────
                                val patientDoc = hashMapOf(
                                    "uid"               to uid,
                                    "name"              to name,
                                    "email"             to email,
                                    "linkedHouseholdId" to "",
                                    "createdAt"         to System.currentTimeMillis()
                                )
                                db.collection("patients").document(uid).set(patientDoc)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Welcome, $name! Your account is ready.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this, PatientDashboardActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        // patients doc failed but profile doc saved — still let them in
                                        Toast.makeText(
                                            this,
                                            "Account created. Some profile data may be missing.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        startActivity(Intent(this, PatientDashboardActivity::class.java))
                                        finish()
                                    }
                            }
                            .addOnFailureListener {
                                // individual_profiles failed — still proceed
                                Toast.makeText(
                                    this,
                                    "Account created. Some profile data may be missing.",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this, PatientDashboardActivity::class.java))
                                finish()
                            }
                    }
                    .addOnFailureListener { e ->
                        // Firestore save failed — delete the Auth account
                        // so it doesn't become an orphan with no Firestore doc
                        result.user?.delete()
                        setLoading(false)
                        Toast.makeText(
                            this,
                            "Registration failed. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                // Firebase Auth error — most common: email already in use
                val msg = when {
                    e.message?.contains("email address is already") == true ->
                        "This email is already registered. Try logging in."
                    e.message?.contains("badly formatted") == true ->
                        "Invalid email format."
                    else -> e.message ?: "Registration failed. Try again."
                }
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnRegister.isEnabled = !loading
        binding.btnRegister.text = if (loading) "Creating account..." else "Create Patient Account"
    }
}