package com.example.kasudlo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kasudlo.adapters.AssessmentListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientAssessmentsActivity : AppCompatActivity() {

    private lateinit var adapter: AssessmentListAdapter
    private val list = ArrayList<HashMap<String, Any>>()
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_assessment)

        uid = FirebaseAuth.getInstance().currentUser?.uid ?: run { finish(); return }

        findViewById<LinearLayout>(R.id.btnBack)?.setOnClickListener { finish() }

        findViewById<FloatingActionButton>(R.id.fabNewAssessment)?.setOnClickListener {
            val intent = Intent(this, AssessmentActivity::class.java)
            intent.putExtra("patientUid", uid)
            startActivity(intent)
        }

        val recycler = findViewById<RecyclerView>(R.id.recyclerAssessments)

        // Tap opens read-only preview
        adapter = AssessmentListAdapter(list, onEdit = { docId ->
            val intent = Intent(this, PatientAssessmentPreviewActivity::class.java)
            intent.putExtra("assessmentId", docId)
            startActivity(intent)
        }, showEdit = false)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        loadAssessments()
    }

    override fun onResume() {
        super.onResume()
        loadAssessments()
    }

    private fun loadAssessments() {
        FirebaseFirestore.getInstance()
            .collection("assessments")
            .whereEqualTo("patientUid", uid)
            .get()
            .addOnSuccessListener { docs ->
                list.clear()
                for (doc in docs) {
                    val map = doc.data.toMutableMap() as HashMap<String, Any>
                    map["docId"] = doc.id
                    list.add(map)
                }
                list.sortByDescending { (it["createdAt"] as? Long) ?: 0L }
                adapter.notifyDataSetChanged()
                val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
                tvEmpty?.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}