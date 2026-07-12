package com.example.kasudlo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kasudlo.AssessmentActivity
import com.example.kasudlo.CommunityProfileActivity
import com.example.kasudlo.PatientAssessmentsActivity
import com.example.kasudlo.PatientProfilePreviewActivity
import com.example.kasudlo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientHomeFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_patient_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db   = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val sdf = java.text.SimpleDateFormat("EEE, MMM d", java.util.Locale.getDefault())
        view.findViewById<TextView>(R.id.tvDate)?.text = sdf.format(java.util.Date())

        loadName(view)
        loadCounts(view)

        // Folder cards — read-only previews
        view.findViewById<LinearLayout>(R.id.cardProfile)?.setOnClickListener {
            startActivity(Intent(requireContext(), PatientProfilePreviewActivity::class.java))
        }
        view.findViewById<LinearLayout>(R.id.cardAssessments)?.setOnClickListener {
            startActivity(Intent(requireContext(), PatientAssessmentsActivity::class.java))
        }

        // Quick actions — open editable forms
        view.findViewById<LinearLayout>(R.id.quickAssessments)?.setOnClickListener {
            val intent = Intent(requireContext(), AssessmentActivity::class.java)
            intent.putExtra("patientUid", auth.currentUser?.uid ?: "")
            startActivity(intent)
        }
        view.findViewById<LinearLayout>(R.id.quickProfile)?.setOnClickListener {
            startActivity(Intent(requireContext(), CommunityProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        view?.let { loadCounts(it) }
    }

    private fun loadName(view: View) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!isAdded) return@addOnSuccessListener
                val name = doc.getString("name") ?: "Patient"
                val firstName = name.split(" ").firstOrNull() ?: name
                view.findViewById<TextView>(R.id.tvGreeting)?.text = "Hello, $firstName! 👋"
            }
    }

    private fun loadCounts(view: View) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("assessments")
            .whereEqualTo("patientUid", uid)
            .get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                view.findViewById<TextView>(R.id.tvAssessmentCount)?.text =
                    result.size().toString()
            }
    }
}