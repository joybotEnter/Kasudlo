package com.example.kasudlo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kasudlo.databinding.ActivityCommunityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityProfileDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityProfileBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommunityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // PERSONAL INFO
        val name = binding.etName.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        val birthday = binding.etBirthday.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val contact = binding.etContact.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val philhealth = binding.etPhilhealth.text.toString().trim()
        val registrationDate = binding.etDateRegistration.text.toString().trim()
        val memberType = binding.etMemberType.text.toString().trim()
        val facility = binding.etFacility.text.toString().trim()
        val facilityAddress = binding.etFacilityAddress.text.toString().trim()
        val signature = binding.etSignature.text.toString().trim()

        val gender = when (binding.rgGender.checkedRadioButtonId) {
            R.id.rbMale -> "Male"
            R.id.rbFemale -> "Female"
            else -> ""
        }

        // VALIDATION
        if (name.isEmpty()) {
            binding.etName.error = "Required"
            binding.etName.requestFocus()
            return
        }

        // SOCIAL HISTORY
        val smoking = when (binding.rgSmoking.checkedRadioButtonId) {
            R.id.rbSmokingYes -> "Yes"
            R.id.rbSmokingNo -> "No"
            R.id.rbSmokingQuit -> "Quit"
            else -> ""
        }

        val alcohol = when (binding.rgAlcohol.checkedRadioButtonId) {
            R.id.rbAlcoholYes -> "Yes"
            R.id.rbAlcoholNo -> "No"
            R.id.rbAlcoholQuit -> "Quit"
            else -> ""
        }

        val drugs = when (binding.rgDrugs.checkedRadioButtonId) {
            R.id.rbDrugsYes -> "Yes"
            R.id.rbDrugsNo -> "No"
            R.id.rbDrugsQuit -> "Quit"
            else -> ""
        }

        val sexuallyActive = when (binding.rgSex.checkedRadioButtonId) {
            R.id.rbSexYes -> "Yes"
            R.id.rbSexNo -> "No"
            else -> ""
        }

        // IMMUNIZATION - CHILDREN
        val immunizationChildren = hashMapOf(
            "bcg" to binding.cbBCG.isChecked,
            "opv1" to binding.cbOPV1.isChecked,
            "opv2" to binding.cbOPV2.isChecked,
            "opv3" to binding.cbOPV3.isChecked,
            "dpt1" to binding.cbDPT1.isChecked,
            "dpt2" to binding.cbDPT2.isChecked,
            "dpt3" to binding.cbDPT3.isChecked,
            "measles" to binding.cbMeasles.isChecked,
            "hepa2" to binding.cbHepa2.isChecked,
            "hepa3" to binding.cbHepa3.isChecked,
            "varicella" to binding.cbVaricella.isChecked,
            "none" to binding.cbNoneChild.isChecked
        )

        // IMMUNIZATION - ADULT
        val immunizationAdult = hashMapOf(
            "hpv" to binding.cbHPV.isChecked,
            "mmr" to binding.cbMMR.isChecked,
            "none" to binding.cbAdultNone.isChecked
        )

        // IMMUNIZATION - ELDERLY
        val immunizationElderly = hashMapOf(
            "pneumococcal" to binding.cbPneumococcal.isChecked,
            "flu" to binding.cbFlu.isChecked,
            "others" to binding.etImmunizationOthers.text.toString().trim()
        )

        // FAMILY PLANNING
        val familyPlanning = binding.cbFamilyPlanning.isChecked

        // PAST MEDICAL HISTORY
        val pastMedicalHistory = hashMapOf(
            "allergy" to binding.cbAllergy.isChecked,
            "allergySpecify" to binding.etAllergySpecify.text.toString().trim(),

            "asthma" to binding.cbAsthma.isChecked,

            "cancer" to binding.cbCancer.isChecked,
            "cancerSpecify" to binding.etCancerSpecify.text.toString().trim(),

            "cerebrovascularDisease" to binding.cbCerebrovascular.isChecked,
            "coronaryDisease" to binding.cbCoronary.isChecked,
            "diabetes" to binding.cbDiabetes.isChecked,
            "emphysema" to binding.cbEmphysema.isChecked,
            "epilepsy" to binding.cbEpilepsy.isChecked,

            "hepatitis" to binding.cbHepatitis.isChecked,
            "hepatitisSpecify" to binding.etHepatitisSpecify.text.toString().trim(),

            "hyperlipidemia" to binding.cbHyperlipidemia.isChecked,

            "hypertension" to binding.cbHypertension.isChecked,
            "highestBP" to binding.etHighestBP.text.toString().trim(),

            "pepticUlcer" to binding.cbPeptic.isChecked,
            "pneumonia" to binding.cbPneumonia.isChecked,
            "thyroidDisease" to binding.cbThyroid.isChecked,

            "ptb" to binding.cbPTB.isChecked,
            "ptbSpecify" to binding.etPTBSpecify.text.toString().trim(),

            "uti" to binding.cbUTI.isChecked,
            "mentalIllness" to binding.cbMental.isChecked,

            "others" to binding.etPastOthers.text.toString().trim(),

            "pastSurgicalHistory" to binding.etPastSurgicalHistory.text.toString().trim(),
            "dateDone" to binding.etDateDone.text.toString().trim()
        )

        // FAMILY HISTORY
        val familyHistory = hashMapOf(

            "allergy" to binding.cbFamAllergy.isChecked,
            "allergySpecify" to binding.etFamAllergySpecify.text.toString().trim(),

            "asthma" to binding.cbFamAsthma.isChecked,

            "cancer" to binding.cbFamCancer.isChecked,
            "cancerSpecify" to binding.etFamCancerSpecify.text.toString().trim(),

            "cerebrovascularDisease" to binding.cbFamCerebrovascular.isChecked,
            "coronaryDisease" to binding.cbFamCoronary.isChecked,
            "diabetes" to binding.cbFamDiabetes.isChecked,
            "emphysema" to binding.cbFamEmphysema.isChecked,
            "epilepsy" to binding.cbFamEpilepsy.isChecked,

            "hepatitis" to binding.cbFamHepatitis.isChecked,
            "hepatitisSpecify" to binding.etFamHepatitisSpecify.text.toString().trim(),

            "hyperlipidemia" to binding.cbFamHyperlipidemia.isChecked,

            "hypertension" to binding.cbFamHypertension.isChecked,
            "highestBP" to binding.etFamHighestBP.text.toString().trim(),

            "pepticUlcer" to binding.cbFamPeptic.isChecked,
            "pneumonia" to binding.cbFamPneumonia.isChecked,
            "thyroidDisease" to binding.cbFamThyroid.isChecked,

            "ptb" to binding.cbFamPTB.isChecked,
            "ptbSpecify" to binding.etFamPTBSpecify.text.toString().trim(),

            "uti" to binding.cbFamUTI.isChecked,
            "mentalIllness" to binding.cbFamMental.isChecked,

            "others" to binding.etFamilyOthers.text.toString().trim(),

            "pastSurgicalHistory" to binding.etFamPastSurgicalHistory.text.toString().trim(),
            "dateDone" to binding.etFamDateDone.text.toString().trim()
        )

        // MENSTRUAL HISTORY
        val menopause = when (binding.rgMenopause.checkedRadioButtonId) {
            R.id.rbMenopauseYes -> "Yes"
            R.id.rbMenopauseNo -> "No"
            else -> ""
        }

        val menstrualHistory = hashMapOf(
            "menarche" to binding.etMenarche.text.toString().trim(),
            "sexualIntercourse" to binding.etSexualIntercourse.text.toString().trim(),
            "lastMenstrualPeriod" to binding.etLastMenstrual.text.toString().trim(),
            "periodDuration" to binding.etPeriodDuration.text.toString().trim(),
            "padsPerDay" to binding.etPads.text.toString().trim(),
            "intervalCycle" to binding.etCycle.text.toString().trim(),
            "birthControl" to binding.etBirthControl.text.toString().trim(),
            "menopause" to menopause
        )

        // PREGNANCY HISTORY
        val pregnancyHistory = hashMapOf(
            "gravida" to binding.etGravida.text.toString().trim(),
            "para" to binding.etPara.text.toString().trim(),
            "term" to binding.etTerm.text.toString().trim(),
            "preterm" to binding.etPreterm.text.toString().trim(),
            "abortion" to binding.etAbortion.text.toString().trim(),
            "living" to binding.etLiving.text.toString().trim(),
            "typeOfDelivery" to binding.etTypeOfDelivery.text.toString().trim(),
            "pregnancyInducedHypertension" to binding.etPregnancyHypertension.text.toString().trim()
        )

        // PHYSICAL EXAM
        val physicalExam = hashMapOf(
            "height" to binding.etHeight.text.toString().trim(),
            "weight" to binding.etWeight.text.toString().trim(),
            "temperature" to binding.etTemp.text.toString().trim(),
            "bloodPressure" to binding.etBP.text.toString().trim(),
            "heartRate" to binding.etHR.text.toString().trim(),
            "respiratoryRate" to binding.etRR.text.toString().trim(),

            "visualAcuityRight" to binding.etVisualAcuityRight.text.toString().trim(),
            "visualAcuityLeft" to binding.etVisualAcuityLeft.text.toString().trim(),

            "bloodType" to binding.etBloodType.text.toString().trim()
        )

        // PEDIA CLIENT
        val pediaClient = hashMapOf(
            "bodyLength" to binding.etBodyLength.text.toString().trim(),
            "headCircumference" to binding.etHeadCircumference.text.toString().trim(),
            "chestCircumference" to binding.etChestCircumference.text.toString().trim(),
            "abdomen" to binding.etAbdomen.text.toString().trim(),
            "hip" to binding.etHip.text.toString().trim(),
            "limbs" to binding.etLimbs.text.toString().trim(),
            "muac" to binding.etMUAC.text.toString().trim()
        )

        // MAIN PROFILE
        val profile = hashMapOf(

            "nurseUid" to uid,

            // PERSONAL INFO
            "name" to name,
            "age" to age,
            "birthday" to birthday,
            "gender" to gender,
            "address" to address,
            "contactNumber" to contact,
            "email" to email,
            "philhealthNumber" to philhealth,
            "registrationDate" to registrationDate,
            "memberType" to memberType,
            "facility" to facility,
            "facilityAddress" to facilityAddress,
            "signature" to signature,

            // SOCIAL HISTORY
            "smoking" to smoking,
            "packsPerYear" to binding.etPackYear.text.toString().trim(),

            "alcohol" to alcohol,
            "bottlesPerYear" to binding.etBottleYear.text.toString().trim(),

            "drugs" to drugs,
            "sexuallyActive" to sexuallyActive,

            // IMMUNIZATION
            "immunizationChildren" to immunizationChildren,
            "immunizationAdult" to immunizationAdult,
            "immunizationElderly" to immunizationElderly,

            // FAMILY PLANNING
            "familyPlanning" to familyPlanning,

            // MEDICAL HISTORY
            "pastMedicalHistory" to pastMedicalHistory,

            // FAMILY HISTORY
            "familyHistory" to familyHistory,

            // MENSTRUAL HISTORY
            "menstrualHistory" to menstrualHistory,

            // PREGNANCY HISTORY
            "pregnancyHistory" to pregnancyHistory,

            // PHYSICAL EXAM
            "physicalExam" to physicalExam,

            // PEDIA CLIENT
            "pediaClient" to pediaClient,

            "createdAt" to System.currentTimeMillis()
        )

        binding.btnSave.isEnabled = false

        db.collection("community_profiles")
            .add(profile)
            .addOnSuccessListener {

                val activityLog = hashMapOf(
                    "nurseUid" to uid,
                    "type" to "profile",
                    "title" to "Community Profile Added",
                    "description" to "$name profile created",
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("activity_logs")
                    .add(activityLog)

                Toast.makeText(
                    this,
                    "Profile saved successfully 👤",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener {

                binding.btnSave.isEnabled = true

                Toast.makeText(
                    this,
                    it.message ?: "Failed to save profile",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}