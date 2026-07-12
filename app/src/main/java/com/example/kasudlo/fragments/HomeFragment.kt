package com.example.kasudlo.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kasudlo.AssessmentActivity
import com.example.kasudlo.CommunityProfileActivity
import com.example.kasudlo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    private var tvGreeting: TextView?        = null
    private var tvDate: TextView?            = null
    private var tvPatientCount: TextView?    = null
    private var tvAssessmentCount: TextView? = null

    // Real-time listener registrations — removed in onDestroyView
    private val listeners = mutableListOf<ListenerRegistration>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db    = FirebaseFirestore.getInstance()
        auth  = FirebaseAuth.getInstance()
        prefs = requireActivity().getSharedPreferences("settings", 0)

        tvGreeting        = view.findViewById(R.id.tvGreeting)
        tvDate            = view.findViewById(R.id.tvDate)
        tvPatientCount    = view.findViewById(R.id.tvPatientCount)
        tvAssessmentCount = view.findViewById(R.id.tvAssessmentCount)

        tvDate?.text = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())

        loadNurseName()
        attachCountListeners()

        view.findViewById<LinearLayout>(R.id.cardNewAssessment)?.setOnClickListener {
            startActivity(Intent(requireContext(), AssessmentActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.cardViewProfiles)?.setOnClickListener {
            startActivity(Intent(requireContext(), CommunityProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (prefs.getBoolean("name_updated", false)) {
            loadNurseName()
            prefs.edit().putBoolean("name_updated", false).apply()
        }
        // No need to manually reload counts — snapshot listeners auto-update
    }

    // ── Real-time count listeners ─────────────────────────────────────────────

    private fun attachCountListeners() {
        // Clear any existing listeners first to avoid duplicates
        listeners.forEach { it.remove() }
        listeners.clear()

        // Patient count — users with role == "patient"
        // Individual Profile count
        val patientListener = db.collection("individual_profiles")
            .addSnapshotListener { snap, error ->
                if (!isAdded) return@addSnapshotListener
                if (error != null) {
                    tvPatientCount?.text = "–"
                    return@addSnapshotListener
                }
                tvPatientCount?.text = (snap?.size() ?: 0).toString()
            }

        listeners.add(patientListener)

        // Assessment count — all documents in assessments collection
        val assessmentListener = db.collection("assessments")
            .addSnapshotListener { snap, error ->
                if (!isAdded) return@addSnapshotListener
                if (error != null) {
                    tvAssessmentCount?.text = "–"
                    return@addSnapshotListener
                }
                tvAssessmentCount?.text = (snap?.size() ?: 0).toString()
            }
        listeners.add(assessmentListener)
    }

    // ── Name loading (cache-first, one-time) ──────────────────────────────────

    private fun loadNurseName() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!isAdded) return@addOnSuccessListener
                setGreeting(doc.getString("name"))
            }
            .addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                db.collection("users").document(uid).get(Source.CACHE)
                    .addOnSuccessListener { doc ->
                        if (!isAdded) return@addOnSuccessListener
                        setGreeting(doc.getString("name"))
                    }
                    .addOnFailureListener {
                        if (!isAdded) return@addOnFailureListener
                        tvGreeting?.text = "Hello, Nurse! 👋"
                    }
            }
    }

    private fun setGreeting(name: String?) {
        tvGreeting?.text = if (!name.isNullOrBlank()) "Hello, $name! 👋" else "Hello, Nurse! 👋"
    }

    // ── Cleanup ───────────────────────────────────────────────────────────────

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove Firestore listeners to prevent memory leaks
        listeners.forEach { it.remove() }
        listeners.clear()
        tvGreeting        = null
        tvDate            = null
        tvPatientCount    = null
        tvAssessmentCount = null
    }
}