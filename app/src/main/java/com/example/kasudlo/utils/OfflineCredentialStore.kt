package com.example.kasudlo.utils

import android.content.Context
import android.util.Base64
import java.security.MessageDigest

/**
 * Stores nurse and patient credentials locally so the app can
 * authenticate without an internet connection after the first login.
 *
 * Passwords are stored as SHA-256 hashes — never plain text.
 * This is NOT meant to replace Firebase Auth; it is only a fallback
 * used when the device is offline.
 *
 * Usage:
 *  - Call saveNurseCredentials() and savePatientCredentials() right
 *    after a successful online Firebase login.
 *  - Call verifyNurse() / verifyPatient() when the device is offline.
 */
object OfflineCredentialStore {

    private const val PREFS_NAME = "kasudlo_offline_creds"

    // ── Save ─────────────────────────────────────────────────────────────────

    fun saveNurseCredentials(
        context: Context,
        studentNumber: String,
        email: String,
        password: String,
        uid: String
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("nurse_${studentNumber}_email", email)
            .putString("nurse_${studentNumber}_hash", hash(password))
            .putString("nurse_${studentNumber}_uid", uid)
            .apply()
    }

    fun savePatientCredentials(
        context: Context,
        email: String,
        password: String,
        uid: String
    ) {
        val key = emailKey(email)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("patient_${key}_hash", hash(password))
            .putString("patient_${key}_uid", uid)
            .apply()
    }

    // ── Lookup ────────────────────────────────────────────────────────────────

    fun getNurseEmail(context: Context, studentNumber: String): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("nurse_${studentNumber}_email", null)

    fun getNurseUid(context: Context, studentNumber: String): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("nurse_${studentNumber}_uid", null)

    fun getPatientUid(context: Context, email: String): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("patient_${emailKey(email)}_uid", null)

    // ── Verify ────────────────────────────────────────────────────────────────

    fun verifyNurse(context: Context, studentNumber: String, password: String): Boolean {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("nurse_${studentNumber}_hash", null) ?: return false
        return stored == hash(password)
    }

    fun verifyPatient(context: Context, email: String, password: String): Boolean {
        val key = emailKey(email)
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("patient_${key}_hash", null) ?: return false
        return stored == hash(password)
    }

    fun hasNurseCached(context: Context, studentNumber: String): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .contains("nurse_${studentNumber}_hash")

    fun hasPatientCached(context: Context, email: String): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .contains("patient_${emailKey(email)}_hash")

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /** Turns an email into a safe prefs key (no dots or @ symbols). */
    private fun emailKey(email: String): String =
        email.lowercase().replace(Regex("[^a-z0-9]"), "_")
}