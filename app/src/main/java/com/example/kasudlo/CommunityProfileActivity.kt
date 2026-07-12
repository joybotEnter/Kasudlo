package com.example.kasudlo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.kasudlo.databinding.ActivityCommunityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // The Firestore document id for individual_profiles.
    // • Edit mode  → passed via intent extra "patientUid"
    // • New mode   → a brand-new auto-generated id (never the nurse's uid)
    private lateinit var targetUid: String

    // True when we are creating a brand-new profile (FAB was tapped).
    // False when editing an existing one (list item was tapped).
    private var isNewProfile: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db   = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val passed = intent.getStringExtra("patientUid")

        if (passed != null) {
            // ── Edit mode: nurse tapped an existing profile in the list ───────
            targetUid    = passed
            isNewProfile = false
            loadProfile()
        } else {
            // ── New mode: nurse tapped the FAB ────────────────────────────────
            // Generate a fresh document id — do NOT use the nurse's own uid.
            targetUid    = db.collection("individual_profiles").document().id
            isNewProfile = true
            // Nothing to load; the form starts blank.
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { saveProfile() }
    }

    // ── Load existing profile ─────────────────────────────────────────────────

    private fun loadProfile() {
        db.collection("individual_profiles").document(targetUid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener
                val d = doc.data ?: return@addOnSuccessListener
                applyProfileData(d)
            }
            .addOnFailureListener {
                // Offline — try Firestore local cache
                db.collection("individual_profiles").document(targetUid)
                    .get(com.google.firebase.firestore.Source.CACHE)
                    .addOnSuccessListener { doc ->
                        if (!doc.exists()) return@addOnSuccessListener
                        val d = doc.data ?: return@addOnSuccessListener
                        applyProfileData(d)
                        Toast.makeText(this, "Offline — showing cached profile.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Could not load profile (no cache available).", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun applyProfileData(d: Map<String, Any>) {
        // Personal Info
        binding.etName.setText(d["name"] as? String ?: "")
        binding.etAge.setText(d["age"] as? String ?: "")
        binding.etBirthday.setText(d["birthday"] as? String ?: "")
        binding.etAddress.setText(d["address"] as? String ?: "")
        binding.etContact.setText(d["contact"] as? String ?: "")
        binding.etEmail.setText(d["email"] as? String ?: "")
        binding.etPhilhealth.setText(d["philhealth"] as? String ?: "")
        binding.etDateRegistration.setText(d["dateRegistration"] as? String ?: "")
        binding.etMemberType.setText(d["memberType"] as? String ?: "")
        binding.etFacility.setText(d["facility"] as? String ?: "")
        binding.etFacilityAddress.setText(d["facilityAddress"] as? String ?: "")
        binding.etSignature.setText(d["signature"] as? String ?: "")

        when (d["sex"] as? String) {
            "Male"   -> binding.rbMale.isChecked = true
            "Female" -> binding.rbFemale.isChecked = true
        }

        // Social History
        when (d["smoking"] as? String) {
            "Yes"  -> binding.rbSmokingYes.isChecked = true
            "No"   -> binding.rbSmokingNo.isChecked = true
            "Quit" -> binding.rbSmokingQuit.isChecked = true
        }
        binding.etPackYear.setText(d["packYear"] as? String ?: "")
        when (d["alcohol"] as? String) {
            "Yes"  -> binding.rbAlcoholYes.isChecked = true
            "No"   -> binding.rbAlcoholNo.isChecked = true
            "Quit" -> binding.rbAlcoholQuit.isChecked = true
        }
        binding.etBottleYear.setText(d["bottleYear"] as? String ?: "")
        when (d["drugs"] as? String) {
            "Yes"  -> binding.rbDrugsYes.isChecked = true
            "No"   -> binding.rbDrugsNo.isChecked = true
            "Quit" -> binding.rbDrugsQuit.isChecked = true
        }
        when (d["sexuallyActive"] as? String) {
            "Yes" -> binding.rbSexYes.isChecked = true
            "No"  -> binding.rbSexNo.isChecked = true
        }

        // Immunization
        binding.cbBCG.isChecked          = d["bcg"] as? Boolean ?: false
        binding.cbOPV1.isChecked         = d["opv1"] as? Boolean ?: false
        binding.cbOPV2.isChecked         = d["opv2"] as? Boolean ?: false
        binding.cbOPV3.isChecked         = d["opv3"] as? Boolean ?: false
        binding.cbDPT1.isChecked         = d["dpt1"] as? Boolean ?: false
        binding.cbDPT2.isChecked         = d["dpt2"] as? Boolean ?: false
        binding.cbDPT3.isChecked         = d["dpt3"] as? Boolean ?: false
        binding.cbMeasles.isChecked      = d["measles"] as? Boolean ?: false
        binding.cbHepa2.isChecked        = d["hepa2"] as? Boolean ?: false
        binding.cbHepa3.isChecked        = d["hepa3"] as? Boolean ?: false
        binding.cbVaricella.isChecked    = d["varicella"] as? Boolean ?: false
        binding.cbNoneChild.isChecked    = d["noneChild"] as? Boolean ?: false
        binding.cbHPV.isChecked          = d["hpv"] as? Boolean ?: false
        binding.cbMMR.isChecked          = d["mmr"] as? Boolean ?: false
        binding.cbAdultNone.isChecked    = d["adultNone"] as? Boolean ?: false
        binding.cbPneumococcal.isChecked = d["pneumococcal"] as? Boolean ?: false
        binding.cbFlu.isChecked          = d["flu"] as? Boolean ?: false
        binding.etImmunizationOthers.setText(d["immunizationOthers"] as? String ?: "")

        // Family Planning
        binding.cbFamilyPlanning.isChecked = d["familyPlanning"] as? Boolean ?: false

        // Past Medical History
        binding.cbAllergy.isChecked         = d["allergy"] as? Boolean ?: false
        binding.etAllergySpecify.setText(d["allergySpecify"] as? String ?: "")
        binding.cbAsthma.isChecked          = d["asthma"] as? Boolean ?: false
        binding.cbCancer.isChecked          = d["cancer"] as? Boolean ?: false
        binding.etCancerSpecify.setText(d["cancerSpecify"] as? String ?: "")
        binding.cbCerebrovascular.isChecked = d["cerebrovascular"] as? Boolean ?: false
        binding.cbCoronary.isChecked        = d["coronary"] as? Boolean ?: false
        binding.cbDiabetes.isChecked        = d["diabetes"] as? Boolean ?: false
        binding.cbEmphysema.isChecked       = d["emphysema"] as? Boolean ?: false
        binding.cbEpilepsy.isChecked        = d["epilepsy"] as? Boolean ?: false
        binding.cbHepatitis.isChecked       = d["hepatitis"] as? Boolean ?: false
        binding.etHepatitisSpecify.setText(d["hepatitisSpecify"] as? String ?: "")
        binding.cbHyperlipidemia.isChecked  = d["hyperlipidemia"] as? Boolean ?: false
        binding.cbHypertension.isChecked    = d["hypertension"] as? Boolean ?: false
        binding.etHighestBP.setText(d["highestBP"] as? String ?: "")
        binding.cbPeptic.isChecked          = d["peptic"] as? Boolean ?: false
        binding.cbPneumonia.isChecked       = d["pneumonia"] as? Boolean ?: false
        binding.cbThyroid.isChecked         = d["thyroid"] as? Boolean ?: false
        binding.cbPTB.isChecked             = d["ptb"] as? Boolean ?: false
        binding.etPTBSpecify.setText(d["ptbSpecify"] as? String ?: "")
        binding.cbUTI.isChecked             = d["uti"] as? Boolean ?: false
        binding.cbMental.isChecked          = d["mental"] as? Boolean ?: false
        binding.etPastOthers.setText(d["pastOthers"] as? String ?: "")
        binding.etPastSurgicalHistory.setText(d["pastSurgical"] as? String ?: "")
        binding.etDateDone.setText(d["pastSurgicalDate"] as? String ?: "")

        // Family History
        binding.cbFamAllergy.isChecked         = d["famAllergy"] as? Boolean ?: false
        binding.etFamAllergySpecify.setText(d["famAllergySpecify"] as? String ?: "")
        binding.cbFamAsthma.isChecked          = d["famAsthma"] as? Boolean ?: false
        binding.cbFamCancer.isChecked          = d["famCancer"] as? Boolean ?: false
        binding.etFamCancerSpecify.setText(d["famCancerSpecify"] as? String ?: "")
        binding.cbFamCerebrovascular.isChecked = d["famCerebrovascular"] as? Boolean ?: false
        binding.cbFamCoronary.isChecked        = d["famCoronary"] as? Boolean ?: false
        binding.cbFamDiabetes.isChecked        = d["famDiabetes"] as? Boolean ?: false
        binding.cbFamEmphysema.isChecked       = d["famEmphysema"] as? Boolean ?: false
        binding.cbFamEpilepsy.isChecked        = d["famEpilepsy"] as? Boolean ?: false
        binding.cbFamHepatitis.isChecked       = d["famHepatitis"] as? Boolean ?: false
        binding.etFamHepatitisSpecify.setText(d["famHepatitisSpecify"] as? String ?: "")
        binding.cbFamHyperlipidemia.isChecked  = d["famHyperlipidemia"] as? Boolean ?: false
        binding.cbFamHypertension.isChecked    = d["famHypertension"] as? Boolean ?: false
        binding.etFamHighestBP.setText(d["famHighestBP"] as? String ?: "")
        binding.cbFamPeptic.isChecked          = d["famPeptic"] as? Boolean ?: false
        binding.cbFamPneumonia.isChecked       = d["famPneumonia"] as? Boolean ?: false
        binding.cbFamThyroid.isChecked         = d["famThyroid"] as? Boolean ?: false
        binding.cbFamPTB.isChecked             = d["famPTB"] as? Boolean ?: false
        binding.etFamPTBSpecify.setText(d["famPTBSpecify"] as? String ?: "")
        binding.cbFamUTI.isChecked             = d["famUTI"] as? Boolean ?: false
        binding.cbFamMental.isChecked          = d["famMental"] as? Boolean ?: false
        binding.etFamilyOthers.setText(d["famOthers"] as? String ?: "")
        binding.etFamPastSurgicalHistory.setText(d["famSurgical"] as? String ?: "")
        binding.etFamDateDone.setText(d["famSurgicalDate"] as? String ?: "")

        // Menstrual History
        binding.etMenarche.setText(d["menarche"] as? String ?: "")
        binding.etSexualIntercourse.setText(d["sexualIntercourse"] as? String ?: "")
        binding.etLastMenstrual.setText(d["lastMenstrual"] as? String ?: "")
        binding.etPeriodDuration.setText(d["periodDuration"] as? String ?: "")
        binding.etPads.setText(d["pads"] as? String ?: "")
        binding.etCycle.setText(d["cycle"] as? String ?: "")
        binding.etBirthControl.setText(d["birthControl"] as? String ?: "")
        when (d["menopause"] as? String) {
            "Yes" -> binding.rbMenopauseYes.isChecked = true
            "No"  -> binding.rbMenopauseNo.isChecked = true
        }

        // Pregnancy History
        binding.etGravida.setText(d["gravida"] as? String ?: "")
        binding.etPara.setText(d["para"] as? String ?: "")
        binding.etTerm.setText(d["term"] as? String ?: "")
        binding.etPreterm.setText(d["preterm"] as? String ?: "")
        binding.etAbortion.setText(d["abortion"] as? String ?: "")
        binding.etLiving.setText(d["living"] as? String ?: "")
        binding.etTypeOfDelivery.setText(d["typeOfDelivery"] as? String ?: "")
        binding.etPregnancyHypertension.setText(d["pregnancyHypertension"] as? String ?: "")

        // Physical Examination
        binding.etHeight.setText(d["height"] as? String ?: "")
        binding.etWeight.setText(d["weight"] as? String ?: "")
        binding.etTemp.setText(d["temperature"] as? String ?: "")
        binding.etBP.setText(d["bloodPressure"] as? String ?: "")
        binding.etHR.setText(d["heartRate"] as? String ?: "")
        binding.etRR.setText(d["respiratoryRate"] as? String ?: "")
        binding.etVisualAcuityRight.setText(d["visualRight"] as? String ?: "")
        binding.etVisualAcuityLeft.setText(d["visualLeft"] as? String ?: "")
        binding.etBloodType.setText(d["bloodType"] as? String ?: "")

        // Pedia
        binding.etBodyLength.setText(d["bodyLength"] as? String ?: "")
        binding.etHeadCircumference.setText(d["headCircumference"] as? String ?: "")
        binding.etChestCircumference.setText(d["chestCircumference"] as? String ?: "")
        binding.etAbdomen.setText(d["abdomen"] as? String ?: "")
        binding.etHip.setText(d["hip"] as? String ?: "")
        binding.etLimbs.setText(d["limbs"] as? String ?: "")
        binding.etMUAC.setText(d["muac"] as? String ?: "")
    }

    // ── Save profile ──────────────────────────────────────────────────────────

    private fun saveProfile() {
        val sex = when (binding.rgGender.checkedRadioButtonId) {
            R.id.rbMale   -> "Male"
            R.id.rbFemale -> "Female"
            else -> ""
        }
        val smoking = when (binding.rgSmoking.checkedRadioButtonId) {
            R.id.rbSmokingYes  -> "Yes"
            R.id.rbSmokingNo   -> "No"
            R.id.rbSmokingQuit -> "Quit"
            else -> ""
        }
        val alcohol = when (binding.rgAlcohol.checkedRadioButtonId) {
            R.id.rbAlcoholYes  -> "Yes"
            R.id.rbAlcoholNo   -> "No"
            R.id.rbAlcoholQuit -> "Quit"
            else -> ""
        }
        val drugs = when (binding.rgDrugs.checkedRadioButtonId) {
            R.id.rbDrugsYes  -> "Yes"
            R.id.rbDrugsNo   -> "No"
            R.id.rbDrugsQuit -> "Quit"
            else -> ""
        }
        val sexuallyActive = when (binding.rgSex.checkedRadioButtonId) {
            R.id.rbSexYes -> "Yes"
            R.id.rbSexNo  -> "No"
            else -> ""
        }
        val menopause = when (binding.rgMenopause.checkedRadioButtonId) {
            R.id.rbMenopauseYes -> "Yes"
            R.id.rbMenopauseNo  -> "No"
            else -> ""
        }

        val data = hashMapOf(
            "uid"              to targetUid,
            "name"             to binding.etName.text.toString().trim(),
            "age"              to binding.etAge.text.toString().trim(),
            "birthday"         to binding.etBirthday.text.toString().trim(),
            "sex"              to sex,
            "address"          to binding.etAddress.text.toString().trim(),
            "contact"          to binding.etContact.text.toString().trim(),
            "email"            to binding.etEmail.text.toString().trim(),
            "philhealth"       to binding.etPhilhealth.text.toString().trim(),
            "dateRegistration" to binding.etDateRegistration.text.toString().trim(),
            "memberType"       to binding.etMemberType.text.toString().trim(),
            "facility"         to binding.etFacility.text.toString().trim(),
            "facilityAddress"  to binding.etFacilityAddress.text.toString().trim(),
            "signature"        to binding.etSignature.text.toString().trim(),
            "smoking"          to smoking,
            "packYear"         to binding.etPackYear.text.toString().trim(),
            "alcohol"          to alcohol,
            "bottleYear"       to binding.etBottleYear.text.toString().trim(),
            "drugs"            to drugs,
            "sexuallyActive"   to sexuallyActive,
            "bcg"                to binding.cbBCG.isChecked,
            "opv1"               to binding.cbOPV1.isChecked,
            "opv2"               to binding.cbOPV2.isChecked,
            "opv3"               to binding.cbOPV3.isChecked,
            "dpt1"               to binding.cbDPT1.isChecked,
            "dpt2"               to binding.cbDPT2.isChecked,
            "dpt3"               to binding.cbDPT3.isChecked,
            "measles"            to binding.cbMeasles.isChecked,
            "hepa2"              to binding.cbHepa2.isChecked,
            "hepa3"              to binding.cbHepa3.isChecked,
            "varicella"          to binding.cbVaricella.isChecked,
            "noneChild"          to binding.cbNoneChild.isChecked,
            "hpv"                to binding.cbHPV.isChecked,
            "mmr"                to binding.cbMMR.isChecked,
            "adultNone"          to binding.cbAdultNone.isChecked,
            "pneumococcal"       to binding.cbPneumococcal.isChecked,
            "flu"                to binding.cbFlu.isChecked,
            "immunizationOthers" to binding.etImmunizationOthers.text.toString().trim(),
            "familyPlanning"     to binding.cbFamilyPlanning.isChecked,
            "allergy"            to binding.cbAllergy.isChecked,
            "allergySpecify"     to binding.etAllergySpecify.text.toString().trim(),
            "asthma"             to binding.cbAsthma.isChecked,
            "cancer"             to binding.cbCancer.isChecked,
            "cancerSpecify"      to binding.etCancerSpecify.text.toString().trim(),
            "cerebrovascular"    to binding.cbCerebrovascular.isChecked,
            "coronary"           to binding.cbCoronary.isChecked,
            "diabetes"           to binding.cbDiabetes.isChecked,
            "emphysema"          to binding.cbEmphysema.isChecked,
            "epilepsy"           to binding.cbEpilepsy.isChecked,
            "hepatitis"          to binding.cbHepatitis.isChecked,
            "hepatitisSpecify"   to binding.etHepatitisSpecify.text.toString().trim(),
            "hyperlipidemia"     to binding.cbHyperlipidemia.isChecked,
            "hypertension"       to binding.cbHypertension.isChecked,
            "highestBP"          to binding.etHighestBP.text.toString().trim(),
            "peptic"             to binding.cbPeptic.isChecked,
            "pneumonia"          to binding.cbPneumonia.isChecked,
            "thyroid"            to binding.cbThyroid.isChecked,
            "ptb"                to binding.cbPTB.isChecked,
            "ptbSpecify"         to binding.etPTBSpecify.text.toString().trim(),
            "uti"                to binding.cbUTI.isChecked,
            "mental"             to binding.cbMental.isChecked,
            "pastOthers"         to binding.etPastOthers.text.toString().trim(),
            "pastSurgical"       to binding.etPastSurgicalHistory.text.toString().trim(),
            "pastSurgicalDate"   to binding.etDateDone.text.toString().trim(),
            "famAllergy"         to binding.cbFamAllergy.isChecked,
            "famAllergySpecify"  to binding.etFamAllergySpecify.text.toString().trim(),
            "famAsthma"          to binding.cbFamAsthma.isChecked,
            "famCancer"          to binding.cbFamCancer.isChecked,
            "famCancerSpecify"   to binding.etFamCancerSpecify.text.toString().trim(),
            "famCerebrovascular" to binding.cbFamCerebrovascular.isChecked,
            "famCoronary"        to binding.cbFamCoronary.isChecked,
            "famDiabetes"        to binding.cbFamDiabetes.isChecked,
            "famEmphysema"       to binding.cbFamEmphysema.isChecked,
            "famEpilepsy"        to binding.cbFamEpilepsy.isChecked,
            "famHepatitis"       to binding.cbFamHepatitis.isChecked,
            "famHepatitisSpecify" to binding.etFamHepatitisSpecify.text.toString().trim(),
            "famHyperlipidemia"  to binding.cbFamHyperlipidemia.isChecked,
            "famHypertension"    to binding.cbFamHypertension.isChecked,
            "famHighestBP"       to binding.etFamHighestBP.text.toString().trim(),
            "famPeptic"          to binding.cbFamPeptic.isChecked,
            "famPneumonia"       to binding.cbFamPneumonia.isChecked,
            "famThyroid"         to binding.cbFamThyroid.isChecked,
            "famPTB"             to binding.cbFamPTB.isChecked,
            "famPTBSpecify"      to binding.etFamPTBSpecify.text.toString().trim(),
            "famUTI"             to binding.cbFamUTI.isChecked,
            "famMental"          to binding.cbFamMental.isChecked,
            "famOthers"          to binding.etFamilyOthers.text.toString().trim(),
            "famSurgical"        to binding.etFamPastSurgicalHistory.text.toString().trim(),
            "famSurgicalDate"    to binding.etFamDateDone.text.toString().trim(),
            "menarche"           to binding.etMenarche.text.toString().trim(),
            "sexualIntercourse"  to binding.etSexualIntercourse.text.toString().trim(),
            "lastMenstrual"      to binding.etLastMenstrual.text.toString().trim(),
            "periodDuration"     to binding.etPeriodDuration.text.toString().trim(),
            "pads"               to binding.etPads.text.toString().trim(),
            "cycle"              to binding.etCycle.text.toString().trim(),
            "birthControl"       to binding.etBirthControl.text.toString().trim(),
            "menopause"          to menopause,
            "gravida"            to binding.etGravida.text.toString().trim(),
            "para"               to binding.etPara.text.toString().trim(),
            "term"               to binding.etTerm.text.toString().trim(),
            "preterm"            to binding.etPreterm.text.toString().trim(),
            "abortion"           to binding.etAbortion.text.toString().trim(),
            "living"             to binding.etLiving.text.toString().trim(),
            "typeOfDelivery"     to binding.etTypeOfDelivery.text.toString().trim(),
            "pregnancyHypertension" to binding.etPregnancyHypertension.text.toString().trim(),
            "height"             to binding.etHeight.text.toString().trim(),
            "weight"             to binding.etWeight.text.toString().trim(),
            "temperature"        to binding.etTemp.text.toString().trim(),
            "bloodPressure"      to binding.etBP.text.toString().trim(),
            "heartRate"          to binding.etHR.text.toString().trim(),
            "respiratoryRate"    to binding.etRR.text.toString().trim(),
            "visualRight"        to binding.etVisualAcuityRight.text.toString().trim(),
            "visualLeft"         to binding.etVisualAcuityLeft.text.toString().trim(),
            "bloodType"          to binding.etBloodType.text.toString().trim(),
            "bodyLength"         to binding.etBodyLength.text.toString().trim(),
            "headCircumference"  to binding.etHeadCircumference.text.toString().trim(),
            "chestCircumference" to binding.etChestCircumference.text.toString().trim(),
            "abdomen"            to binding.etAbdomen.text.toString().trim(),
            "hip"                to binding.etHip.text.toString().trim(),
            "limbs"              to binding.etLimbs.text.toString().trim(),
            "muac"               to binding.etMUAC.text.toString().trim(),
            "updatedAt"          to System.currentTimeMillis()
        )

        // Use .set() for both new and existing profiles — it creates or overwrites
        // the document at targetUid, which is either the passed patientUid (edit)
        // or a fresh auto-generated id (new).
        db.collection("individual_profiles").document(targetUid)
            .set(data)
            .addOnSuccessListener {
                val msg = if (isNewProfile) "New profile created!" else "Profile saved!"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Save failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}