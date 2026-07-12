package com.example.kasudlo

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PatientAssessmentPreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_assessment_preview)

        val assessmentId = intent.getStringExtra("assessmentId") ?: run { finish(); return }

        findViewById<LinearLayout>(R.id.btnBack)?.setOnClickListener { finish() }

        FirebaseFirestore.getInstance()
            .collection("assessments").document(assessmentId).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener
                val d = doc.data ?: return@addOnSuccessListener

                // ── Survey Header ──
                set(R.id.tvControlNo,       d["controlNo"]          as? String)
                set(R.id.tvFamilySize,      d["numberOfFamily"]     as? String)
                set(R.id.tvAddress,         d["address"]            as? String)
                set(R.id.tvInformant,       d["informant"]          as? String)
                set(R.id.tvSurveyedBy,      d["surveyedBy"]         as? String)
                set(R.id.tvFirstVisit,      d["dateFirstVisit"]     as? String)
                set(R.id.tvSecondVisit,     d["dateSecondVisit"]    as? String)
                set(R.id.tvThirdVisit,      d["dateThirdVisit"]     as? String)
                set(R.id.tvTimeStarted,     d["timeStarted"]        as? String)
                set(R.id.tvTimeFinished,    d["timeFinished"]       as? String)
                set(R.id.tvStatusLastVisit, d["statusLastVisit"]    as? String)

                // ── Section I — Demographic ──
                set(R.id.tvMember1Name,             d["member1Name"]             as? String)
                set(R.id.tvMember1Relation,         d["member1Relation"]         as? String)
                set(R.id.tvMember1Sex,              d["member1Gender"]           as? String)
                set(R.id.tvMember1Age,              d["member1Age"]              as? String)
                set(R.id.tvMember1Birthdate,        d["member1Birthdate"]        as? String)
                set(R.id.tvMember1MaritalStatus,    d["member1MaritalStatus"]    as? String)
                set(R.id.tvMember1Religion,         d["member1Religion"]         as? String)
                set(R.id.tvMember1Education,        d["member1Education"]        as? String)
                set(R.id.tvMember1OccupationType,   d["member1OccupationType"]   as? String)
                set(R.id.tvMember1PlaceOfWork,      d["member1PlaceOfWork"]      as? String)
                set(R.id.tvMember1LengthOfResidence,d["member1LengthOfResidence"]as? String)
                set(R.id.tvDialect,                 d["dialect"]                 as? String)

                // Family type (checkboxes → joined string)
                val familyTypes = mutableListOf<String>()
                if (d["familyNuclear"]       == true) familyTypes.add("Nuclear")
                if (d["familyExtended"]      == true) familyTypes.add("Extended")
                if (d["familyDyad"]          == true) familyTypes.add("Dyad")
                if (d["familyHomosexual"]    == true) familyTypes.add("Homosexual")
                if (d["familyCohabiting"]    == true) familyTypes.add("Cohabiting")
                if (d["familyBlended"]       == true) familyTypes.add("Blended")
                if (d["familyWithGrandparents"] == true) familyTypes.add("With Grandparents")
                if (d["familySingleParent"]  == true) familyTypes.add("Single Parent")
                set(R.id.tvFamilyType,      familyTypes.joinToString(", ").ifBlank { null })
                set(R.id.tvFamilyPower,     d["familyPower"]     as? String)
                set(R.id.tvFamilyResidence, d["familyResidence"] as? String)
                set(R.id.tvFamilyDescent,   d["familyDescent"]   as? String)

                // ── Section II — Socio-Economic ──
                val services = mutableListOf<String>()
                if (d["serviceReligious"]  == true) services.add("Religious Services")
                if (d["serviceLivelihood"] == true) services.add("Livelihood Services")
                if (d["serviceHealth"]     == true) services.add("Health Services")
                if (d["serviceGarbage"]    == true) services.add("Garbage Collection")
                if (d["servicePeaceOrder"] == true) services.add("Peace & Order")
                val svcOthers = d["serviceOthers"] as? String
                if (!svcOthers.isNullOrBlank()) services.add("Others: $svcOthers")
                set(R.id.tvCommunityServices, services.joinToString(", ").ifBlank { null })

                val facilities = mutableListOf<String>()
                if (d["facilityBrgyHall"]      == true) facilities.add("Barangay Hall")
                if (d["facilityHealthStation"] == true) facilities.add("Health Station")
                if (d["facilityChurch"]        == true) facilities.add("Church")
                if (d["facilitySchool"]        == true) facilities.add("School")
                if (d["orgSeniorCitizen"]      == true) facilities.add("Senior Citizen Center")
                if (d["orgYouth"]              == true) facilities.add("Youth Center")
                val orgOthers = d["orgOthers"] as? String
                if (!orgOthers.isNullOrBlank()) facilities.add("Others: $orgOthers")
                set(R.id.tvFacilities, facilities.joinToString(", ").ifBlank { null })

                val traditions = mutableListOf<String>()
                if (d["tradBayanihan"]    == true) traditions.add("Bayanihan")
                if (d["tradPalabra"]      == true) traditions.add("Palabra de Honor")
                if (d["tradPakikisama"]   == true) traditions.add("Pakikisama")
                if (d["tradNingas"]       == true) traditions.add("Ningas Kugon")
                if (d["tradFiestas"]      == true) traditions.add("Fiestas")
                if (d["tradFamilyTies"]   == true) traditions.add("Close Family Ties")
                if (d["tradRespectElderly"] == true) traditions.add("Respect for Elderly")
                val tradOthers = d["tradOthers"] as? String
                if (!tradOthers.isNullOrBlank()) traditions.add("Others: $tradOthers")
                set(R.id.tvTraditions, traditions.joinToString(", ").ifBlank { null })

                val leaders = mutableListOf<String>()
                if (d["leaderCaptain"]     == true) leaders.add("Barangay Captain")
                if (d["leaderKagawad"]     == true) leaders.add("Kagawad")
                if (d["leaderBhw"]         == true) leaders.add("BHW (Barangay Health Worker)")
                if (d["leaderReligious"]   == true) leaders.add("Religious Leader")
                if (d["leaderElderly"]     == true) leaders.add("Elderly")
                if (d["leaderInfluential"] == true) leaders.add("Influential Person")
                if (d["leaderNeighbor"]    == true) leaders.add("Neighbor")
                set(R.id.tvLeaders, leaders.joinToString(", ").ifBlank { null })

                val conflicts = mutableListOf<String>()
                if (d["conflictAlcohol"]       == true) conflicts.add("Alcohol")
                if (d["conflictDrugs"]         == true) conflicts.add("Drugs")
                if (d["conflictFamilyConflict"]== true) conflicts.add("Family Conflict")
                if (d["conflictGossip"]        == true) conflicts.add("Gossip")
                if (d["conflictRiot"]          == true) conflicts.add("Riot")
                val conflictOthers = d["conflictOthers"] as? String
                if (!conflictOthers.isNullOrBlank()) conflicts.add("Others: $conflictOthers")
                set(R.id.tvConflicts, conflicts.joinToString(", ").ifBlank { null })

                val resolution = mutableListOf<String>()
                if (d["resolutionBrgyHearing"] == true) resolution.add("Barangay Hearing")
                if (d["resolutionPolice"]      == true) resolution.add("Police")
                if (d["resolutionSettlement"]  == true) resolution.add("Settlement")
                val resOthers = d["resolutionOthers"] as? String
                if (!resOthers.isNullOrBlank()) resolution.add("Others: $resOthers")
                set(R.id.tvResolution, resolution.joinToString(", ").ifBlank { null })

                val rec = mutableListOf<String>()
                if (d["recCourt"]      == true) rec.add("Court")
                if (d["recPlayground"] == true) rec.add("Playground")
                if (d["recPlaza"]      == true) rec.add("Plaza")
                val recOthers = d["recOthers"] as? String
                if (!recOthers.isNullOrBlank()) rec.add("Others: $recOthers")
                set(R.id.tvRecreation, rec.joinToString(", ").ifBlank { null })

                val trans = mutableListOf<String>()
                if (d["transTricycle"]       == true) trans.add("Tricycle")
                if (d["transJeep"]           == true) trans.add("Jeep")
                if (d["transPujPuv"]         == true) trans.add("PUJ/PUV")
                if (d["transBicycle"]        == true) trans.add("Bicycle")
                if (d["transPrivateVehicle"] == true) trans.add("Private Vehicle")
                set(R.id.tvTransportation, trans.joinToString(", ").ifBlank { null })

                val comm = mutableListOf<String>()
                if (d["commCellPhone"]   == true) comm.add("Cell Phone")
                if (d["commInternet"]    == true) comm.add("Internet")
                if (d["commTelephone"]   == true) comm.add("Telephone")
                if (d["commTwoWayRadio"] == true) comm.add("Two-Way Radio")
                if (d["commPostal"]      == true) comm.add("Postal System")
                set(R.id.tvCommunication, comm.joinToString(", ").ifBlank { null })

                val incSrc = mutableListOf<String>()
                if (d["financeEmployment"] == true) incSrc.add("Employment")
                if (d["financeBusiness"]   == true) incSrc.add("Business")
                if (d["financePension"]    == true) incSrc.add("Pension")
                if (d["financeRelatives"]  == true) incSrc.add("Relatives")
                if (d["financeOthers"]     == true) incSrc.add("Others")
                set(R.id.tvIncomeSource, incSrc.joinToString(", ").ifBlank { null })

                set(R.id.tvMonthlyIncome,  d["monthlyIncome"]   as? String)
                set(R.id.tvIncomeAdequacy, d["incomeAdequacy"]  as? String)

                val e1pos = d["earner1Position"] as? String ?: ""
                val e1inc = d["earner1Income"]   as? String ?: ""
                set(R.id.tvEarner1, if (e1pos.isBlank() && e1inc.isBlank()) null else "$e1pos / $e1inc")

                val e2pos = d["earner2Position"] as? String ?: ""
                val e2inc = d["earner2Income"]   as? String ?: ""
                set(R.id.tvEarner2, if (e2pos.isBlank() && e2inc.isBlank()) null else "$e2pos / $e2inc")

                val e3pos = d["earner3Position"] as? String ?: ""
                val e3inc = d["earner3Income"]   as? String ?: ""
                set(R.id.tvEarner3, if (e3pos.isBlank() && e3inc.isBlank()) null else "$e3pos / $e3inc")

                val e4pos = d["earner4Position"] as? String ?: ""
                val e4inc = d["earner4Income"]   as? String ?: ""
                set(R.id.tvEarner4, if (e4pos.isBlank() && e4inc.isBlank()) null else "$e4pos / $e4inc")

                set(R.id.tvRhuPhysician,    d["rhuPhysician"]    as? String)
                set(R.id.tvRhuNurse,        d["rhuNurse"]        as? String)
                set(R.id.tvBhcAwareness,    d["bhcAwareness"]    as? String)

                val bhcMidwife   = d["bhcMidwife"]        as? String ?: ""
                val bhcSchedule  = d["bhcSchedule"]       as? String ?: ""
                val bhcServices  = d["bhcHealthServices"] as? String ?: ""
                set(R.id.tvBhcDetails, listOf(bhcMidwife, bhcSchedule, bhcServices)
                    .filter { it.isNotBlank() }.joinToString(" | ").ifBlank { null })

                set(R.id.tvConcernsSuggestions, d["concernsSuggestions"] as? String)

                // ── Section III — Health Status ──
                set(R.id.tvSmoker, d["smoker"] as? String)
                val smokerDetails = buildString {
                    append(d["smokerName"]       as? String ?: "")
                    val sa = d["smokerAge"] as? String; if (!sa.isNullOrBlank()) append(" | Age: $sa")
                    val sas = d["smokerAgeStarted"] as? String; if (!sas.isNullOrBlank()) append(" | Started: $sas")
                    val sf = d["smokerFrequency"] as? String; if (!sf.isNullOrBlank()) append(" | Freq: $sf")
                    val sr = d["smokerReason"]    as? String; if (!sr.isNullOrBlank()) append(" | Reason: $sr")
                }
                set(R.id.tvSmokerDetails, smokerDetails.ifBlank { null })

                set(R.id.tvAlcohol, d["alcohol"] as? String)
                val alcoholDetails = buildString {
                    append(d["alcoholName"]       as? String ?: "")
                    val aa = d["alcoholAge"] as? String; if (!aa.isNullOrBlank()) append(" | Age: $aa")
                    val aas = d["alcoholAgeStarted"] as? String; if (!aas.isNullOrBlank()) append(" | Started: $aas")
                    val af = d["alcoholFrequency"] as? String; if (!af.isNullOrBlank()) append(" | Freq: $af")
                    val ar = d["alcoholReason"]    as? String; if (!ar.isNullOrBlank()) append(" | Reason: $ar")
                }
                set(R.id.tvAlcoholDetails, alcoholDetails.ifBlank { null })

                set(R.id.tvDrugs, d["drugs"] as? String)
                val drugDetails = buildString {
                    append(d["drugName"]       as? String ?: "")
                    val da = d["drugAge"] as? String; if (!da.isNullOrBlank()) append(" | Age: $da")
                    val das = d["drugAgeStarted"] as? String; if (!das.isNullOrBlank()) append(" | Started: $das")
                    val dt = d["drugTypesUsed"] as? String; if (!dt.isNullOrBlank()) append(" | Types: $dt")
                    val dr = d["drugReason"]    as? String; if (!dr.isNullOrBlank()) append(" | Reason: $dr")
                }
                set(R.id.tvDrugDetails, drugDetails.ifBlank { null })

                val morbidity = buildString {
                    append(d["morbidityName"]   as? String ?: "")
                    val a = d["morbidityAge"]    as? String; if (!a.isNullOrBlank()) append(" | Age: $a")
                    val g = d["morbidityGender"] as? String; if (!g.isNullOrBlank()) append(" | $g")
                    val c = d["morbidityCause"]  as? String; if (!c.isNullOrBlank()) append(" | $c")
                }
                set(R.id.tvMorbidity, morbidity.ifBlank { null })
                val morbInt = buildString {
                    val adm = d["morbidityAdmitted"]     as? String; if (!adm.isNullOrBlank()) append("Admitted: $adm")
                    val inv = d["morbidityIntervention"] as? String; if (!inv.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Intervention: $inv") }
                }
                set(R.id.tvMorbidityIntervention, morbInt.ifBlank { null })

                val mortality = buildString {
                    append(d["mortalityName"]   as? String ?: "")
                    val a = d["mortalityAge"]    as? String; if (!a.isNullOrBlank()) append(" | Age: $a")
                    val g = d["mortalityGender"] as? String; if (!g.isNullOrBlank()) append(" | $g")
                    val c = d["mortalityCause"]  as? String; if (!c.isNullOrBlank()) append(" | $c")
                }
                set(R.id.tvMortality, mortality.ifBlank { null })

                val ncd = buildString {
                    append(d["ncdName"]   as? String ?: "")
                    val a = d["ncdAge"]    as? String; if (!a.isNullOrBlank()) append(" | Age: $a")
                    val g = d["ncdGender"] as? String; if (!g.isNullOrBlank()) append(" | $g")
                    val t = d["ncdType"]   as? String; if (!t.isNullOrBlank()) append(" | $t")
                }
                set(R.id.tvNcd, ncd.ifBlank { null })

                val cd = buildString {
                    append(d["cdName"]   as? String ?: "")
                    val a = d["cdAge"]    as? String; if (!a.isNullOrBlank()) append(" | Age: $a")
                    val g = d["cdGender"] as? String; if (!g.isNullOrBlank()) append(" | $g")
                    val t = d["cdType"]   as? String; if (!t.isNullOrBlank()) append(" | $t")
                }
                set(R.id.tvCd, cd.ifBlank { null })

                val bp = buildString {
                    append(d["bpName"]   as? String ?: "")
                    val a = d["bpAge"]     as? String; if (!a.isNullOrBlank()) append(" | Age: $a")
                    val g = d["bpGender"]  as? String; if (!g.isNullOrBlank()) append(" | $g")
                    val r = d["bpReading"] as? String; if (!r.isNullOrBlank()) append(" | Reading: $r")
                }
                set(R.id.tvBloodPressure, bp.ifBlank { null })

                val anthr = buildString {
                    append(d["anthrName"]   as? String ?: "")
                    val a = d["anthrAge"]    as? String; if (!a.isNullOrBlank()) append(" | Age: $a")
                    val w = d["anthrWeight"] as? String; if (!w.isNullOrBlank()) append(" | Wt: $w")
                    val h = d["anthrHeight"] as? String; if (!h.isNullOrBlank()) append(" | Ht: $h")
                    val b = d["anthrBmi"]    as? String; if (!b.isNullOrBlank()) append(" | BMI: $b")
                    val m = d["anthrMuac"]   as? String; if (!m.isNullOrBlank()) append(" | MUAC: $m")
                }
                set(R.id.tvAnthropometric, anthr.ifBlank { null })

                val waistHip = buildString {
                    val wc = d["waistCircumference"] as? String; if (!wc.isNullOrBlank()) append("Waist: $wc")
                    val hc = d["hipCircumference"]   as? String; if (!hc.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Hip: $hc") }
                    val whr= d["waistHipRatio"]      as? String; if (!whr.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("WHR: $whr") }
                }
                set(R.id.tvWaistHip, waistHip.ifBlank { null })

                val antenatal = buildString {
                    append(d["antenatalName"]    as? String ?: "")
                    val aog = d["antenatalAog"]      as? String; if (!aog.isNullOrBlank()) append(" | AOG: $aog")
                    val chk = d["antenatalCheckup"]  as? String; if (!chk.isNullOrBlank()) append(" | Checkup: $chk")
                    val tet = d["antenatalTetanus"]  as? String; if (!tet.isNullOrBlank()) append(" | Tetanus: $tet")
                }
                set(R.id.tvAntenatal, antenatal.ifBlank { null })

                val imm = buildString {
                    append(d["immunizationName"]   as? String ?: "")
                    val a = d["immunizationAge"]    as? String; if (!a.isNullOrBlank()) append(" | Age: $a")
                    val g = d["immunizationGender"] as? String; if (!g.isNullOrBlank()) append(" | $g")
                }
                set(R.id.tvImmunization, imm.ifBlank { null })

                val immDet = buildString {
                    fun add(label: String, key: String) {
                        val v = d[key] as? String; if (!v.isNullOrBlank()) { if (isNotEmpty()) append("  "); append("$label: $v") }
                    }
                    add("BCG", "immunizationBcg")
                    add("DPT1", "immunizationDpt1"); add("DPT2", "immunizationDpt2"); add("DPT3", "immunizationDpt3")
                    add("OPV1", "immunizationOpv1"); add("OPV2", "immunizationOpv2"); add("OPV3", "immunizationOpv3")
                    add("Hepa1", "immunizationHepa1"); add("Hepa2", "immunizationHepa2"); add("Hepa3", "immunizationHepa3")
                    add("Measles", "immunizationMeasles")
                    add("FIC", "immunizationFic")
                    add("Incomplete", "immunizationIncomplete")
                    add("Complete Age", "immunizationCompleteAge")
                }
                set(R.id.tvImmunizationDetails, immDet.ifBlank { null })

                set(R.id.tvFamilyPlanning, d["familyPlanning"] as? String)
                val fpMethods = mutableListOf<String>()
                if (d["fpPills"]       == true) fpMethods.add("Pills")
                if (d["fpCondoms"]     == true) fpMethods.add("Condoms")
                if (d["fpInjectable"] == true) fpMethods.add("Injectable")
                if (d["fpImplant"]     == true) fpMethods.add("Implant")
                if (d["fpIud"]         == true) fpMethods.add("IUD (Intrauterine Device)")
                if (d["fpBbt"]         == true) fpMethods.add("BBT (Basal Body Temperature)")
                if (d["fpBillings"]    == true) fpMethods.add("Billings Method")
                if (d["fpSdm"]         == true) fpMethods.add("SDM (Standard Days Method)")
                if (d["fpLam"]         == true) fpMethods.add("LAM (Lactational Amenorrhea Method)")
                if (d["fpSymptoThermal"] == true) fpMethods.add("Sympto-Thermal")
                if (d["fpBtl"]         == true) fpMethods.add("BTL (Bilateral Tubal Ligation)")
                if (d["fpVasectomy"]   == true) fpMethods.add("Vasectomy")
                val fpReasons = mutableListOf<String>()
                if (d["fpReasonGoodHealth"]    == true) fpReasons.add("Good Health")
                if (d["fpReasonPersonalBelief"]== true) fpReasons.add("Personal Belief")
                if (d["fpReasonReligious"]     == true) fpReasons.add("Religious")
                if (d["fpReasonInfluence"]     == true) fpReasons.add("Influence")
                val fpReasonOthers = d["fpReasonOthers"] as? String
                if (!fpReasonOthers.isNullOrBlank()) fpReasons.add("Others: $fpReasonOthers")
                val fpNonReasons = mutableListOf<String>()
                if (d["fpNonReasonBadHealth"]    == true) fpNonReasons.add("Bad Health")
                if (d["fpNonReasonPersonalBelief"]== true) fpNonReasons.add("Personal Belief")
                if (d["fpNonReasonReligious"]    == true) fpNonReasons.add("Religious")
                if (d["fpNonReasonInfluence"]    == true) fpNonReasons.add("Influence")
                val fpNonOthers = d["fpNonReasonOthers"] as? String
                if (!fpNonOthers.isNullOrBlank()) fpNonReasons.add("Others: $fpNonOthers")
                val fpText = buildString {
                    if (fpMethods.isNotEmpty()) append("Methods: ${fpMethods.joinToString(", ")}")
                    if (fpReasons.isNotEmpty()) { if (isNotEmpty()) append("\n"); append("Reason (Acceptor): ${fpReasons.joinToString(", ")}") }
                    if (fpNonReasons.isNotEmpty()) { if (isNotEmpty()) append("\n"); append("Reason (Non-Acceptor): ${fpNonReasons.joinToString(", ")}") }
                }
                set(R.id.tvFpDetails, fpText.ifBlank { null })

                val consults = mutableListOf<String>()
                if (d["consultDoctor"]      == true) consults.add("Doctor")
                if (d["consultNurse"]       == true) consults.add("Nurse")
                if (d["consultMidwife"]     == true) consults.add("Midwife")
                if (d["consultHilot"]       == true) consults.add("Hilot")
                if (d["consultAlbularyo"]   == true) consults.add("Albularyo")
                if (d["consultFaithHealer"] == true) consults.add("Faith Healer")
                if (d["consultElderly"]     == true) consults.add("Elderly")
                set(R.id.tvConsults, consults.joinToString(", ").ifBlank { null })

                val meds = mutableListOf<String>()
                if (d["medPrescribed"] == true) meds.add("Prescribed")
                if (d["medOTC"]        == true) meds.add("OTC")
                if (d["medHerbals"]    == true) meds.add("Herbals")
                val medOthers = d["medOthers"] as? String
                if (!medOthers.isNullOrBlank()) meds.add("Others: $medOthers")
                set(R.id.tvMedicines, meds.joinToString(", ").ifBlank { null })

                set(R.id.tvMedCheckup,   d["medCheckup"]    as? String)
                set(R.id.tvDentalCheckup,d["dentalCheckup"] as? String)

                val hbAmt = d["healthBudgetAmount"] as? String
                val hbVal = d["healthBudget"]        as? String
                set(R.id.tvHealthBudget, listOf(hbVal, hbAmt).filterNot { it.isNullOrBlank() }.joinToString(" — ").ifBlank { null })

                val measures = mutableListOf<String>()
                if (d["measureRHU"]             == true) measures.add("RHU (Rural Health Unit)")
                if (d["measureCommunityHealer"] == true) measures.add("Community Healer")
                if (d["measurePrivateWorker"]   == true) measures.add("Private Worker")
                if (d["measureSelfMedication"]  == true) measures.add("Self Medication")
                if (d["measureNone"]            == true) measures.add("None")
                set(R.id.tvMeasuresSick, measures.joinToString(", ").ifBlank { null })

                // ── Section IV — Nutrition ──
                val fc1 = d["foodFirstChoice"]  as? String ?: ""
                val fc2 = d["foodSecondChoice"] as? String ?: ""
                set(R.id.tvFoodChoice, listOf(fc1, fc2).filter { it.isNotBlank() }.joinToString(" / ").ifBlank { null })

                val s1 = d["servings1"] as? String ?: ""
                val s2 = d["servings2"] as? String ?: ""
                set(R.id.tvFoodServings, listOf(s1, s2).filter { it.isNotBlank() }.joinToString(" / ").ifBlank { null })

                set(R.id.tvFoodPrepFreq,    d["foodPrepFreq"]    as? String)
                set(R.id.tvFoodPreparation, d["foodPreparation"] as? String)

                val storage = mutableListOf<String>()
                if (d["storageRefrigerator"] == true) storage.add("Refrigerator")
                if (d["storageCabinet"]      == true) storage.add("Cabinet")
                if (d["storageTable"]        == true) storage.add("Table")
                if (d["storageBasket"]       == true) storage.add("Basket")
                set(R.id.tvFoodStorage, storage.joinToString(", ").ifBlank { null })

                val foodBuy = mutableListOf<String>()
                if (d["foodCarinderia"] == true) foodBuy.add("Carinderia")
                if (d["foodRestaurant"] == true) foodBuy.add("Restaurant")
                if (d["foodCart"]       == true) foodBuy.add("Food Cart")
                val fbOthers = d["foodBuyOthers"] as? String
                if (!fbOthers.isNullOrBlank()) foodBuy.add("Others: $fbOthers")
                set(R.id.tvFoodBuy, foodBuy.joinToString(", ").ifBlank { null })

                val buyReasons = mutableListOf<String>()
                if (d["foodCheaper"]   == true) buyReasons.add("Cheaper")
                if (d["foodConvenient"]== true) buyReasons.add("Convenient")
                if (d["foodVariety"]   == true) buyReasons.add("Variety")
                if (d["foodHealthy"]   == true) buyReasons.add("Healthy")
                set(R.id.tvFoodBuyReason, buyReasons.joinToString(", ").ifBlank { null })

                val fuel = mutableListOf<String>()
                if (d["cookGasStove"]      == true) fuel.add("Gas Stove")
                if (d["cookElectricStove"] == true) fuel.add("Electric Stove")
                if (d["cookFirewood"]      == true) fuel.add("Firewood")
                val cookOthers = d["cookOthers"] as? String
                if (!cookOthers.isNullOrBlank()) fuel.add("Others: $cookOthers")
                set(R.id.tvCookingFuel, fuel.joinToString(", ").ifBlank { null })

                val unhealthy = buildString {
                    val cf = d["cannedFood"]           as? String; if (!cf.isNullOrBlank()) append("Canned: $cf")
                    val cb = d["carbonatedBeverages"]  as? String; if (!cb.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Carbonated: $cb") }
                    val gf = d["grilledFood"]          as? String; if (!gf.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Grilled: $gf") }
                }
                set(R.id.tvUnhealthyFood, unhealthy.ifBlank { null })

                // ── Section V — Environmental Health ──
                set(R.id.tvHomeOwnership, d["homeOwnership"] as? String)
                set(R.id.tvConstruction,  d["construction"]  as? String)
                set(R.id.tvNumRooms,      d["numRooms"]      as? String)
                set(R.id.tvSpaceAdequacy, d["spaceAdequacy"] as? String)
                set(R.id.tvVentilation,   d["ventilation"]   as? String)

                val lighting = buildString {
                    val la = d["lightingAdequacy"] as? String; if (!la.isNullOrBlank()) append("Adequacy: $la")
                    val lf = d["lightingFacility"] as? String; if (!lf.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Facility: $lf") }
                    val lo = d["lightingOthers"]   as? String; if (!lo.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Others: $lo") }
                }
                set(R.id.tvLighting, lighting.ifBlank { null })

                val waterSrc = buildString {
                    val dr = d["waterSourceDrinking"] as? String; if (!dr.isNullOrBlank()) append("Drinking: $dr")
                    val ck = d["waterSourceCooking"]  as? String; if (!ck.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Cooking: $ck") }
                    val bt = d["waterSourceBathing"]  as? String; if (!bt.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Bathing: $bt") }
                }
                set(R.id.tvWaterSource, waterSrc.ifBlank { null })

                val waterDet = buildString {
                    val own = d["waterOwnership"]         as? String; if (!own.isNullOrBlank()) append("Ownership: $own")
                    val pot = d["waterPotable"]           as? String; if (!pot.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Potable: $pot") }
                    val sto = d["waterStorage"]           as? String; if (!sto.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Storage: $sto") }
                    val dis = d["waterSourceDistance"]    as? String; if (!dis.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Distance: $dis") }
                }
                set(R.id.tvWaterDetails, waterDet.ifBlank { null })

                val toilet = buildString {
                    val tt = d["toiletType"]      as? String; if (!tt.isNullOrBlank()) append("Type: $tt")
                    val to = d["toiletOwnership"] as? String; if (!to.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Ownership: $to") }
                    val san= d["sanitation"]      as? String; if (!san.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Sanitation: $san") }
                }
                set(R.id.tvToilet, toilet.ifBlank { null })

                val kitchDrain = buildString {
                    val ks = d["kitchenSanitation"] as? String; if (!ks.isNullOrBlank()) append("Kitchen: $ks")
                    val ds = d["drainageSystem"]    as? String; if (!ds.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Drainage System: $ds") }
                    val dc = d["drainageCondition"] as? String; if (!dc.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Condition: $dc") }
                }
                set(R.id.tvKitchenDrainage, kitchDrain.ifBlank { null })

                val waste = mutableListOf<String>()
                if (d["wasteCollected"]    == true) waste.add("Garbage Collection")
                if (d["wasteComposting"]   == true) waste.add("Composting")
                if (d["wasteHogFeeding"]   == true) waste.add("Hog Feeding")
                if (d["wasteBurialPit"]    == true) waste.add("Burial in Pit")
                if (d["wasteOpenBurning"]  == true) waste.add("Open Burning")
                if (d["wasteOpenDumping"]  == true) waste.add("Open Dumping")
                set(R.id.tvWasteDisposal, waste.joinToString(", ").ifBlank { null })

                set(R.id.tvWasteSegregation, d["wasteSegregation"] as? String)

                val garbageSto = mutableListOf<String>()
                if (d["garbageStoragePracticed"] == true) garbageSto.add("Practiced")
                set(R.id.tvGarbageStorage, garbageSto.joinToString(", ").ifBlank { d["garbageStorage"] as? String })

                val animals = buildString {
                    val k = d["animal1Kind"]         as? String; if (!k.isNullOrBlank()) append("Kind: $k")
                    val n = d["animal1Number"]       as? String; if (!n.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("No.: $n") }
                    val w = d["animal1KeptWhere"]    as? String; if (!w.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Kept: $w") }
                    val v = d["animal1Vaccination"]  as? String; if (!v.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Vaccination: $v") }
                    val rb= d["rabiesAnimals"]       as? String; if (!rb.isNullOrBlank()) { if (isNotEmpty()) append(" | "); append("Rabies: $rb") }
                }
                set(R.id.tvAnimals, animals.ifBlank { null })


                // ── Section VI — Other Data ──
                set(R.id.tvSuppliesAvailability, d["suppliesAvailability"] as? String)
                set(R.id.tvBallotSystem,         d["ballotSystem"]         as? String)
            }
    }

    private fun set(viewId: Int, value: String?) {
        findViewById<TextView>(viewId)?.text = value?.ifBlank { "—" } ?: "—"
    }
}