package com.example.kasudlo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.kasudlo.LoginActivity
import com.example.kasudlo.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button — goes back to previous fragment
        view.findViewById<LinearLayout>(R.id.btnBack)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // About the App — opens AboutFragment
        view.findViewById<LinearLayout>(R.id.itemAbout)?.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, AboutFragment())
                .addToBackStack(null)
                .commit()
        }


        // Settings — opens SettingsFragment
        view.findViewById<LinearLayout>(R.id.itemSettings)?.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        // Delete Account — confirm then delete from Firebase Auth
        view.findViewById<LinearLayout>(R.id.itemDeleteAccount)?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    FirebaseAuth.getInstance().currentUser?.delete()
                        ?.addOnSuccessListener {
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        ?.addOnFailureListener {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Re-login Required")
                                .setMessage("For security, please log out and log back in before deleting your account.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Logout — confirm then sign out
        view.findViewById<LinearLayout>(R.id.itemLogout)?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}