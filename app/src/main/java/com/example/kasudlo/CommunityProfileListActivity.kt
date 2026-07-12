package com.example.kasudlo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommunityProfileListActivity : AppCompatActivity() {

    private val fullList = ArrayList<HashMap<String, Any>>()
    private val filteredList = ArrayList<HashMap<String, Any>>()

    private lateinit var adapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_profile_list)

        findViewById<View>(R.id.btnBack)
            .setOnClickListener {
                finish()
            }

        val recycler =
            findViewById<RecyclerView>(R.id.recyclerProfiles)

        val etSearch =
            findViewById<EditText>(R.id.etSearchProfile)

        adapter = ProfileAdapter(filteredList)

        recycler.layoutManager =
            LinearLayoutManager(this)

        recycler.adapter = adapter

        etSearch.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    filterList(s.toString())
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )

        loadProfiles()
    }

    private fun loadProfiles() {

        val uid =
            FirebaseAuth.getInstance()
                .currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("community_profiles")
            .whereEqualTo("nurseUid", uid)
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { docs ->

                fullList.clear()

                for (doc in docs) {

                    val map =
                        HashMap<String, Any>()

                    map.putAll(doc.data)

                    map["docId"] = doc.id

                    fullList.add(map)
                }

                filterList("")
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Load failed: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun filterList(query: String) {

        filteredList.clear()

        if (query.isEmpty()) {

            filteredList.addAll(fullList)

        } else {

            val q = query.lowercase()

            fullList.filterTo(filteredList) {

                (it["name"] as? String)
                    ?.lowercase()
                    ?.contains(q) == true ||

                        (it["barangay"] as? String)
                            ?.lowercase()
                            ?.contains(q) == true
            }
        }

        adapter.notifyDataSetChanged()
    }

    // =======================
    // RecyclerView Adapter
    // =======================

    inner class ProfileAdapter(
        private val list:
        List<HashMap<String, Any>>
    ) : RecyclerView.Adapter<ProfileAdapter.VH>() {

        inner class VH(v: View)
            : RecyclerView.ViewHolder(v) {

            val name: TextView =
                v.findViewById(R.id.tvProfileName)

            val age: TextView =
                v.findViewById(R.id.tvProfileAge)

            val barangay: TextView =
                v.findViewById(R.id.tvProfileBarangay)

            val health: TextView =
                v.findViewById(R.id.tvProfileHealth)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): VH {

            return VH(
                LayoutInflater.from(
                    parent.context
                ).inflate(
                    R.layout.item_community_profile,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(
            holder: VH,
            position: Int
        ) {

            val item = list[position]

            holder.name.text =
                item["name"] as? String ?: "—"

            holder.age.text =
                "Age: ${item["age"] ?: "—"} | Gender: ${item["gender"] ?: "—"}"

            holder.barangay.text =
                "📍 ${item["barangay"] ?: "—"}"

            val concerns = buildList {

                if (item["isSmoker"] == true) {
                    add("Smoker")
                }

                if (item["drinksAlcohol"] == true) {
                    add("Drinks alcohol")
                }

                if (item["hasNCD"] == true) {
                    add(
                        "NCD: ${
                            item["ncdDetails"]
                                ?: ""
                        }"
                    )
                }
            }

            holder.health.text =
                if (concerns.isEmpty()) {
                    "No health flags"
                } else {
                    "⚠️ ${concerns.joinToString(" · ")}"
                }

            // CLICK ITEM → OPEN PROFILE
            holder.itemView.setOnClickListener {

                val docId =
                    item["docId"] as? String

                if (docId == null) {

                    Toast.makeText(
                        this@CommunityProfileListActivity,
                        "Profile ID missing",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val intent = Intent(
                    this@CommunityProfileListActivity,
                    CommunityProfileDetailActivity::class.java
                )

                intent.putExtra(
                    "profileId",
                    docId
                )

                startActivity(intent)
            }
        }
    }
}