package com.example.kasudlo.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kasudlo.CommunityProfileActivity
import com.example.kasudlo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source

class IndividualProfileListFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var tvEmpty: LinearLayout

    private val fullList     = ArrayList<HashMap<String, Any>>()
    private val filteredList = ArrayList<HashMap<String, Any>>()
    private lateinit var adapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_individual_profile_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerProfiles)
        etSearch = view.findViewById(R.id.etSearchProfile)
        tvEmpty  = view.findViewById(R.id.tvEmpty)

        adapter = ProfileAdapter(
            list     = filteredList,
            onEdit   = { docId ->
                val intent = Intent(requireContext(), CommunityProfileActivity::class.java)
                intent.putExtra("patientUid", docId)
                startActivity(intent)
            },
            onDelete = { docId, position -> confirmDelete(docId, position) }
        )

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.fabNewProfile).setOnClickListener {
            startActivity(Intent(requireContext(), CommunityProfileActivity::class.java))
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        loadProfiles()
    }

    override fun onResume() {
        super.onResume()
        loadProfiles()
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    private fun confirmDelete(docId: String, position: Int) {
        if (!isAdded) return
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Profile")
            .setMessage("Are you sure you want to delete this profile? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteProfile(docId, position) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProfile(docId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("individual_profiles").document(docId)
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
                tvEmpty.visibility  = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
                recycler.visibility = if (filteredList.isEmpty()) View.GONE   else View.VISIBLE
                Toast.makeText(requireContext(), "Profile deleted.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                Toast.makeText(requireContext(), "Delete failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ── Data loading (cache-first) ────────────────────────────────────────────

    private fun loadProfiles() {
        if (!isAdded) return
        val query = FirebaseFirestore.getInstance().collection("individual_profiles")

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
                                "You are offline. No cached profiles available yet.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        if (!isAdded) return@addOnFailureListener
                        Toast.makeText(requireContext(), "Load failed: ${e.message}", Toast.LENGTH_LONG).show()
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
        fullList.sortByDescending { (it["updatedAt"] as? Long) ?: 0L }
        filterList(etSearch.text.toString())
    }

    private fun filterList(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val q = query.lowercase()
            fullList.filterTo(filteredList) {
                (it["name"]    as? String)?.lowercase()?.contains(q) == true ||
                        (it["address"] as? String)?.lowercase()?.contains(q) == true ||
                        (it["contact"] as? String)?.lowercase()?.contains(q) == true
            }
        }
        adapter.notifyDataSetChanged()
        tvEmpty.visibility  = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
        recycler.visibility = if (filteredList.isEmpty()) View.GONE   else View.VISIBLE
    }

    // ── Adapter ───────────────────────────────────────────────────────────────

    inner class ProfileAdapter(
        private val list: ArrayList<HashMap<String, Any>>,
        private val onEdit: (String) -> Unit,
        private val onDelete: (String, Int) -> Unit
    ) : RecyclerView.Adapter<ProfileAdapter.VH>() {

        inner class VH(val root: View) : RecyclerView.ViewHolder(root) {
            val tvName:     TextView = root.findViewById(R.id.tvProfileName)
            val tvAddress:  TextView = root.findViewById(R.id.tvProfileAddress)
            val tvAge:      TextView = root.findViewById(R.id.tvProfileAge)
            val tvContact:  TextView = root.findViewById(R.id.tvProfileContact)
            val btnEdit:    View     = root.findViewById(R.id.btnEditProfile)
            val btnDelete:  View     = root.findViewById(R.id.btnDeleteProfile)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_individual_profile, parent, false)
            return VH(v)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item  = list[position]
            val docId = item["docId"] as? String ?: ""

            holder.tvName.text    = (item["name"]    as? String)?.ifBlank { "Unnamed Patient" } ?: "Unnamed Patient"
            holder.tvAddress.text = (item["address"] as? String)?.ifBlank { "No address on file" } ?: "No address on file"
            holder.tvAge.text     = "Age: ${item["age"] as? String ?: "—"}"
            holder.tvContact.text = "📞 ${item["contact"] as? String ?: "—"}"

            holder.root.setOnClickListener     { onEdit(docId) }
            holder.btnEdit.setOnClickListener  { onEdit(docId) }
            holder.btnDelete.setOnClickListener {
                onDelete(docId, holder.adapterPosition)
            }
        }
    }
}