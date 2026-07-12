package com.example.kasudlo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class PatientProfileActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var uid: String
    private var patientName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        uid = FirebaseAuth.getInstance().currentUser?.uid ?: run { finish(); return }
        db  = FirebaseFirestore.getInstance()

        findViewById<LinearLayout>(R.id.btnBack)?.setOnClickListener { finish() }
        findViewById<Button>(R.id.btnSave)?.setOnClickListener { saveProfile() }

        loadNameThenProfile()
    }

    private fun loadNameThenProfile() {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                patientName = userDoc.getString("name") ?: ""
                loadProfile()
            }
            .addOnFailureListener {
                loadProfile()
            }
    }

    private fun loadProfile() {
        db.collection("individual_profiles").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // If profile already has a name, prefer it (more up-to-date)
                    val storedName = doc.getString("name") ?: ""
                    if (storedName.isNotBlank()) patientName = storedName

                    findViewById<EditText>(R.id.etAge)?.setText(doc.getString("age") ?: "")
                    findViewById<EditText>(R.id.etAddress)?.setText(doc.getString("address") ?: "")
                    findViewById<EditText>(R.id.etContact)?.setText(doc.getString("contact") ?: "")
                    findViewById<EditText>(R.id.etCondition)?.setText(doc.getString("condition") ?: "")
                }
            }
    }

    private fun saveProfile() {
        val age       = findViewById<EditText>(R.id.etAge)?.text.toString().trim()
        val address   = findViewById<EditText>(R.id.etAddress)?.text.toString().trim()
        val contact   = findViewById<EditText>(R.id.etContact)?.text.toString().trim()
        val condition = findViewById<EditText>(R.id.etCondition)?.text.toString().trim()

        val data = hashMapOf<String, Any>(
            "uid"       to uid,
            "age"       to age,
            "address"   to address,
            "contact"   to contact,
            "condition" to condition,
            "updatedAt" to System.currentTimeMillis()
        )

        // FIX: only write name if we actually have one — never overwrite with blank
        if (patientName.isNotBlank()) {
            data["name"] = patientName
        }

        findViewById<Button>(R.id.btnSave)?.isEnabled = false

        db.collection("individual_profiles").document(uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                findViewById<Button>(R.id.btnSave)?.isEnabled = true
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}