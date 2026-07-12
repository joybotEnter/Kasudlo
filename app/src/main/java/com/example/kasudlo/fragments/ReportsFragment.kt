package com.example.kasudlo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.kasudlo.R
import com.example.kasudlo.models.Household
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private var lastReportText: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragments_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        val btnGenerate = view.findViewById<LinearLayout>(R.id.btnGenerate)
        val btnTally    = view.findViewById<LinearLayout>(R.id.btnAutoTally)
        val btnShare    = view.findViewById<LinearLayout>(R.id.btnShare)
        val previewCard = view.findViewById<LinearLayout>(R.id.reportPreviewCard)
        val tvPreview   = view.findViewById<TextView>(R.id.tvPreview)
        val tvStatus    = view.findViewById<TextView>(R.id.tvReportStatus)

        // ── Monthly Report ─────────────────────────────────────────────────────
        // Fetches: households + individual_profiles + assessments
        btnGenerate.setOnClickListener {
            tvStatus.text = "Generating monthly report..."
            val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
            val generatedAt = SimpleDateFormat("MMM d, yyyy hh:mm a", Locale.getDefault()).format(Date())

            // Step 1: households
            db.collection("households").get().addOnSuccessListener { householdDocs ->
                if (!isAdded) return@addOnSuccessListener
                val households   = householdDocs.map { it.toObject(Household::class.java) }
                val totalMembers = households.sumOf { it.members }
                val barangays    = households.map { it.barangay }.filter { it.isNotBlank() }.toSet()

                // Step 2: individual_profiles
                db.collection("individual_profiles").get().addOnSuccessListener { profileDocs ->
                    if (!isAdded) return@addOnSuccessListener

                    // ── Profile tallies ──
                    var pMale = 0; var pFemale = 0
                    val pAgeGroups = linkedMapOf(
                        "0-4" to 0, "5-9" to 0, "10-14" to 0, "15-19" to 0,
                        "20-24" to 0, "25-29" to 0, "30-34" to 0, "35-39" to 0,
                        "40-44" to 0, "45-49" to 0, "50-59" to 0, "60-64" to 0,
                        "65-69" to 0, "70-74" to 0, "75-79" to 0, "80+" to 0
                    )
                    // Social history
                    var pSmokingYes = 0; var pSmokingNo = 0; var pSmokingQuit = 0
                    var pAlcoholYes = 0; var pAlcoholNo = 0; var pAlcoholQuit = 0
                    var pDrugsYes   = 0; var pDrugsNo   = 0; var pDrugsQuit  = 0
                    // NCDs (past medical history checkboxes)
                    var pHypertension = 0; var pDiabetes = 0; var pAsthma = 0
                    var pCancer = 0; var pCerebrovascular = 0; var pCoronary = 0
                    var pEmphysema = 0; var pEpilepsy = 0; var pHepatitis = 0
                    var pHyperlipidemia = 0; var pPeptic = 0; var pPneumonia = 0
                    var pThyroid = 0; var pPTB = 0; var pUTI = 0; var pMental = 0
                    // Immunization coverage
                    var iBCG = 0; var iOPV1 = 0; var iOPV2 = 0; var iOPV3 = 0
                    var iDPT1 = 0; var iDPT2 = 0; var iDPT3 = 0; var iMeasles = 0
                    var iHepa2 = 0; var iHepa3 = 0; var iVaricella = 0; var iHPV = 0
                    var iMMR = 0; var iPneumococcal = 0; var iFlu = 0
                    // Family planning
                    var pFpYes = 0; var pFpNo = 0
                    // OB history
                    var pWithOBHistory = 0
                    // Vitals averages
                    var weightSum = 0.0; var weightCount = 0
                    var heightSum = 0.0; var heightCount = 0

                    fun str(data: Map<String, Any>, key: String) = (data[key] as? String ?: "").trim()
                    fun bool(data: Map<String, Any>, key: String) = data[key] as? Boolean ?: false

                    for (doc in profileDocs) {
                        val d = doc.data

                        // Sex
                        when (str(d, "sex").lowercase()) {
                            "male"   -> pMale++
                            "female" -> pFemale++
                        }

                        // Age groups
                        str(d, "age").toIntOrNull()?.let { age ->
                            val bucket = when (age) {
                                in 0..4   -> "0-4";  in 5..9   -> "5-9";   in 10..14 -> "10-14"
                                in 15..19 -> "15-19"; in 20..24 -> "20-24"; in 25..29 -> "25-29"
                                in 30..34 -> "30-34"; in 35..39 -> "35-39"; in 40..44 -> "40-44"
                                in 45..49 -> "45-49"; in 50..59 -> "50-59"; in 60..64 -> "60-64"
                                in 65..69 -> "65-69"; in 70..74 -> "70-74"; in 75..79 -> "75-79"
                                else      -> "80+"
                            }
                            pAgeGroups[bucket] = (pAgeGroups[bucket] ?: 0) + 1
                        }

                        // Social history
                        when (str(d, "smoking").lowercase()) {
                            "yes"  -> pSmokingYes++
                            "no"   -> pSmokingNo++
                            "quit" -> pSmokingQuit++
                        }
                        when (str(d, "alcohol").lowercase()) {
                            "yes"  -> pAlcoholYes++
                            "no"   -> pAlcoholNo++
                            "quit" -> pAlcoholQuit++
                        }
                        when (str(d, "drugs").lowercase()) {
                            "yes"  -> pDrugsYes++
                            "no"   -> pDrugsNo++
                            "quit" -> pDrugsQuit++
                        }

                        // NCDs
                        if (bool(d, "hypertension"))    pHypertension++
                        if (bool(d, "diabetes"))        pDiabetes++
                        if (bool(d, "asthma"))          pAsthma++
                        if (bool(d, "cancer"))          pCancer++
                        if (bool(d, "cerebrovascular")) pCerebrovascular++
                        if (bool(d, "coronary"))        pCoronary++
                        if (bool(d, "emphysema"))       pEmphysema++
                        if (bool(d, "epilepsy"))        pEpilepsy++
                        if (bool(d, "hepatitis"))       pHepatitis++
                        if (bool(d, "hyperlipidemia"))  pHyperlipidemia++
                        if (bool(d, "peptic"))          pPeptic++
                        if (bool(d, "pneumonia"))       pPneumonia++
                        if (bool(d, "thyroid"))         pThyroid++
                        if (bool(d, "ptb"))             pPTB++
                        if (bool(d, "uti"))             pUTI++
                        if (bool(d, "mental"))          pMental++

                        // Immunization
                        if (bool(d, "bcg"))         iBCG++
                        if (bool(d, "opv1"))        iOPV1++
                        if (bool(d, "opv2"))        iOPV2++
                        if (bool(d, "opv3"))        iOPV3++
                        if (bool(d, "dpt1"))        iDPT1++
                        if (bool(d, "dpt2"))        iDPT2++
                        if (bool(d, "dpt3"))        iDPT3++
                        if (bool(d, "measles"))     iMeasles++
                        if (bool(d, "hepa2"))       iHepa2++
                        if (bool(d, "hepa3"))       iHepa3++
                        if (bool(d, "varicella"))   iVaricella++
                        if (bool(d, "hpv"))         iHPV++
                        if (bool(d, "mmr"))         iMMR++
                        if (bool(d, "pneumococcal"))iPneumococcal++
                        if (bool(d, "flu"))         iFlu++

                        // Family planning (Boolean in individual_profiles)
                        val fp = d["familyPlanning"]
                        when {
                            fp is Boolean && fp -> pFpYes++
                            fp is Boolean       -> pFpNo++
                        }

                        // OB history: count anyone with gravida filled in
                        if (str(d, "gravida").isNotBlank()) pWithOBHistory++

                        // Vitals
                        str(d, "weight").toDoubleOrNull()?.let { weightSum += it; weightCount++ }
                        str(d, "height").toDoubleOrNull()?.let { heightSum += it; heightCount++ }
                    }

                    // Step 3: assessments
                    db.collection("assessments").get().addOnSuccessListener { assessmentDocs ->
                        if (!isAdded) return@addOnSuccessListener

                        // Quick assessment tallies for monthly summary
                        var aMale = 0; var aFemale = 0
                        var aSmokingYes = 0; var aAlcoholYes = 0; var aDrugsYes = 0
                        var aHypertension = 0; var aDiabetes = 0
                        var aFpAcceptor = 0; var aFpNonAcceptor = 0
                        var aWaterPotableYes = 0; var aWasteSegYes = 0
                        var aBhcAware = 0

                        for (doc in assessmentDocs) {
                            val d = doc.data
                            fun aStr(key: String) = (d[key] as? String ?: "").trim().lowercase()
                            fun aBool(key: String) = d[key] as? Boolean ?: false

                            for (mn in 1..5) {
                                val mg = aStr("member${mn}Gender")
                                when { mg.contains("female") -> aFemale++; mg.contains("male") -> aMale++ }
                            }
                            if (aStr("smoker").contains("yes"))          aSmokingYes++
                            if (aStr("alcohol").contains("yes"))         aAlcoholYes++
                            if (aStr("drugs").contains("yes"))           aDrugsYes++
                            if (aBool("hypertension") || aStr("ncdType").contains("hypertension")) aHypertension++
                            if (aBool("diabetes")     || aStr("ncdType").contains("diabetes"))     aDiabetes++
                            when {
                                aStr("familyPlanning").contains("non")      -> aFpNonAcceptor++
                                aStr("familyPlanning").contains("acceptor") -> aFpAcceptor++
                            }
                            if (aStr("waterPotable").contains("yes"))    aWaterPotableYes++
                            if (aStr("wasteSegregation").contains("practiced") &&
                                !aStr("wasteSegregation").contains("not")) aWasteSegYes++
                            if (aStr("bhcAwareness").contains("aware") &&
                                !aStr("bhcAwareness").contains("unaware")) aBhcAware++
                        }

                        // ── Build Monthly Report ──────────────────────────
                        val totalProfiles   = profileDocs.size()
                        val totalAssessments = assessmentDocs.size()
                        val avgWeight = if (weightCount > 0) "%.1f kg".format(weightSum / weightCount) else "N/A"
                        val avgHeight = if (heightCount > 0) "%.1f cm".format(heightSum / heightCount) else "N/A"

                        val sb = StringBuilder()
                        sb.appendLine(" MONTHLY HEALTH REPORT")
                        sb.appendLine("Period    : $monthName")
                        sb.appendLine("Generated : $generatedAt")
                        sb.appendLine("═══════════════════════════════")



                        // ── 2. Individual Profiles Summary ──
                        sb.appendLine("\n INDIVIDUAL PROFILES SUMMARY")
                        sb.appendLine("─────────────────────────────")
                        sb.appendLine("  Total Registered : $totalProfiles")
                        sb.appendLine("  Male             : $pMale")
                        sb.appendLine("  Female           : $pFemale")

                        val filledAges = pAgeGroups.filter { it.value > 0 }
                        if (filledAges.isNotEmpty()) {
                            sb.appendLine("\n  Age Distribution")
                            filledAges.forEach { (g, c) -> sb.appendLine("    $g : $c") }
                        }

                        sb.appendLine("\n   Social History")
                        sb.appendLine("    Smoker  — Yes: $pSmokingYes  No: $pSmokingNo  Quit: $pSmokingQuit")
                        sb.appendLine("    Alcohol — Yes: $pAlcoholYes  No: $pAlcoholNo  Quit: $pAlcoholQuit")
                        sb.appendLine("    Drugs   — Yes: $pDrugsYes   No: $pDrugsNo   Quit: $pDrugsQuit")

                        sb.appendLine("\n   Medical History (NCDs with recorded cases)")
                        val ncdList = listOf(
                            "Hypertension" to pHypertension, "Diabetes" to pDiabetes,
                            "Asthma" to pAsthma, "Cancer" to pCancer,
                            "Cerebrovascular" to pCerebrovascular, "Coronary Artery Disease" to pCoronary,
                            "Emphysema" to pEmphysema, "Epilepsy" to pEpilepsy,
                            "Hepatitis" to pHepatitis, "Hyperlipidemia" to pHyperlipidemia,
                            "Peptic Ulcer" to pPeptic, "Pneumonia" to pPneumonia,
                            "Thyroid Disease" to pThyroid, "PTB" to pPTB,
                            "UTI" to pUTI, "Mental Health" to pMental
                        ).filter { it.second > 0 }
                        if (ncdList.isEmpty()) sb.appendLine("    No recorded NCD cases.")
                        else ncdList.forEach { (name, count) -> sb.appendLine("    $name : $count") }

                        sb.appendLine("\n   Immunization Coverage")
                        val vaccineList = listOf(
                            "BCG" to iBCG, "OPV1" to iOPV1, "OPV2" to iOPV2, "OPV3" to iOPV3,
                            "DPT1" to iDPT1, "DPT2" to iDPT2, "DPT3" to iDPT3,
                            "Measles" to iMeasles, "Hepa-B 2" to iHepa2, "Hepa-B 3" to iHepa3,
                            "Varicella" to iVaricella, "HPV" to iHPV, "MMR" to iMMR,
                            "Pneumococcal" to iPneumococcal, "Flu" to iFlu
                        ).filter { it.second > 0 }
                        if (vaccineList.isEmpty()) sb.appendLine("    No immunization data recorded.")
                        else vaccineList.forEach { (name, count) -> sb.appendLine("    $name : $count") }

                        sb.appendLine("\n   Family Planning (Individual Profiles)")
                        sb.appendLine("    Using FP   : $pFpYes")
                        sb.appendLine("    Not Using  : $pFpNo")

                        if (pWithOBHistory > 0)
                            sb.appendLine("\n   Patients with OB History : $pWithOBHistory")

                        sb.appendLine("\n  ⚖ Average Vitals (from profiles)")
                        sb.appendLine("    Avg Weight : $avgWeight")
                        sb.appendLine("    Avg Height : $avgHeight")

                        // ── 3. Assessments Summary ──
                        sb.appendLine("\n ASSESSMENTS SUMMARY")
                        sb.appendLine("─────────────────────────────")
                        sb.appendLine("  Total Submitted : $totalAssessments")
                        if (totalAssessments > 0) {
                            sb.appendLine("  Male            : $aMale")
                            sb.appendLine("  Female          : $aFemale")
                            sb.appendLine("\n   Health Behaviours (Household)")
                            sb.appendLine("    Smoker in household       : $aSmokingYes")
                            sb.appendLine("    Alcohol drinker           : $aAlcoholYes")
                            sb.appendLine("    Drug user                 : $aDrugsYes")
                            sb.appendLine("\n  Reported Conditions (Assessments)")
                            sb.appendLine("    Hypertension              : $aHypertension")
                            sb.appendLine("    Diabetes                  : $aDiabetes")
                            sb.appendLine("\n   Family Planning (Assessments)")
                            sb.appendLine("    Acceptor                  : $aFpAcceptor")
                            sb.appendLine("    Non-Acceptor              : $aFpNonAcceptor")
                            sb.appendLine("\n   Environment")
                            sb.appendLine("    Potable Water             : $aWaterPotableYes")
                            sb.appendLine("    Waste Segregation Practiced: $aWasteSegYes")
                            sb.appendLine("\n   BHC Awareness")
                            sb.appendLine("    Aware of BHC              : $aBhcAware")
                        }

                        sb.appendLine("\n═══════════════════════════════")
                        sb.appendLine("Kasudlo Community Health App")

                        lastReportText = sb.toString()
                        tvPreview.text = lastReportText
                        previewCard.isVisible = true
                        tvStatus.text = "Monthly report generated ✓"
                        Toast.makeText(requireContext(), "Monthly report ready!", Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener {
                        if (!isAdded) return@addOnFailureListener
                        tvStatus.text = "Failed loading assessments."
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener {
                    if (!isAdded) return@addOnFailureListener
                    tvStatus.text = "Failed loading profiles."
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                tvStatus.text = "Failed loading households."
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }


        // ── AUTO TALLY (Full) ─────────────────────────────────────────────
        btnTally.setOnClickListener {
            tvStatus.text = "Tallying all assessment fields..."
            db.collection("assessments").get().addOnSuccessListener { docs ->
                if (!isAdded) return@addOnSuccessListener
                val total = docs.size()

                // ── Sec 1: Demographics ──
                var male = 0; var female = 0; var otherGender = 0; var totalMembersFromAssessments = 0
                val ageGroups = linkedMapOf("0-4" to 0,"5-9" to 0,"10-14" to 0,"15-19" to 0,
                    "20-24" to 0,"25-29" to 0,"30-34" to 0,"35-39" to 0,"40-44" to 0,
                    "45-49" to 0,"50-59" to 0,"60-64" to 0,"65-69" to 0,"70-74" to 0,"75-79" to 0,"80+" to 0)
                val eduMap = linkedMapOf("Kinder" to 0,"Elementary" to 0,"Elementary Grad" to 0,
                    "Elementary Under Grad" to 0,"High School" to 0,"High School Grad" to 0,
                    "High School Under Grad" to 0,"Senior High" to 0,"Senior High Grad" to 0,
                    "College" to 0,"College Grad" to 0,"Vocational" to 0,"Post Graduate" to 0,"No Formal Education" to 0)
                val maritalMap = linkedMapOf("Single" to 0,"Married" to 0,"Widowed" to 0,"Separated" to 0,"Cohabiting" to 0)
                val religionMap = mutableMapOf<String, Int>()
                val occupationMap = mutableMapOf<String, Int>()
                val dialectMap = mutableMapOf<String, Int>()

                // ── Sec 2: Socio-Economic ──
                var incomeAdequate = 0; var incomeNotAdequate = 0
                val sourceMap = linkedMapOf("Employment" to 0,"Business" to 0,"Pension" to 0,"Support from Relatives" to 0)
                val transportMap = linkedMapOf("Tricycle" to 0,"Jeepney" to 0,"PUJ/PUV" to 0,"Bicycle" to 0,"Private Vehicle" to 0)
                val commMap = linkedMapOf("Internet" to 0,"Cell Phone" to 0,"Telephone" to 0,"Postal System" to 0,"Two-Way Radio" to 0)
                val serviceMap = linkedMapOf("Religious Services" to 0,"Livelihood Services" to 0,"Health Services" to 0,
                    "Garbage Collection" to 0,"Peace and Order" to 0)

                // ── Sec 3: Housing & Environment ──
                val ownershipMap = linkedMapOf("Owned" to 0,"Rented" to 0,"Rent Free" to 0,"Squatting" to 0)
                val constructionMap = linkedMapOf("Light" to 0,"Mixed" to 0,"Concrete" to 0)
                var waterPotableYes = 0; var waterPotableNo = 0
                var wasteSegPracticed = 0; var wasteSegNotPracticed = 0
                val toiletMap = linkedMapOf("Ballot System" to 0,"Pail System" to 0,"Overhung Latrine" to 0,"Water Sealed" to 0,"Flush Type" to 0)
                val cookMap = linkedMapOf("Electric Stove" to 0,"Gas Stove" to 0,"Firewood" to 0)
                val disposalMap = linkedMapOf("Hog Feeding" to 0,"Open Dumping" to 0,"Burial in Pit" to 0,
                    "Collected by Garbage Truck" to 0,"Composting" to 0,"Open Burning" to 0)

                // ── Sec 4: Health & Illness ──
                var smokerYes = 0; var smokerNo = 0
                var drugsYes = 0; var drugsNo = 0
                var alcoholYes = 0; var alcoholNo = 0
                val foodChoiceMap = linkedMapOf("Meat" to 0,"Fish" to 0,"Vegetable" to 0,"Mixed" to 0)
                val consultMap = linkedMapOf("Doctor" to 0,"Nurse" to 0,"Midwife" to 0,"Hilot" to 0,"Albularyo" to 0,"Faith Healer" to 0)
                val medTypeMap = linkedMapOf("Prescribed" to 0,"Over-the-Counter" to 0,"Herbals" to 0)
                val checkupMap = linkedMapOf("Once a Year" to 0,"Twice a Year" to 0,"More than Once a Year" to 0)

                // ── Sec 5: Beliefs & Health Services ──
                var fpAcceptor = 0; var fpNonAcceptor = 0
                val fpMethodMap = linkedMapOf("BTL (Bilateral Tubal Ligation)" to 0,"Vasectomy" to 0,"Pills" to 0,"IUD (Intrauterine Device)" to 0,
                    "Injectable" to 0,"Condoms" to 0,"Implant" to 0,"Billings Method" to 0)
                val ncdMap = mutableMapOf<String, Int>()
                val cdMap  = mutableMapOf<String, Int>()
                var bhcAware = 0; var bhcUnaware = 0

                // ── Sec 6: Health Resources & Leadership ──
                val conflictMap = linkedMapOf("Gossip" to 0,"Family Conflict" to 0,"Drugs" to 0,"Riot" to 0,"Alcohol" to 0)
                val leaderMap = linkedMapOf("Barangay Captain" to 0,"Kagawad" to 0,"BHW (Barangay Health Worker)" to 0,"Elderly" to 0,"Religious Leader" to 0)

                // ── Process Each Doc ──────────────────────────────────────
                fun str(data: Map<String,Any>, key: String) = (data[key] as? String ?: "").trim()
                fun bool(data: Map<String,Any>, key: String) = data[key] as? Boolean ?: false

                for (doc in docs) {
                    val d = doc.data

                    // Count filled member slots
                    for (mn in 1..5) { if (str(d,"member${mn}Gender").isNotBlank() || str(d,"member${mn}Name").isNotBlank()) totalMembersFromAssessments++ }

                    // ── All family members (1-5) contribute to demographic tallies ──
                    for (mn in 1..5) {
                        val g = str(d, "member${mn}Gender").lowercase()
                        if (g.isBlank()) continue  // skip empty member slots
                        when { g.contains("female") -> female++; g.contains("male") -> male++; else -> otherGender++ }

                        str(d, "member${mn}Age").toIntOrNull()?.let { age ->
                            val b = when(age){ in 0..4->"0-4"; in 5..9->"5-9"; in 10..14->"10-14"; in 15..19->"15-19";
                                in 20..24->"20-24"; in 25..29->"25-29"; in 30..34->"30-34"; in 35..39->"35-39";
                                in 40..44->"40-44"; in 45..49->"45-49"; in 50..59->"50-59"; in 60..64->"60-64";
                                in 65..69->"65-69"; in 70..74->"70-74"; in 75..79->"75-79"; else->"80+" }
                            ageGroups[b] = (ageGroups[b]?:0)+1
                        }

                        val edu = str(d, "member${mn}Education").lowercase()
                        eduMap.keys.firstOrNull { edu.contains(it.lowercase()) }?.let { eduMap[it] = (eduMap[it]?:0)+1 }

                        val mar = str(d, "member${mn}MaritalStatus").lowercase()
                        maritalMap.keys.firstOrNull { mar.contains(it.lowercase()) }?.let { maritalMap[it] = (maritalMap[it]?:0)+1 }

                        val rel = str(d, "member${mn}Religion")
                        if (rel.isNotBlank()) religionMap[rel] = (religionMap[rel]?:0)+1

                        val occ = str(d, "member${mn}OccupationType")
                        if (occ.isNotBlank()) occupationMap[occ] = (occupationMap[occ]?:0)+1

                        val dialect = str(d, "member${mn}Dialect")
                        if (dialect.isNotBlank()) dialectMap[dialect] = (dialectMap[dialect]?:0)+1
                    }

                    val inc = str(d,"incomeAdequacy").lowercase()
                    when { inc.contains("not") -> incomeNotAdequate++; inc.contains("adequate") -> incomeAdequate++ }

                    if (bool(d,"financeEmployment")) sourceMap["Employment"] = (sourceMap["Employment"]?:0)+1
                    if (bool(d,"financeBusiness"))   sourceMap["Business"]   = (sourceMap["Business"]?:0)+1
                    if (bool(d,"financePension"))     sourceMap["Pension"]    = (sourceMap["Pension"]?:0)+1
                    if (bool(d,"financeRelatives"))   sourceMap["Support from Relatives"] = (sourceMap["Support from Relatives"]?:0)+1

                    if (bool(d,"tricycle"))       transportMap["Tricycle"]        = (transportMap["Tricycle"]?:0)+1
                    if (bool(d,"jeep"))           transportMap["Jeepney"]         = (transportMap["Jeepney"]?:0)+1
                    if (bool(d,"pujPuv"))         transportMap["PUJ/PUV"]         = (transportMap["PUJ/PUV"]?:0)+1
                    if (bool(d,"bicycle"))        transportMap["Bicycle"]         = (transportMap["Bicycle"]?:0)+1
                    if (bool(d,"privateVehicle")) transportMap["Private Vehicle"] = (transportMap["Private Vehicle"]?:0)+1

                    if (bool(d,"internet"))     commMap["Internet"]     = (commMap["Internet"]?:0)+1
                    if (bool(d,"cellPhone"))    commMap["Cell Phone"]   = (commMap["Cell Phone"]?:0)+1
                    if (bool(d,"telephone"))    commMap["Telephone"]    = (commMap["Telephone"]?:0)+1
                    if (bool(d,"postalSystem")) commMap["Postal System"]= (commMap["Postal System"]?:0)+1
                    if (bool(d,"twoWayRadio"))  commMap["Two-Way Radio"]= (commMap["Two-Way Radio"]?:0)+1

                    if (bool(d,"religiousServices"))  serviceMap["Religious Services"]  = (serviceMap["Religious Services"]?:0)+1
                    if (bool(d,"livelihoodServices")) serviceMap["Livelihood Services"] = (serviceMap["Livelihood Services"]?:0)+1
                    if (bool(d,"healthServices"))     serviceMap["Health Services"]     = (serviceMap["Health Services"]?:0)+1
                    if (bool(d,"garbageCollection"))  serviceMap["Garbage Collection"]  = (serviceMap["Garbage Collection"]?:0)+1
                    if (bool(d,"peaceAndOrder"))       serviceMap["Peace and Order"]    = (serviceMap["Peace and Order"]?:0)+1

                    val own = str(d,"homeOwnership").lowercase()
                    ownershipMap.keys.firstOrNull { own.contains(it.lowercase()) }?.let { ownershipMap[it] = (ownershipMap[it]?:0)+1 }

                    val con = str(d,"construction").lowercase()
                    constructionMap.keys.firstOrNull { con.contains(it.lowercase()) }?.let { constructionMap[it] = (constructionMap[it]?:0)+1 }

                    val wp = str(d,"waterPotable").lowercase()
                    when { wp.contains("yes") -> waterPotableYes++; wp.contains("no") -> waterPotableNo++ }

                    val ws = str(d,"wasteSegregation").lowercase()
                    when { ws.contains("not") -> wasteSegNotPracticed++; ws.contains("practiced") -> wasteSegPracticed++ }

                    val tt = str(d,"toiletType").lowercase()
                    toiletMap.keys.firstOrNull { tt.contains(it.lowercase()) }?.let { toiletMap[it] = (toiletMap[it]?:0)+1 }

                    if (bool(d,"cookElectricStove")) cookMap["Electric Stove"] = (cookMap["Electric Stove"]?:0)+1
                    if (bool(d,"cookGasStove"))      cookMap["Gas Stove"]      = (cookMap["Gas Stove"]?:0)+1
                    if (bool(d,"cookFirewood"))      cookMap["Firewood"]       = (cookMap["Firewood"]?:0)+1

                    if (bool(d,"hogFeeding"))   disposalMap["Hog Feeding"]               = (disposalMap["Hog Feeding"]?:0)+1
                    if (bool(d,"openDumping"))  disposalMap["Open Dumping"]              = (disposalMap["Open Dumping"]?:0)+1
                    if (bool(d,"burialInPit"))  disposalMap["Burial in Pit"]             = (disposalMap["Burial in Pit"]?:0)+1
                    if (bool(d,"collected"))    disposalMap["Collected by Garbage Truck"]= (disposalMap["Collected by Garbage Truck"]?:0)+1
                    if (bool(d,"composting"))   disposalMap["Composting"]                = (disposalMap["Composting"]?:0)+1
                    if (bool(d,"openBurning"))  disposalMap["Open Burning"]              = (disposalMap["Open Burning"]?:0)+1

                    val sm = str(d,"smoker").lowercase()
                    when { sm.contains("yes") -> smokerYes++; sm.contains("no") -> smokerNo++ }

                    val dr = str(d,"drugs").lowercase()
                    when { dr.contains("yes") -> drugsYes++; dr.contains("no") -> drugsNo++ }

                    val al = str(d,"alcohol").lowercase()
                    when { al.contains("yes") -> alcoholYes++; al.contains("no") -> alcoholNo++ }

                    val fc = str(d,"foodChoice").lowercase()
                    foodChoiceMap.keys.firstOrNull { fc.contains(it.lowercase()) }?.let { foodChoiceMap[it] = (foodChoiceMap[it]?:0)+1 }

                    if (bool(d,"consultDoctor"))     consultMap["Doctor"]      = (consultMap["Doctor"]?:0)+1
                    if (bool(d,"consultNurse"))      consultMap["Nurse"]       = (consultMap["Nurse"]?:0)+1
                    if (bool(d,"consultMidwife"))    consultMap["Midwife"]     = (consultMap["Midwife"]?:0)+1
                    if (bool(d,"consultHilot"))      consultMap["Hilot"]       = (consultMap["Hilot"]?:0)+1
                    if (bool(d,"consultAlbularyo"))  consultMap["Albularyo"]   = (consultMap["Albularyo"]?:0)+1
                    if (bool(d,"consultFaithHealer"))consultMap["Faith Healer"]= (consultMap["Faith Healer"]?:0)+1

                    if (bool(d,"medPrescribed")) medTypeMap["Prescribed"]       = (medTypeMap["Prescribed"]?:0)+1
                    if (bool(d,"medOTC"))        medTypeMap["Over-the-Counter"] = (medTypeMap["Over-the-Counter"]?:0)+1
                    if (bool(d,"medHerbals"))    medTypeMap["Herbals"]          = (medTypeMap["Herbals"]?:0)+1

                    val mck = str(d,"medCheckup").lowercase()
                    checkupMap.keys.firstOrNull { mck.contains(it.lowercase()) }?.let { checkupMap[it] = (checkupMap[it]?:0)+1 }

                    val fp = str(d,"familyPlanning").lowercase()
                    when { fp.contains("non") -> fpNonAcceptor++; fp.contains("acceptor") -> fpAcceptor++ }

                    if (bool(d,"fpBtl"))          fpMethodMap["BTL (Bilateral Tubal Ligation)"]  = (fpMethodMap["BTL (Bilateral Tubal Ligation)"]?:0)+1
                    if (bool(d,"fpVasectomy"))    fpMethodMap["Vasectomy"]                        = (fpMethodMap["Vasectomy"]?:0)+1
                    if (bool(d,"fpPills"))        fpMethodMap["Pills"]                            = (fpMethodMap["Pills"]?:0)+1
                    if (bool(d,"fpIud"))          fpMethodMap["IUD (Intrauterine Device)"]        = (fpMethodMap["IUD (Intrauterine Device)"]?:0)+1
                    if (bool(d,"fpInjectable"))   fpMethodMap["Injectable"]                       = (fpMethodMap["Injectable"]?:0)+1
                    if (bool(d,"fpCondoms"))      fpMethodMap["Condoms"]                          = (fpMethodMap["Condoms"]?:0)+1
                    if (bool(d,"fpImplant"))      fpMethodMap["Implant"]                          = (fpMethodMap["Implant"]?:0)+1
                    if (bool(d,"fpBillings"))     fpMethodMap["Billings Method"]                  = (fpMethodMap["Billings Method"]?:0)+1

                    val ncd = str(d,"ncdType")
                    if (ncd.isNotBlank()) ncdMap[ncd] = (ncdMap[ncd]?:0)+1

                    val cd = str(d,"cdType")
                    if (cd.isNotBlank()) cdMap[cd] = (cdMap[cd]?:0)+1

                    val bhc = str(d,"bhcAwareness").lowercase()
                    when { bhc.contains("unaware") -> bhcUnaware++; bhc.contains("aware") -> bhcAware++ }

                    if (bool(d,"conflictGossip"))        conflictMap["Gossip"]          = (conflictMap["Gossip"]?:0)+1
                    if (bool(d,"conflictFamilyConflict"))conflictMap["Family Conflict"]  = (conflictMap["Family Conflict"]?:0)+1
                    if (bool(d,"conflictDrugs"))         conflictMap["Drugs"]           = (conflictMap["Drugs"]?:0)+1
                    if (bool(d,"conflictRiot"))          conflictMap["Riot"]            = (conflictMap["Riot"]?:0)+1
                    if (bool(d,"conflictAlcohol"))       conflictMap["Alcohol"]         = (conflictMap["Alcohol"]?:0)+1

                    if (bool(d,"leaderCaptain"))    leaderMap["Barangay Captain"] = (leaderMap["Barangay Captain"]?:0)+1
                    if (bool(d,"leaderKagawad"))    leaderMap["Kagawad"]          = (leaderMap["Kagawad"]?:0)+1
                    if (bool(d,"leaderBhw"))        leaderMap["BHW (Barangay Health Worker)"]  = (leaderMap["BHW (Barangay Health Worker)"]?:0)+1
                    if (bool(d,"leaderElderly"))    leaderMap["Elderly"]          = (leaderMap["Elderly"]?:0)+1
                    if (bool(d,"leaderReligious"))  leaderMap["Religious Leader"] = (leaderMap["Religious Leader"]?:0)+1
                }

                // ── Build Tally Report ────────────────────────────────────
                fun pad(s: String, n: Int = 26) = s.padEnd(n)
                fun section(title: String) = "\n$title\n${"─".repeat(30)}\n"
                fun tally(map: Map<String,Int>) = map.entries.filter { it.value > 0 }.joinToString("\n") { "  ${pad(it.key)}: ${it.value}" }
                fun freeText(map: Map<String,Int>) = map.entries.sortedByDescending { it.value }.take(10)
                    .filter { it.value > 0 }.joinToString("\n") { "  ${pad(it.key)}: ${it.value}" }

                val sb = StringBuilder()
                sb.appendLine("FULL AUTO TALLY REPORT")
                sb.appendLine("Generated: ${SimpleDateFormat("MMM d, yyyy hh:mm a", Locale.getDefault()).format(Date())}")
                sb.appendLine("Total Assessments : $total")
                sb.appendLine("═══════════════════════════════")

                sb.append(section("I. DEMOGRAPHIC PROFILE"))
                sb.appendLine("  Total Individuals Tallied : $totalMembersFromAssessments")
                sb.appendLine(" GENDER")
                sb.appendLine("  ${pad("Male")}  : $male")
                sb.appendLine("  ${pad("Female")}: $female")
                if (otherGender > 0) sb.appendLine("  ${pad("Other/Blank")}: $otherGender")

                sb.appendLine("\n AGE GROUP")
                ageGroups.forEach { (g, c) -> if (c > 0) sb.appendLine("  ${pad(g)}: $c") }

                sb.appendLine("\n EDUCATIONAL ATTAINMENT")
                sb.appendLine(tally(eduMap))

                sb.appendLine("\n MARITAL STATUS")
                sb.appendLine(tally(maritalMap))

                if (religionMap.isNotEmpty()) {
                    sb.appendLine("\n RELIGION (Top 10)")
                    sb.appendLine(freeText(religionMap))
                }

                if (occupationMap.isNotEmpty()) {
                    sb.appendLine("\n OCCUPATION (Top 10)")
                    sb.appendLine(freeText(occupationMap))
                }

                if (dialectMap.isNotEmpty()) {
                    sb.appendLine("\n DIALECT / LANGUAGE SPOKEN (Top 10)")
                    sb.appendLine(freeText(dialectMap))
                }

                sb.append(section("II. SOCIO-ECONOMIC"))
                sb.appendLine(" INCOME ADEQUACY")
                sb.appendLine("  ${pad("Adequate")}    : $incomeAdequate")
                sb.appendLine("  ${pad("Not Adequate")} : $incomeNotAdequate")

                sb.appendLine("\n SOURCE OF INCOME")
                sb.appendLine(tally(sourceMap))

                sb.appendLine("\n TRANSPORTATION")
                sb.appendLine(tally(transportMap))

                sb.appendLine("\n COMMUNICATION")
                sb.appendLine(tally(commMap))

                sb.appendLine("\n COMMUNITY SERVICES")
                sb.appendLine(tally(serviceMap))

                sb.append(section("III. HOUSING & ENVIRONMENT"))
                sb.appendLine(" HOME OWNERSHIP")
                sb.appendLine(tally(ownershipMap))

                sb.appendLine("\n CONSTRUCTION TYPE")
                sb.appendLine(tally(constructionMap))

                sb.appendLine("\n WATER POTABILITY")
                sb.appendLine("  ${pad("Potable (Yes)")}: $waterPotableYes")
                sb.appendLine("  ${pad("Not Potable")} : $waterPotableNo")

                sb.appendLine("\n WASTE SEGREGATION")
                sb.appendLine("  ${pad("Practiced")}    : $wasteSegPracticed")
                sb.appendLine("  ${pad("Not Practiced")}: $wasteSegNotPracticed")

                sb.appendLine("\n TOILET TYPE")
                sb.appendLine(tally(toiletMap))

                sb.appendLine("\n COOKING METHOD")
                sb.appendLine(tally(cookMap))

                sb.appendLine("\n WASTE DISPOSAL METHOD")
                sb.appendLine(tally(disposalMap))

                sb.append(section("IV. HEALTH & ILLNESS PATTERN"))
                sb.appendLine(" SMOKER IN HOUSEHOLD")
                sb.appendLine("  ${pad("Yes")}: $smokerYes")
                sb.appendLine("  ${pad("No")} : $smokerNo")

                sb.appendLine("\n DRUG USER IN HOUSEHOLD")
                sb.appendLine("  ${pad("Yes")}: $drugsYes")
                sb.appendLine("  ${pad("No")} : $drugsNo")

                sb.appendLine("\n ALCOHOL DRINKER IN HOUSEHOLD")
                sb.appendLine("  ${pad("Yes")}: $alcoholYes")
                sb.appendLine("  ${pad("No")} : $alcoholNo")

                sb.appendLine("\n PRIMARY FOOD CHOICE")
                sb.appendLine(tally(foodChoiceMap))

                sb.appendLine("\n WHO THEY CONSULT WHEN SICK")
                sb.appendLine(tally(consultMap))

                sb.appendLine("\n MEDICINE TYPE USED")
                sb.appendLine(tally(medTypeMap))

                sb.appendLine("\n MEDICAL CHECK-UP FREQUENCY")
                sb.appendLine(tally(checkupMap))

                if (ncdMap.isNotEmpty()) {
                    sb.appendLine("\n NON-COMMUNICABLE DISEASES (Top 10)")
                    sb.appendLine(freeText(ncdMap))
                }

                if (cdMap.isNotEmpty()) {
                    sb.appendLine("\n COMMUNICABLE DISEASES (Top 10)")
                    sb.appendLine(freeText(cdMap))
                }

                sb.append(section("V. BELIEFS & HEALTH SERVICES"))
                sb.appendLine("FAMILY PLANNING STATUS")
                sb.appendLine("  ${pad("Acceptor")}    : $fpAcceptor")
                sb.appendLine("  ${pad("Non-Acceptor")}: $fpNonAcceptor")

                sb.appendLine("\n FP METHOD USED")
                sb.appendLine(tally(fpMethodMap))

                sb.append(section("VI. HEALTH RESOURCES & LEADERSHIP"))
                sb.appendLine("BHC AWARENESS")
                sb.appendLine("  ${pad("Aware")}  : $bhcAware")
                sb.appendLine("  ${pad("Unaware")}: $bhcUnaware")

                sb.appendLine("\n COMMUNITY LEADERS IDENTIFIED")
                sb.appendLine(tally(leaderMap))

                sb.appendLine("\nCOMMUNITY CONFLICTS")
                sb.appendLine(tally(conflictMap))

                sb.appendLine("\n═══════════════════════════════")
                sb.appendLine("Kasudlo Community Health App")

                lastReportText = sb.toString()
                tvPreview.text = lastReportText
                previewCard.isVisible = true
                tvStatus.text = "Full tally complete ✓"
                Toast.makeText(requireContext(), "Full tally ready!", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                tvStatus.text = "Tally failed."
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // ── Share ─────────────────────────────────────────────────────────
        btnShare.setOnClickListener {
            if (lastReportText.isBlank()) {
                Toast.makeText(requireContext(), "Generate a report first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Kasudlo Health Report")
                putExtra(Intent.EXTRA_TEXT, lastReportText)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Report via..."))
        }
    }
}