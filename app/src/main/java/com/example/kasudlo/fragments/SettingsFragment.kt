package com.example.kasudlo.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kasudlo.LoginActivity
import com.example.kasudlo.R
import com.example.kasudlo.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source

class SettingsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var prefs: SharedPreferences

    private var etDisplayName: EditText?  = null
    private var tvEmail: TextView?        = null
    private var tvAppVersion: TextView?   = null
    private var btnBack: LinearLayout?    = null
    private var btnSaveName: Button?      = null
    private var btnLogout: LinearLayout?  = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth  = FirebaseAuth.getInstance()
        db    = FirebaseFirestore.getInstance()
        prefs = requireActivity().getSharedPreferences("settings", 0)

        etDisplayName = view.findViewById(R.id.etDisplayName)
        tvEmail       = view.findViewById(R.id.tvEmail)
        tvAppVersion  = view.findViewById(R.id.tvAppVersion)
        btnBack       = view.findViewById(R.id.btnBack)
        btnSaveName   = view.findViewById(R.id.btnSaveName)
        btnLogout     = view.findViewById(R.id.btnLogout)

        btnBack?.setOnClickListener {
            requireActivity()
                .onBackPressedDispatcher
                .onBackPressed()
        }

        if (etDisplayName == null || tvEmail == null || tvAppVersion == null ||
            btnSaveName == null || btnLogout == null) return

        etDisplayName?.setHintTextColor(
            resources.getColor(R.color.textLight, requireContext().theme)
        )

        btnBack?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        tvAppVersion?.text = "Kasudlo v1.0"

        loadUserProfile()
        setupSaveName()
        setupLogout()
    }

    // ── Profile loading (cache-first) ─────────────────────────────────────────

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        tvEmail?.text = user.email ?: "No Email"

        // Restore from local prefs instantly while the network resolves
        val cachedName = prefs.getString("display_name", null)
        if (!cachedName.isNullOrBlank()) {
            etDisplayName?.setText(cachedName)
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                if (!isAdded) return@addOnSuccessListener
                val name = doc.getString("name") ?: ""
                etDisplayName?.setText(name)
                if (name.isNotBlank()) prefs.edit().putString("display_name", name).apply()
            }
            .addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                db.collection("users").document(user.uid).get(Source.CACHE)
                    .addOnSuccessListener { doc ->
                        if (!isAdded) return@addOnSuccessListener
                        val name = doc.getString("name") ?: ""
                        if (name.isNotBlank()) etDisplayName?.setText(name)
                    }
                    .addOnFailureListener {
                        if (!isAdded) return@addOnFailureListener
                        if (cachedName.isNullOrBlank()) etDisplayName?.hint = "Enter your full name"
                    }
            }
    }

    // ── Save name (works offline — Firestore queues the write) ────────────────

    private fun setupSaveName() {
        btnSaveName?.setOnClickListener {
            val newName = etDisplayName?.text.toString().trim()

            if (newName.isEmpty()) {
                etDisplayName?.error = "Please enter your name"
                return@setOnClickListener
            }

            val uid = auth.currentUser?.uid ?: run {
                Toast.makeText(requireContext(), "Not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSaveName?.isEnabled = false
            btnSaveName?.text = "Saving..."

            // Save locally immediately — available offline right away
            prefs.edit()
                .putString("display_name", newName)
                .putBoolean("name_updated", true)
                .apply()

            // Write to Firestore — queued offline, synced when connected
            db.collection("users")
                .document(uid)
                .set(mapOf("name" to newName), SetOptions.merge())
                .addOnSuccessListener {
                    if (!isAdded) return@addOnSuccessListener
                    onSaveComplete(synced = true)
                }
                .addOnFailureListener {
                    if (!isAdded) return@addOnFailureListener
                    onSaveComplete(synced = NetworkUtils.isOnline(requireContext()))
                }
        }
    }

    private fun onSaveComplete(synced: Boolean) {
        btnSaveName?.isEnabled = true
        btnSaveName?.text = "Save Name"

        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(etDisplayName?.windowToken, 0)

        val message = if (synced) "Name updated!" else "Name saved locally. Will sync when connected."
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    private fun setupLogout() {
        btnLogout?.setOnClickListener {
            showStyledDialog(
                title       = "Confirm Logout",
                message     = "Are you sure you want to log out?",
                confirmText = "Yes, Logout",
                onConfirm   = {
                    auth.signOut()
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            )
        }
    }

    private fun showStyledDialog(
        title: String,
        message: String,
        confirmText: String,
        onConfirm: () -> Unit
    ) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(confirmText) { d, _ -> d.dismiss(); onConfirm() }
            .setNegativeButton("Cancel")   { d, _ -> d.dismiss() }
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }
}