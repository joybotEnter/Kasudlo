package com.example.kasudlo.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kasudlo.AssessmentActivity
import com.example.kasudlo.EditAssessmentActivity
import com.example.kasudlo.R
import com.example.kasudlo.adapters.AssessmentListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source

class AssessmentListFragment : Fragment() {

    private lateinit var adapter: AssessmentListAdapter
    private val fullList     = ArrayList<HashMap<String, Any>>()
    private val filteredList = ArrayList<HashMap<String, Any>>()
    private var currentRole  = "nurse"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_assessment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabNewAssessment).setOnClickListener {
            startActivity(Intent(requireContext(), AssessmentActivity::class.java))
        }

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerAssessments)
        val etSearch = view.findViewById<EditText>(R.id.etSearchAssessment)

        adapter = AssessmentListAdapter(
            list       = filteredList,
            onEdit     = { docId ->
                val intent = Intent(requireContext(), EditAssessmentActivity::class.java)
                intent.putExtra("assessmentId", docId)
                startActivity(intent)
            },
            onDelete   = { docId, position -> confirmDelete(docId, position) },
            showDelete = currentRole == "nurse"
        )

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        fetchRoleThenLoad()
    }

    override fun onResume() {
        super.onResume()
        fetchRoleThenLoad()
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    private fun confirmDelete(docId: String, position: Int) {
        if (!isAdded) return
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Assessment")
            .setMessage("Are you sure you want to delete this assessment? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteAssessment(docId, position) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAssessment(docId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("assessments").document(docId)
            .delete()
            .addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener
                // Remove from both lists and refresh
                val removedFromFull = fullList.removeAll { it["docId"] == docId }
                if (removedFromFull) {
                    val idx = filteredList.indexOfFirst { it["docId"] == docId }
                    if (idx != -1) {
                        filteredList.removeAt(idx)
                        adapter.notifyItemRemoved(idx)
                    }
                }
                Toast.makeText(requireContext(), "Assessment deleted.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Toast.makeText(requireContext(), "Delete failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ── Data loading (cache-first) ────────────────────────────────────────────

    private fun fetchRoleThenLoad() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db  = FirebaseFirestore.getInstance()

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                currentRole = doc.getString("role") ?: "patient"
                loadAssessments(uid)
            }
            .addOnFailureListener {
                db.collection("users").document(uid).get(Source.CACHE)
                    .addOnSuccessListener { doc ->
                        currentRole = doc.getString("role") ?: "patient"
                        loadAssessments(uid)
                    }
                    .addOnFailureListener { loadAssessments(uid) }
            }
    }

    private fun loadAssessments(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val query = if (currentRole == "nurse") {
            db.collection("assessments")
        } else {
            db.collection("assessments").whereEqualTo("patientUid", uid)
        }

        query.get()
            .addOnSuccessListener { docs ->
                if (!isAdded) return@addOnSuccessListener
                populateList(docs)
            }
            .addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                query.get(Source.CACHE)
                    .addOnSuccessListener { docs ->
                        if (!isAdded) return@addOnSuccessListener
                        populateList(docs)
                        if (docs.isEmpty) {
                            Toast.makeText(
                                requireContext(),
                                "You are offline. No cached assessments available yet.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        if (!isAdded) return@addOnFailureListener
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
    }

    private fun populateList(docs: QuerySnapshot) {
        fullList.clear()
        for (doc in docs) {
            val map = doc.data.toMutableMap() as HashMap<String, Any>
            map["docId"] = doc.id
            fullList.add(map)
        }
        fullList.sortByDescending { (it["createdAt"] as? Long) ?: 0L }

        // Rebuild adapter with correct role-based delete visibility
        val recycler = view?.findViewById<RecyclerView>(R.id.recyclerAssessments)
        adapter = AssessmentListAdapter(
            list       = filteredList,
            onEdit     = { docId ->
                val intent = Intent(requireContext(), EditAssessmentActivity::class.java)
                intent.putExtra("assessmentId", docId)
                startActivity(intent)
            },
            onDelete   = { docId, position -> confirmDelete(docId, position) },
            showDelete = currentRole == "nurse"
        )
        recycler?.adapter = adapter

        filterList(view?.findViewById<EditText>(R.id.etSearchAssessment)?.text.toString())
    }

    private fun filterList(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val q = query.lowercase()
            fullList.filterTo(filteredList) {
                (it["member1Name"] as? String)?.lowercase()?.contains(q) == true ||
                        (it["informant"]   as? String)?.lowercase()?.contains(q) == true ||
                        (it["address"]     as? String)?.lowercase()?.contains(q) == true ||
                        (it["controlNo"]   as? String)?.lowercase()?.contains(q) == true
            }
        }
        adapter.notifyDataSetChanged()
    }
}