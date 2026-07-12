package com.example.kasudlo

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientProfilePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile_preview)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run { finish(); return }

        findViewById<LinearLayout>(R.id.btnBack)?.setOnClickListener { finish() }

        val db = FirebaseFirestore.getInstance()

        // Load profile first, then resolve name with fallback to users collection
        db.collection("individual_profiles").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    findViewById<TextView>(R.id.tvNoData)?.visibility = View.VISIBLE
                    // Still try to show the name from users
                    loadNameFallback(db, uid, profileName = null)
                    return@addOnSuccessListener
                }
                val d = doc.data ?: return@addOnSuccessListener

                val profileName = d["name"] as? String
                loadNameFallback(db, uid, profileName)

                setField(R.id.tvAge,        d["age"]        as? String)
                setField(R.id.tvBirthday,   d["birthday"]   as? String)
                setField(R.id.tvSex,        d["sex"]        as? String)
                setField(R.id.tvAddress,    d["address"]    as? String)
                setField(R.id.tvContact,    d["contact"]    as? String)
                setField(R.id.tvEmail,      d["email"]      as? String)
                setField(R.id.tvPhilhealth, d["philhealth"] as? String)
                setField(R.id.tvBloodType,  d["bloodType"]  as? String)
                setField(R.id.tvMemberType, d["memberType"] as? String)
                setField(R.id.tvFacility,   d["facility"]   as? String)
                setField(R.id.tvSmoking,    d["smoking"]    as? String)
                setField(R.id.tvAlcohol,    d["alcohol"]    as? String)
                setField(R.id.tvHighestBP,  d["highestBP"]  as? String)
                setField(R.id.tvAllergy,    buildYesNo(d["allergy"] as? Boolean, d["allergySpecify"] as? String))
                setField(R.id.tvDiabetes,   buildYesNo(d["diabetes"] as? Boolean, null))
                setField(R.id.tvAsthma,     buildYesNo(d["asthma"]   as? Boolean, null))
            }
    }

    /**
     * Sets tvName using profileName if non-blank,
     * otherwise falls back to the name stored in users/{uid}.
     */
    private fun loadNameFallback(db: FirebaseFirestore, uid: String, profileName: String?) {
        if (!profileName.isNullOrBlank()) {
            setField(R.id.tvName, profileName)
            return
        }
        // Fallback: read from users collection
        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                setField(R.id.tvName, userDoc.getString("name"))
            }
    }

    private fun setField(viewId: Int, value: String?) {
        findViewById<TextView>(viewId)?.text = value?.ifBlank { "—" } ?: "—"
    }

    private fun buildYesNo(flag: Boolean?, specify: String?): String {
        if (flag == null) return "—"
        return if (flag) "Yes${if (!specify.isNullOrBlank()) " ($specify)" else ""}" else "No"
    }
}