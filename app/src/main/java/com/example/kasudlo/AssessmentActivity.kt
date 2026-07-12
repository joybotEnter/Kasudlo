package com.example.kasudlo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AssessmentActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var intentPatientUid: String = ""

    private lateinit var rgGarbageDisposal: RadioGroup

    private lateinit var rgAnimal1RabiesCarrier: RadioGroup
    private lateinit var rgAnimal2Vaccination: RadioGroup
    private lateinit var rgAnimal2RabiesCarrier: RadioGroup
    private lateinit var etAnimal2Kind: EditText
    private lateinit var etAnimal2Heads: EditText

    private lateinit var rgHousingCongestionObservation: RadioGroup

    // ── Header ───────────────────────────────────────────────
    private lateinit var btnBack: LinearLayout
    private lateinit var btnSave: Button
    private lateinit var etControlNo: EditText
    // ── Survey Header Radio Groups ────────────────────────────
    private lateinit var rgRelationToHead2: RadioGroup
    private lateinit var rgGender2: RadioGroup
    private lateinit var rgMaritalStatus2: RadioGroup
    private lateinit var rgEducationalAttainment2: RadioGroup
    private lateinit var etNumberOfFamily: EditText
    private lateinit var etAddress: EditText
    private lateinit var etInformant: EditText
    private lateinit var etSurveyedBy: EditText
    private lateinit var etDateFirstVisit: EditText

    private lateinit var rgSafetyPrecautions: RadioGroup
    private lateinit var etBmiRemarks: EditText
    private lateinit var etWcHcRemarks: EditText
    private lateinit var etMuacRemarks: EditText

    private lateinit var rgKitchenSanitation2: RadioGroup

    private lateinit var etDateSecondVisit: EditText
    private lateinit var etDateThirdVisit: EditText
    private lateinit var etTimeStarted: EditText
    private lateinit var etTimeFinished: EditText
    private lateinit var etStatusLastVisit: EditText

    // ── Member 1 ─────────────────────────────────────────────
    private lateinit var etMember1Name: EditText
    private lateinit var etMember1Relation: EditText
    private lateinit var etMember1Gender: EditText
    private lateinit var etMember1Age: EditText
    private lateinit var etMember1Birthdate: EditText
    private lateinit var etMember1MaritalStatus: EditText
    private lateinit var etMember1Religion: EditText
    private lateinit var etMember1Education: EditText
    private lateinit var etMember1OccupationType: EditText
    private lateinit var etMember1PlaceOfWork: EditText
    private lateinit var etMember1LengthOfResidence: EditText
    // Member 1 dialect uses etDialect (original shared field in XML)

    // ── Member 2 ─────────────────────────────────────────────
    private lateinit var etMember2Name: EditText
    private lateinit var etMember2Relation: EditText
    private lateinit var etMember2Gender: EditText
    private lateinit var etMember2Age: EditText
    private lateinit var etMember2Birthdate: EditText
    private lateinit var etMember2MaritalStatus: EditText
    private lateinit var etMember2Religion: EditText
    private lateinit var etMember2Education: EditText
    private lateinit var etMember2OccupationType: EditText
    private lateinit var etMember2PlaceOfWork: EditText
    private lateinit var etMember2LengthOfResidence: EditText
    private lateinit var etMember2Dialect: EditText

    // ── Member 3 ─────────────────────────────────────────────
    private lateinit var etMember3Name: EditText
    private lateinit var etMember3Relation: EditText
    private lateinit var etMember3Gender: EditText
    private lateinit var etMember3Age: EditText
    private lateinit var etMember3Birthdate: EditText
    private lateinit var etMember3MaritalStatus: EditText
    private lateinit var etMember3Religion: EditText
    private lateinit var etMember3Education: EditText
    private lateinit var etMember3OccupationType: EditText
    private lateinit var etMember3PlaceOfWork: EditText
    private lateinit var etMember3LengthOfResidence: EditText
    private lateinit var etMember3Dialect: EditText

    // ── Member 4 ─────────────────────────────────────────────
    private lateinit var etMember4Name: EditText
    private lateinit var etMember4Relation: EditText
    private lateinit var etMember4Gender: EditText
    private lateinit var etMember4Age: EditText
    private lateinit var etMember4Birthdate: EditText
    private lateinit var etMember4MaritalStatus: EditText
    private lateinit var etMember4Religion: EditText
    private lateinit var etMember4Education: EditText
    private lateinit var etMember4OccupationType: EditText
    private lateinit var etMember4PlaceOfWork: EditText
    private lateinit var etMember4LengthOfResidence: EditText
    private lateinit var etMember4Dialect: EditText

    // ── Member 5 ─────────────────────────────────────────────
    private lateinit var etMember5Name: EditText
    private lateinit var etMember5Relation: EditText
    private lateinit var etMember5Gender: EditText
    private lateinit var etMember5Age: EditText
    private lateinit var etMember5Birthdate: EditText
    private lateinit var etMember5MaritalStatus: EditText
    private lateinit var etMember5Religion: EditText
    private lateinit var etMember5Education: EditText
    private lateinit var etMember5OccupationType: EditText
    private lateinit var etMember5PlaceOfWork: EditText
    private lateinit var etMember5LengthOfResidence: EditText
    private lateinit var etMember5Dialect: EditText

    // etDialect = Member 1's dialect field (R.id.etDialect in XML)
    private lateinit var etDialect: EditText

    // ── Family type ──────────────────────────────────────────
    private lateinit var cbNuclear: CheckBox
    private lateinit var cbExtended: CheckBox
    private lateinit var cbDyad: CheckBox
    private lateinit var cbHomosexual: CheckBox
    private lateinit var cbCohabiting: CheckBox
    private lateinit var cbBlended: CheckBox
    private lateinit var cbWithGrandparents: CheckBox
    private lateinit var cbSingleParent: CheckBox
    private lateinit var rgFamilyPower: RadioGroup
    private lateinit var rgFamilyResidence: RadioGroup
    private lateinit var rgFamilyDescent: RadioGroup
    private lateinit var rgFoodStorage2: RadioGroup

    // ── Community ────────────────────────────────────────────
    private lateinit var cbReligiousServices: CheckBox
    private lateinit var cbLivelihoodServices: CheckBox
    private lateinit var cbHealthServices: CheckBox
    private lateinit var cbGarbageCollection: CheckBox
    private lateinit var cbPeaceAndOrder: CheckBox
    private lateinit var cbCommOthers: CheckBox
    private lateinit var etCommOthers: EditText
    private lateinit var cbBrgyHall: CheckBox
    private lateinit var cbHealthStation: CheckBox
    private lateinit var cbChurch: CheckBox
    private lateinit var cbSchool: CheckBox
    private lateinit var cbSeniorCitizen: CheckBox
    private lateinit var cbYouth: CheckBox
    private lateinit var cbOrganizationsOthers: CheckBox
    private lateinit var etOrganizationsOthers: EditText
    private lateinit var cbBayanihan: CheckBox
    private lateinit var cbPalabraDeHonor: CheckBox
    private lateinit var cbPakikisama: CheckBox
    private lateinit var cbNingasKugon: CheckBox
    private lateinit var cbFiestas: CheckBox
    private lateinit var cbCloseFamilyTies: CheckBox
    private lateinit var cbRespectForElderly: CheckBox
    private lateinit var cbTraditionOthers: CheckBox
    private lateinit var etTraditionOthers: EditText
    private lateinit var cbRecreationCourt: CheckBox
    private lateinit var cbPlayground: CheckBox
    private lateinit var cbPlaza: CheckBox
    private lateinit var cbRecreationOthers: CheckBox
    private lateinit var etRecreationOthers: EditText
    private lateinit var cbTricycle: CheckBox
    private lateinit var cbJeep: CheckBox
    private lateinit var cbPujPuv: CheckBox
    private lateinit var cbBicycle: CheckBox
    private lateinit var cbPrivateVehicle: CheckBox
    private lateinit var cbPostalSystem: CheckBox
    private lateinit var cbInternet: CheckBox
    private lateinit var cbTelephone: CheckBox
    private lateinit var cbCellPhone: CheckBox
    private lateinit var cbTwoWayRadio: CheckBox

    // ── Economic ─────────────────────────────────────────────
    private lateinit var etEarner1Position: EditText
    private lateinit var etEarner1Income: EditText
    private lateinit var etEarner2Position: EditText
    private lateinit var etEarner2Income: EditText
    private lateinit var etEarner3Position: EditText
    private lateinit var etEarner3Income: EditText
    private lateinit var etEarner4Position: EditText
    private lateinit var etEarner4Income: EditText
    private lateinit var spinnerMonthlyIncome: Spinner
    private lateinit var cbFinanceEmployment: CheckBox
    private lateinit var cbFinanceBusiness: CheckBox
    private lateinit var cbFinancePension: CheckBox
    private lateinit var cbFinanceRelatives: CheckBox
    private lateinit var cbFinanceOthers: CheckBox
    private lateinit var rgIncomeAdequacy: RadioGroup
    private lateinit var etHealthBudgetAmount: EditText

    // ── Housing ──────────────────────────────────────────────
    private lateinit var rgHomeOwnership: RadioGroup
    private lateinit var rgConstruction: RadioGroup
    private lateinit var rgNumRooms: RadioGroup
    private lateinit var rgHousingCongestion: RadioGroup
    private lateinit var rgSpaceAdequacy: RadioGroup
    private lateinit var rgLightingFacility: RadioGroup

    private lateinit var rgLightingAdequacy: RadioGroup
    private lateinit var rgVentilation: RadioGroup
    private lateinit var rgSanitation: RadioGroup

    // ── Water ────────────────────────────────────────────────
    private lateinit var rgWaterOwnership: RadioGroup
    private lateinit var rgWaterSource: RadioGroup

    // ── Food storage / cooking ───────────────────────────────
    private lateinit var rgFoodStorage: RadioGroup
    private lateinit var cbStorageRefrigerator: CheckBox
    private lateinit var cbStorageCabinet: CheckBox
    private lateinit var cbStorageBasket: CheckBox
    private lateinit var cbStorageTable: CheckBox
    private lateinit var cbCookElectricStove: CheckBox
    private lateinit var cbCookGasStove: CheckBox
    private lateinit var cbCookFirewood: CheckBox
    private lateinit var cbCookOthers: CheckBox
    private lateinit var etCookOthers: EditText

    // ── Waste ────────────────────────────────────────────────
    private lateinit var rgGarbageStorage: RadioGroup
    private lateinit var rgWasteSegregation: RadioGroup
    private lateinit var cbHogFeeding: CheckBox
    private lateinit var cbOpenDumping: CheckBox
    private lateinit var cbBurialInPit: CheckBox
    private lateinit var cbCollected: CheckBox
    private lateinit var cbComposting: CheckBox
    private lateinit var cbOpenBurning: CheckBox

    // ── Toilet / drainage ────────────────────────────────────
    private lateinit var rgToiletOwnership: RadioGroup
    private lateinit var rgToiletType: RadioGroup
    private lateinit var rgDrainageSystem: RadioGroup
    private lateinit var rgDrainageCondition: RadioGroup

    // ── Animals ───────────────────────────────────────────────
    private lateinit var rgRabiesAnimals: RadioGroup
    private lateinit var etAnimal1Kind: EditText
    private lateinit var etAnimal1Number: EditText
    private lateinit var etAnimal1KeptWhere: EditText
    private lateinit var rgAnimal1Vaccination: RadioGroup

    // ── Health & Illness ─────────────────────────────────────
    private lateinit var rgSmoker: RadioGroup
    private lateinit var etSmokerFrequency: EditText
    private lateinit var etSmokerName: EditText
    private lateinit var etSmokerAge: EditText
    private lateinit var etSmokerAgeStarted: EditText
    private lateinit var etSmokerReason: EditText
    private lateinit var rgDrugs: RadioGroup
    private lateinit var etDrugTypesUsed: EditText
    private lateinit var etDrugName: EditText
    private lateinit var etDrugAge: EditText
    private lateinit var etDrugAgeStarted: EditText
    private lateinit var etDrugReason: EditText
    private lateinit var rgAlcohol: RadioGroup
    private lateinit var etAlcoholName: EditText
    private lateinit var etAlcoholAge: EditText
    private lateinit var etAlcoholAgeStarted: EditText
    private lateinit var etAlcoholFrequency: EditText
    private lateinit var etAlcoholReason: EditText

    // ── Anthropometrics ──────────────────────────────────────
    private lateinit var etAnthrName: EditText
    private lateinit var etAnthrAge: EditText
    private lateinit var etAnthrWeight: EditText
    private lateinit var etAnthrHeight: EditText
    private lateinit var etAnthrBmi: EditText
    private lateinit var etAnthrMuac: EditText
    private lateinit var etWaistCircumference: EditText
    private lateinit var etHipCircumference: EditText
    private lateinit var etWaistHipRatio: EditText

    // ── Food choices ─────────────────────────────────────────
    private lateinit var rgFoodChoice: RadioGroup
    private lateinit var rgServings1: RadioGroup
    private lateinit var rgFoodChoice2: RadioGroup
    private lateinit var rgServings2: RadioGroup
    private lateinit var rgFoodPrepFreq: RadioGroup
    private lateinit var rgFoodPreparation: RadioGroup
    private lateinit var cbFoodRestaurant: CheckBox
    private lateinit var cbFoodCarinderia: CheckBox
    private lateinit var cbFoodCart: CheckBox
    private lateinit var cbFoodBuyOthers: CheckBox
    private lateinit var etFoodBuyOthers: EditText
    private lateinit var cbFoodConvenient: CheckBox
    private lateinit var cbFoodCheaper: CheckBox
    private lateinit var cbFoodHealthy: CheckBox
    private lateinit var cbFoodVariety: CheckBox
    private lateinit var rgCannedFood: RadioGroup
    private lateinit var rgGrilledFood: RadioGroup
    private lateinit var rgCarbonatedBeverages: RadioGroup

    // ── Beliefs & Practices ──────────────────────────────────
    private lateinit var cbConsultDoctor: CheckBox
    private lateinit var cbConsultNurse: CheckBox
    private lateinit var cbConsultMidwife: CheckBox
    private lateinit var cbConsultHilot: CheckBox
    private lateinit var cbConsultAlbularyo: CheckBox
    private lateinit var cbConsultFaithHealer: CheckBox
    private lateinit var cbConsultElderly: CheckBox
    private lateinit var cbMeasurePrivateWorker: CheckBox
    private lateinit var cbMeasureCommunityHealer: CheckBox
    private lateinit var cbMeasureRHU: CheckBox
    private lateinit var cbMeasureSelfMedication: CheckBox
    private lateinit var cbMeasureNone: CheckBox
    private lateinit var cbMedPrescribed: CheckBox
    private lateinit var cbMedOTC: CheckBox
    private lateinit var cbMedHerbals: CheckBox
    private lateinit var cbMedOthers: CheckBox
    private lateinit var etMedOthers: EditText
    private lateinit var rgMedCheckup: RadioGroup
    private lateinit var rgDentalCheckup: RadioGroup

    // ── Community Health Programs ─────────────────────────────
    private lateinit var etBhcHealthServices: EditText
    private lateinit var etImmunizationName: EditText
    private lateinit var etImmunizationAge: EditText
    private lateinit var etImmunizationGender: EditText
    private lateinit var etImmunizationBcg: EditText
    private lateinit var etImmunizationDpt1: EditText
    private lateinit var etImmunizationDpt2: EditText
    private lateinit var etImmunizationDpt3: EditText
    private lateinit var etImmunizationHepa1: EditText
    private lateinit var etImmunizationHepa2: EditText
    private lateinit var etImmunizationHepa3: EditText
    private lateinit var etImmunizationOpv1: EditText
    private lateinit var etImmunizationOpv2: EditText
    private lateinit var etImmunizationOpv3: EditText
    private lateinit var etImmunizationMeasles: EditText
    private lateinit var etImmunizationCompleteAge: EditText
    private lateinit var etImmunizationIncomplete: EditText
    private lateinit var etImmunizationFic: EditText
    private lateinit var etAntenatalName: EditText
    private lateinit var etAntenatalAog: EditText
    private lateinit var rgAntenatalCheckup: RadioGroup
    private lateinit var rgAntenatalTetanus: RadioGroup

    // ── Family Planning ──────────────────────────────────────
    private lateinit var rgFamilyPlanning: RadioGroup
    private lateinit var cbFpReasonGoodHealth: CheckBox
    private lateinit var cbFpReasonPersonalBelief: CheckBox
    private lateinit var cbFpReasonReligious: CheckBox
    private lateinit var cbFpReasonInfluence: CheckBox
    private lateinit var cbFpReasonOthers: CheckBox
    private lateinit var etFpReasonOthers: EditText
    private lateinit var cbFpNonReasonBadHealth: CheckBox
    private lateinit var cbFpNonReasonPersonalBelief: CheckBox
    private lateinit var cbFpNonReasonReligious: CheckBox
    private lateinit var cbFpNonReasonInfluence: CheckBox
    private lateinit var cbFpNonReasonOthers: CheckBox
    private lateinit var etFpNonReasonOthers: EditText
    private lateinit var cbFpBtl: CheckBox
    private lateinit var cbFpVasectomy: CheckBox
    private lateinit var cbFpPills: CheckBox
    private lateinit var cbFpIud: CheckBox
    private lateinit var cbFpInjectable: CheckBox
    private lateinit var cbFpCondoms: CheckBox
    private lateinit var cbFpImplant: CheckBox
    private lateinit var cbFpBillings: CheckBox
    private lateinit var cbFpBbt: CheckBox
    private lateinit var cbFpSymptoThermal: CheckBox
    private lateinit var cbFpSdm: CheckBox
    private lateinit var cbFpLam: CheckBox

    // ── Health Indicators ────────────────────────────────────
    private lateinit var etMorbidityName: EditText
    private lateinit var etMorbidityAge: EditText
    private lateinit var etMorbidityGender: EditText
    private lateinit var etMorbidityCause: EditText
    private lateinit var rgMorbidityIntervention: RadioGroup
    private lateinit var rgMorbidityAdmitted: RadioGroup
    private lateinit var etMortalityName: EditText
    private lateinit var etMortalityAge: EditText
    private lateinit var etMortalityGender: EditText
    private lateinit var etMortalityCause: EditText
    private lateinit var etNcdName: EditText
    private lateinit var etNcdAge: EditText
    private lateinit var etNcdGender: EditText
    private lateinit var etNcdType: EditText
    private lateinit var etCdName: EditText
    private lateinit var etCdAge: EditText
    private lateinit var etCdGender: EditText
    private lateinit var etCdType: EditText
    private lateinit var etBpName: EditText
    private lateinit var etBpAge: EditText
    private lateinit var etBpGender: EditText
    private lateinit var etBpReading: EditText
    private lateinit var rgBhcAwareness: RadioGroup

    // ── Health Resource ──────────────────────────────────────
    private lateinit var etBhcSchedule: EditText
    private lateinit var etRhuPhysician: EditText
    private lateinit var etRhuNurse: EditText
    private lateinit var etBhcMidwife: EditText
    private lateinit var rgSuppliesAvailability: RadioGroup

    // ── Political ────────────────────────────────────────────
    private lateinit var cbLeaderCaptain: CheckBox
    private lateinit var cbLeaderKagawad: CheckBox
    private lateinit var cbLeaderElderly: CheckBox
    private lateinit var cbLeaderBhw: CheckBox
    private lateinit var cbLeaderInfluential: CheckBox
    private lateinit var cbLeaderReligious: CheckBox
    private lateinit var cbLeaderNeighbor: CheckBox
    private lateinit var cbConflictGossip: CheckBox
    private lateinit var cbConflictFamilyConflict: CheckBox
    private lateinit var cbConflictDrugs: CheckBox
    private lateinit var cbConflictRiot: CheckBox
    private lateinit var cbConflictAlcohol: CheckBox
    private lateinit var cbConflictOthers: CheckBox
    private lateinit var etConflictOthers: EditText
    private lateinit var cbResolutionSettlement: CheckBox
    private lateinit var cbResolutionBrgyHearing: CheckBox
    private lateinit var cbResolutionPolice: CheckBox
    private lateinit var cbResolutionOthers: CheckBox
    private lateinit var etResolutionOthers: EditText

    private lateinit var etConcernsSuggestions: EditText

    private fun showDatePicker(field: EditText) {
        val cal = java.util.Calendar.getInstance()
        android.app.DatePickerDialog(
            this,
            { _, y, m, d -> field.setText("%02d/%02d/%04d".format(m + 1, d, y)) },
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH),
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(field: EditText) {
        val cal = java.util.Calendar.getInstance()
        android.app.TimePickerDialog(
            this,
            { _, h, m -> field.setText("%02d:%02d".format(h, m)) },
            cal.get(java.util.Calendar.HOUR_OF_DAY),
            cal.get(java.util.Calendar.MINUTE),
            false
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment)

        supportActionBar?.title = "Community Survey"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        intentPatientUid = intent.getStringExtra("patientUid") ?: ""

        bindViews()
        setupSpinner()

        btnBack.setOnClickListener { finish() }
        btnSave.setOnClickListener { saveForm() }

        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    val role = doc.getString("role") ?: "patient"
                    if (role == "patient") {
                        applyPatientRestrictions()
                    }
                }
            val etControlNo = findViewById<EditText>(R.id.etControlNo)
            etControlNo.isEnabled = true
            etControlNo.isFocusable = true
            etControlNo.isFocusableInTouchMode = true
            etControlNo.isClickable = true
        }
    }

    private fun applyPatientRestrictions() {
        val nurseOnlyFields = listOf(
            etControlNo, etSurveyedBy, etDateFirstVisit, etDateSecondVisit,
            etDateThirdVisit, etTimeStarted, etTimeFinished, etStatusLastVisit
        )
        nurseOnlyFields.forEach { field ->
            field.isEnabled = false
            field.alpha = 0.5f
        }
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        etControlNo = findViewById(R.id.etControlNo)

        val etControlNo = findViewById<EditText>(R.id.etControlNo)
        etControlNo.setOnClickListener { etControlNo.requestFocus() }
        etControlNo.setOnFocusChangeListener { _, hasFocus ->
            android.util.Log.d("CONTROL_NO", "Focused = $hasFocus")
        }

        etNumberOfFamily = findViewById(R.id.etNumberOfFamily)
        etAddress        = findViewById(R.id.etAddress)
        etInformant      = findViewById(R.id.etInformant)

        rgRelationToHead2        = findViewById(R.id.rgRelationToHead2)
        rgGender2                = findViewById(R.id.rgGender2)
        rgMaritalStatus2         = findViewById(R.id.rgMaritalStatus2)
        rgEducationalAttainment2 = findViewById(R.id.rgEducationalAttainment2)

        etSurveyedBy     = findViewById(R.id.etSurveyedBy)
        etDateFirstVisit  = findViewById(R.id.etDateFirstVisit)
        etDateSecondVisit = findViewById(R.id.etDateSecondVisit)
        etDateThirdVisit  = findViewById(R.id.etDateThirdVisit)
        etTimeStarted    = findViewById(R.id.etTimeStarted)
        etTimeFinished   = findViewById(R.id.etTimeFinished)
        etStatusLastVisit = findViewById(R.id.etStatusLastVisit)

        // ── Member 1
        etMember1Name             = findViewById(R.id.etMember1Name)
        etMember1Relation         = findViewById(R.id.etMember1Relation)
        etMember1Gender           = findViewById(R.id.etMember1Gender)
        etMember1Age              = findViewById(R.id.etMember1Age)
        etMember1Birthdate        = findViewById(R.id.etMember1Birthdate)
        etMember1MaritalStatus    = findViewById(R.id.etMember1MaritalStatus)
        etMember1Religion         = findViewById(R.id.etMember1Religion)
        etMember1Education        = findViewById(R.id.etMember1Education)
        etMember1OccupationType   = findViewById(R.id.etMember1OccupationType)
        etMember1PlaceOfWork      = findViewById(R.id.etMember1PlaceOfWork)
        etMember1LengthOfResidence = findViewById(R.id.etMember1LengthOfResidence)
        // Member 1 dialect → etDialect (bound below)

        // ── Member 2
        etMember2Name             = findViewById(R.id.etMember2Name)
        etMember2Relation         = findViewById(R.id.etMember2Relation)
        etMember2Gender           = findViewById(R.id.etMember2Gender)
        etMember2Age              = findViewById(R.id.etMember2Age)
        etMember2Birthdate        = findViewById(R.id.etMember2Birthdate)
        etMember2MaritalStatus    = findViewById(R.id.etMember2MaritalStatus)
        etMember2Religion         = findViewById(R.id.etMember2Religion)
        etMember2Education        = findViewById(R.id.etMember2Education)
        etMember2OccupationType   = findViewById(R.id.etMember2OccupationType)
        etMember2PlaceOfWork      = findViewById(R.id.etMember2PlaceOfWork)
        etMember2LengthOfResidence = findViewById(R.id.etMember2LengthOfResidence)
        etMember2Dialect          = findViewById(R.id.etMember2Dialect)

        // ── Member 3
        etMember3Name             = findViewById(R.id.etMember3Name)
        etMember3Relation         = findViewById(R.id.etMember3Relation)
        etMember3Gender           = findViewById(R.id.etMember3Gender)
        etMember3Age              = findViewById(R.id.etMember3Age)
        etMember3Birthdate        = findViewById(R.id.etMember3Birthdate)
        etMember3MaritalStatus    = findViewById(R.id.etMember3MaritalStatus)
        etMember3Religion         = findViewById(R.id.etMember3Religion)
        etMember3Education        = findViewById(R.id.etMember3Education)
        etMember3OccupationType   = findViewById(R.id.etMember3OccupationType)
        etMember3PlaceOfWork      = findViewById(R.id.etMember3PlaceOfWork)
        etMember3LengthOfResidence = findViewById(R.id.etMember3LengthOfResidence)
        etMember3Dialect          = findViewById(R.id.etMember3Dialect)



        // ── Member 4
        etMember4Name             = findViewById(R.id.etMember4Name)
        etMember4Relation         = findViewById(R.id.etMember4Relation)
        etMember4Gender           = findViewById(R.id.etMember4Gender)
        etMember4Age              = findViewById(R.id.etMember4Age)
        etMember4Birthdate        = findViewById(R.id.etMember4Birthdate)
        etMember4MaritalStatus    = findViewById(R.id.etMember4MaritalStatus)
        etMember4Religion         = findViewById(R.id.etMember4Religion)
        etMember4Education        = findViewById(R.id.etMember4Education)
        etMember4OccupationType   = findViewById(R.id.etMember4OccupationType)
        etMember4PlaceOfWork      = findViewById(R.id.etMember4PlaceOfWork)
        etMember4LengthOfResidence = findViewById(R.id.etMember4LengthOfResidence)
        etMember4Dialect          = findViewById(R.id.etMember4Dialect)

        // ── Member 5
        etMember5Name             = findViewById(R.id.etMember5Name)
        etMember5Relation         = findViewById(R.id.etMember5Relation)
        etMember5Gender           = findViewById(R.id.etMember5Gender)
        etMember5Age              = findViewById(R.id.etMember5Age)
        etMember5Birthdate        = findViewById(R.id.etMember5Birthdate)
        etMember5MaritalStatus    = findViewById(R.id.etMember5MaritalStatus)
        etMember5Religion         = findViewById(R.id.etMember5Religion)
        etMember5Education        = findViewById(R.id.etMember5Education)
        etMember5OccupationType   = findViewById(R.id.etMember5OccupationType)
        etMember5PlaceOfWork      = findViewById(R.id.etMember5PlaceOfWork)
        etMember5LengthOfResidence = findViewById(R.id.etMember5LengthOfResidence)
        etMember5Dialect          = findViewById(R.id.etMember5Dialect)

        // Member 1 dialect field (shared original XML field)
        etDialect = findViewById(R.id.etDialect)

        cbNuclear           = findViewById(R.id.cbNuclear)
        cbExtended          = findViewById(R.id.cbExtended)
        cbDyad              = findViewById(R.id.cbDyad)
        cbHomosexual        = findViewById(R.id.cbHomosexual)
        cbCohabiting        = findViewById(R.id.cbCohabiting)

        rgSafetyPrecautions = findViewById(R.id.rgSafetyPrecautions)
        etBmiRemarks        = findViewById(R.id.etBmiRemarks)
        etWcHcRemarks       = findViewById(R.id.etWcHcRemarks)
        etMuacRemarks       = findViewById(R.id.etMuacRemarks)

        rgKitchenSanitation2 = findViewById(R.id.rgKitchenSanitation2)

        rgAnimal1RabiesCarrier = findViewById(R.id.rgAnimal1RabiesCarrier)
        rgAnimal2Vaccination   = findViewById(R.id.rgAnimal2Vaccination)
        rgAnimal2RabiesCarrier = findViewById(R.id.rgAnimal2RabiesCarrier)
        etAnimal2Kind          = findViewById(R.id.etAnimal2Kind)
        etAnimal2Heads         = findViewById(R.id.etAnimal2Heads)

        rgGarbageDisposal = findViewById(R.id.rgGarbageDisposal)
        rgFoodStorage2    = findViewById(R.id.rgFoodStorage2)

        cbBlended           = findViewById(R.id.cbBlended)
        cbWithGrandparents  = findViewById(R.id.cbWithGrandparents)
        cbSingleParent      = findViewById(R.id.cbSingleParent)
        rgFamilyPower       = findViewById(R.id.rgFamilyPower)
        rgFamilyResidence   = findViewById(R.id.rgFamilyResidence)
        rgFamilyDescent     = findViewById(R.id.rgFamilyDescent)
        cbReligiousServices  = findViewById(R.id.cbReligiousServices)
        cbLivelihoodServices = findViewById(R.id.cbLivelihoodServices)
        cbHealthServices     = findViewById(R.id.cbHealthServices)
        cbGarbageCollection  = findViewById(R.id.cbGarbageCollection)
        cbPeaceAndOrder      = findViewById(R.id.cbPeaceAndOrder)
        cbCommOthers         = findViewById(R.id.cbCommOthers)
        etCommOthers         = findViewById(R.id.etCommOthers)
        cbBrgyHall           = findViewById(R.id.cbBrgyHall)
        cbHealthStation      = findViewById(R.id.cbHealthStation)
        cbChurch             = findViewById(R.id.cbChurch)
        cbSchool             = findViewById(R.id.cbSchool)
        cbSeniorCitizen      = findViewById(R.id.cbSeniorCitizen)
        cbYouth              = findViewById(R.id.cbYouth)
        cbOrganizationsOthers = findViewById(R.id.cbOrganizationsOthers)
        etOrganizationsOthers = findViewById(R.id.etOrganizationsOthers)
        cbBayanihan          = findViewById(R.id.cbBayanihan)
        cbPalabraDeHonor     = findViewById(R.id.cbPalabraDeHonor)
        cbPakikisama         = findViewById(R.id.cbPakikisama)
        cbNingasKugon        = findViewById(R.id.cbNingasKugon)
        cbFiestas            = findViewById(R.id.cbFiestas)
        cbCloseFamilyTies    = findViewById(R.id.cbCloseFamilyTies)
        cbRespectForElderly  = findViewById(R.id.cbRespectForElderly)
        cbTraditionOthers    = findViewById(R.id.cbTraditionOthers)
        etTraditionOthers    = findViewById(R.id.etTraditionOthers)
        cbRecreationCourt    = findViewById(R.id.cbRecreationCourt)
        cbPlayground         = findViewById(R.id.cbPlayground)
        cbPlaza              = findViewById(R.id.cbPlaza)
        cbRecreationOthers   = findViewById(R.id.cbRecreationOthers)
        etRecreationOthers   = findViewById(R.id.etRecreationOthers)
        cbTricycle           = findViewById(R.id.cbTricycle)
        cbJeep               = findViewById(R.id.cbJeep)
        cbPujPuv             = findViewById(R.id.cbPujPuv)
        cbBicycle            = findViewById(R.id.cbBicycle)
        cbPrivateVehicle     = findViewById(R.id.cbPrivateVehicle)
        cbPostalSystem       = findViewById(R.id.cbPostalSystem)
        cbInternet           = findViewById(R.id.cbInternet)
        cbTelephone          = findViewById(R.id.cbTelephone)
        cbCellPhone          = findViewById(R.id.cbCellPhone)
        cbTwoWayRadio        = findViewById(R.id.cbTwoWayRadio)
        etEarner1Position    = findViewById(R.id.etEarner1Position)
        etEarner1Income      = findViewById(R.id.etEarner1Income)
        etEarner2Position    = findViewById(R.id.etEarner2Position)
        etEarner2Income      = findViewById(R.id.etEarner2Income)
        etEarner3Position    = findViewById(R.id.etEarner3Position)
        etEarner3Income      = findViewById(R.id.etEarner3Income)
        etEarner4Position    = findViewById(R.id.etEarner4Position)
        etEarner4Income      = findViewById(R.id.etEarner4Income)
        spinnerMonthlyIncome = findViewById(R.id.spinnerMonthlyIncome)
        cbFinanceEmployment  = findViewById(R.id.cbFinanceEmployment)
        cbFinanceBusiness    = findViewById(R.id.cbFinanceBusiness)
        cbFinancePension     = findViewById(R.id.cbFinancePension)
        cbFinanceRelatives   = findViewById(R.id.cbFinanceRelatives)
        cbFinanceOthers      = findViewById(R.id.cbFinanceOthers)
        rgIncomeAdequacy     = findViewById(R.id.rgIncomeAdequacy)
        etHealthBudgetAmount = findViewById(R.id.etHealthBudgetAmount)
        rgHomeOwnership      = findViewById(R.id.rgHomeOwnership)
        rgConstruction       = findViewById(R.id.rgConstruction)
        rgNumRooms           = findViewById(R.id.rgNumRooms)
        rgHousingCongestion  = findViewById(R.id.rgHousingCongestion)
        rgHousingCongestionObservation = findViewById(R.id.rgHousingCongestionObservation)
        rgSpaceAdequacy      = findViewById(R.id.rgSpaceAdequacy)
        rgLightingFacility   = findViewById(R.id.rgLightingFacility)
        rgLightingAdequacy   = findViewById(R.id.rgLightingAdequacy)
        rgVentilation        = findViewById(R.id.rgVentilation)
        rgSanitation         = findViewById(R.id.rgSanitation)
        rgWaterOwnership     = findViewById(R.id.rgWaterOwnership)
        rgWaterSource        = findViewById(R.id.rgWaterSource)

        // rgFoodStorage points to same view as rgFoodStorage2
        rgFoodStorage         = findViewById(R.id.rgFoodStorage2)
        cbStorageRefrigerator = findViewById(R.id.cbStorageRefrigerator)
        cbStorageCabinet      = findViewById(R.id.cbStorageCabinet)
        cbStorageBasket       = findViewById(R.id.cbStorageBasket)
        cbStorageTable        = findViewById(R.id.cbStorageTable)
        cbCookElectricStove   = findViewById(R.id.cbCookElectricStove)
        cbCookGasStove        = findViewById(R.id.cbCookGasStove)
        cbCookFirewood        = findViewById(R.id.cbCookFirewood)
        cbCookOthers          = findViewById(R.id.cbCookOthers)
        etCookOthers          = findViewById(R.id.etCookOthers)
        rgGarbageStorage      = findViewById(R.id.rgGarbageStorage)
        rgWasteSegregation    = findViewById(R.id.rgWasteSegregation)
        cbHogFeeding          = findViewById(R.id.cbHogFeeding)
        cbOpenDumping         = findViewById(R.id.cbOpenDumping)
        cbBurialInPit         = findViewById(R.id.cbBurialInPit)
        cbCollected           = findViewById(R.id.cbCollected)
        cbComposting          = findViewById(R.id.cbComposting)
        cbOpenBurning         = findViewById(R.id.cbOpenBurning)
        rgToiletOwnership     = findViewById(R.id.rgToiletOwnership)
        rgToiletType          = findViewById(R.id.rgToiletType)
        rgDrainageSystem      = findViewById(R.id.rgDrainageSystem)
        rgDrainageCondition   = findViewById(R.id.rgDrainageCondition)
        rgRabiesAnimals       = findViewById(R.id.rgRabiesAnimals)
        etAnimal1Kind         = findViewById(R.id.etAnimal1Kind)
        etAnimal1Number       = findViewById(R.id.etAnimal1Number)
        etAnimal1KeptWhere    = findViewById(R.id.etAnimal1KeptWhere)
        rgAnimal1Vaccination  = findViewById(R.id.rgAnimal1Vaccination)
        rgSmoker              = findViewById(R.id.rgSmoker)
        etSmokerFrequency     = findViewById(R.id.etSmokerFrequency)
        etSmokerName          = findViewById(R.id.etSmokerName)
        etSmokerAge           = findViewById(R.id.etSmokerAge)
        etSmokerAgeStarted    = findViewById(R.id.etSmokerAgeStarted)
        etSmokerReason        = findViewById(R.id.etSmokerReason)
        rgDrugs               = findViewById(R.id.rgDrugs)
        etDrugTypesUsed       = findViewById(R.id.etDrugTypesUsed)
        etDrugName            = findViewById(R.id.etDrugName)
        etDrugAge             = findViewById(R.id.etDrugAge)
        etDrugAgeStarted      = findViewById(R.id.etDrugAgeStarted)
        etDrugReason          = findViewById(R.id.etDrugReason)
        rgAlcohol             = findViewById(R.id.rgAlcohol)
        etAlcoholName         = findViewById(R.id.etAlcoholName)
        etAlcoholAge          = findViewById(R.id.etAlcoholAge)
        etAlcoholAgeStarted   = findViewById(R.id.etAlcoholAgeStarted)
        etAlcoholFrequency    = findViewById(R.id.etAlcoholFrequency)
        etAlcoholReason       = findViewById(R.id.etAlcoholReason)
        etAnthrName           = findViewById(R.id.etAnthrName)
        etAnthrAge            = findViewById(R.id.etAnthrAge)
        etAnthrWeight         = findViewById(R.id.etAnthrWeight)
        etAnthrHeight         = findViewById(R.id.etAnthrHeight)
        etAnthrBmi            = findViewById(R.id.etAnthrBmi)
        etAnthrMuac           = findViewById(R.id.etAnthrMuac)
        etWaistCircumference  = findViewById(R.id.etWaistCircumference)
        etHipCircumference    = findViewById(R.id.etHipCircumference)
        etWaistHipRatio       = findViewById(R.id.etWaistHipRatio)
        rgFoodChoice          = findViewById(R.id.rgFoodChoice)
        rgServings1           = findViewById(R.id.rgServings1)
        rgFoodChoice2         = findViewById(R.id.rgFoodChoice2)
        rgServings2           = findViewById(R.id.rgServings2)
        rgFoodPrepFreq        = findViewById(R.id.rgFoodPrepFreq)
        rgFoodPreparation     = findViewById(R.id.rgFoodPreparation)
        cbFoodRestaurant      = findViewById(R.id.cbFoodRestaurant)
        cbFoodCarinderia      = findViewById(R.id.cbFoodCarinderia)
        cbFoodCart            = findViewById(R.id.cbFoodCart)
        cbFoodBuyOthers       = findViewById(R.id.cbFoodBuyOthers)
        etFoodBuyOthers       = findViewById(R.id.etFoodBuyOthers)
        cbFoodConvenient      = findViewById(R.id.cbFoodConvenient)
        cbFoodCheaper         = findViewById(R.id.cbFoodCheaper)
        cbFoodHealthy         = findViewById(R.id.cbFoodHealthy)
        cbFoodVariety         = findViewById(R.id.cbFoodVariety)
        rgCannedFood          = findViewById(R.id.rgCannedFood)
        rgGrilledFood         = findViewById(R.id.rgGrilledFood)
        rgCarbonatedBeverages = findViewById(R.id.rgCarbonatedBeverages)
        cbConsultDoctor       = findViewById(R.id.cbConsultDoctor)
        cbConsultNurse        = findViewById(R.id.cbConsultNurse)
        cbConsultMidwife      = findViewById(R.id.cbConsultMidwife)
        cbConsultHilot        = findViewById(R.id.cbConsultHilot)
        cbConsultAlbularyo    = findViewById(R.id.cbConsultAlbularyo)
        cbConsultFaithHealer  = findViewById(R.id.cbConsultFaithHealer)
        cbConsultElderly      = findViewById(R.id.cbConsultElderly)
        cbMeasurePrivateWorker   = findViewById(R.id.cbMeasurePrivateWorker)
        cbMeasureCommunityHealer = findViewById(R.id.cbMeasureCommunityHealer)
        cbMeasureRHU             = findViewById(R.id.cbMeasureRHU)
        cbMeasureSelfMedication  = findViewById(R.id.cbMeasureSelfMedication)
        cbMeasureNone            = findViewById(R.id.cbMeasureNone)
        cbMedPrescribed       = findViewById(R.id.cbMedPrescribed)
        cbMedOTC              = findViewById(R.id.cbMedOTC)
        cbMedHerbals          = findViewById(R.id.cbMedHerbals)
        cbMedOthers           = findViewById(R.id.cbMedOthers)
        etMedOthers           = findViewById(R.id.etMedOthers)
        rgMedCheckup          = findViewById(R.id.rgMedCheckup)
        rgDentalCheckup       = findViewById(R.id.rgDentalCheckup)
        etBhcHealthServices   = findViewById(R.id.etBhcHealthServices)
        etImmunizationName    = findViewById(R.id.etImmunizationName)
        etImmunizationAge     = findViewById(R.id.etImmunizationAge)
        etImmunizationGender  = findViewById(R.id.etImmunizationGender)
        etImmunizationBcg     = findViewById(R.id.etImmunizationBcg)
        etImmunizationDpt1    = findViewById(R.id.etImmunizationDpt1)
        etImmunizationDpt2    = findViewById(R.id.etImmunizationDpt2)
        etImmunizationDpt3    = findViewById(R.id.etImmunizationDpt3)
        etImmunizationHepa1   = findViewById(R.id.etImmunizationHepa1)
        etImmunizationHepa2   = findViewById(R.id.etImmunizationHepa2)
        etImmunizationHepa3   = findViewById(R.id.etImmunizationHepa3)
        etImmunizationOpv1    = findViewById(R.id.etImmunizationOpv1)
        etImmunizationOpv2    = findViewById(R.id.etImmunizationOpv2)
        etImmunizationOpv3    = findViewById(R.id.etImmunizationOpv3)
        etImmunizationMeasles      = findViewById(R.id.etImmunizationMeasles)
        etImmunizationCompleteAge  = findViewById(R.id.etImmunizationCompleteAge)
        etImmunizationIncomplete   = findViewById(R.id.etImmunizationIncomplete)
        etImmunizationFic          = findViewById(R.id.etImmunizationFic)
        etAntenatalName       = findViewById(R.id.etAntenatalName)
        etAntenatalAog        = findViewById(R.id.etAntenatalAog)
        rgAntenatalCheckup    = findViewById(R.id.rgAntenatalCheckup)
        rgAntenatalTetanus    = findViewById(R.id.rgAntenatalTetanus)
        rgFamilyPlanning      = findViewById(R.id.rgFamilyPlanning)
        cbFpReasonGoodHealth       = findViewById(R.id.cbFpReasonGoodHealth)
        cbFpReasonPersonalBelief   = findViewById(R.id.cbFpReasonPersonalBelief)
        cbFpReasonReligious        = findViewById(R.id.cbFpReasonReligious)
        cbFpReasonInfluence        = findViewById(R.id.cbFpReasonInfluence)
        cbFpReasonOthers           = findViewById(R.id.cbFpReasonOthers)
        etFpReasonOthers           = findViewById(R.id.etFpReasonOthers)
        cbFpNonReasonBadHealth     = findViewById(R.id.cbFpNonReasonBadHealth)
        cbFpNonReasonPersonalBelief = findViewById(R.id.cbFpNonReasonPersonalBelief)
        cbFpNonReasonReligious     = findViewById(R.id.cbFpNonReasonReligious)
        cbFpNonReasonInfluence     = findViewById(R.id.cbFpNonReasonInfluence)
        cbFpNonReasonOthers        = findViewById(R.id.cbFpNonReasonOthers)
        etFpNonReasonOthers        = findViewById(R.id.etFpNonReasonOthers)
        cbFpBtl           = findViewById(R.id.cbFpBtl)
        cbFpVasectomy     = findViewById(R.id.cbFpVasectomy)
        cbFpPills         = findViewById(R.id.cbFpPills)
        cbFpIud           = findViewById(R.id.cbFpIud)
        cbFpInjectable    = findViewById(R.id.cbFpInjectable)
        cbFpCondoms       = findViewById(R.id.cbFpCondoms)
        cbFpImplant       = findViewById(R.id.cbFpImplant)
        cbFpBillings      = findViewById(R.id.cbFpBillings)
        cbFpBbt           = findViewById(R.id.cbFpBbt)
        cbFpSymptoThermal = findViewById(R.id.cbFpSymptoThermal)
        cbFpSdm           = findViewById(R.id.cbFpSdm)
        cbFpLam           = findViewById(R.id.cbFpLam)
        etMorbidityName   = findViewById(R.id.etMorbidityName)
        etMorbidityAge    = findViewById(R.id.etMorbidityAge)
        etMorbidityGender = findViewById(R.id.etMorbidityGender)
        etMorbidityCause  = findViewById(R.id.etMorbidityCause)
        rgMorbidityIntervention = findViewById(R.id.rgMorbidityIntervention)
        rgMorbidityAdmitted     = findViewById(R.id.rgMorbidityAdmitted)
        etMortalityName   = findViewById(R.id.etMortalityName)
        etMortalityAge    = findViewById(R.id.etMortalityAge)
        etMortalityGender = findViewById(R.id.etMortalityGender)
        etMortalityCause  = findViewById(R.id.etMortalityCause)
        etNcdName         = findViewById(R.id.etNcdName)
        etNcdAge          = findViewById(R.id.etNcdAge)
        etNcdGender       = findViewById(R.id.etNcdGender)
        etNcdType         = findViewById(R.id.etNcdType)
        etCdName          = findViewById(R.id.etCdName)
        etCdAge           = findViewById(R.id.etCdAge)
        etCdGender        = findViewById(R.id.etCdGender)
        etCdType          = findViewById(R.id.etCdType)
        etBpName          = findViewById(R.id.etBpName)
        etBpAge           = findViewById(R.id.etBpAge)
        etBpGender        = findViewById(R.id.etBpGender)
        etBpReading       = findViewById(R.id.etBpReading)
        rgBhcAwareness         = findViewById(R.id.rgBhcAwareness)
        etBhcSchedule          = findViewById(R.id.etBhcSchedule)
        etRhuPhysician         = findViewById(R.id.etRhuPhysician)
        etRhuNurse             = findViewById(R.id.etRhuNurse)
        etBhcMidwife           = findViewById(R.id.etBhcMidwife)
        rgSuppliesAvailability = findViewById(R.id.rgSuppliesAvailability)
        cbLeaderCaptain        = findViewById(R.id.cbLeaderCaptain)
        cbLeaderKagawad        = findViewById(R.id.cbLeaderKagawad)
        cbLeaderElderly        = findViewById(R.id.cbLeaderElderly)
        cbLeaderBhw            = findViewById(R.id.cbLeaderBhw)
        cbLeaderInfluential    = findViewById(R.id.cbLeaderInfluential)
        cbLeaderReligious      = findViewById(R.id.cbLeaderReligious)
        cbLeaderNeighbor       = findViewById(R.id.cbLeaderNeighbor)
        cbConflictGossip         = findViewById(R.id.cbConflictGossip)
        cbConflictFamilyConflict = findViewById(R.id.cbConflictFamilyConflict)
        cbConflictDrugs          = findViewById(R.id.cbConflictDrugs)
        cbConflictRiot           = findViewById(R.id.cbConflictRiot)
        cbConflictAlcohol        = findViewById(R.id.cbConflictAlcohol)
        cbConflictOthers         = findViewById(R.id.cbConflictOthers)
        etConflictOthers         = findViewById(R.id.etConflictOthers)
        cbResolutionSettlement  = findViewById(R.id.cbResolutionSettlement)
        cbResolutionBrgyHearing = findViewById(R.id.cbResolutionBrgyHearing)
        cbResolutionPolice      = findViewById(R.id.cbResolutionPolice)
        cbResolutionOthers      = findViewById(R.id.cbResolutionOthers)
        etResolutionOthers      = findViewById(R.id.etResolutionOthers)
        etConcernsSuggestions   = findViewById(R.id.etConcernsSuggestions)

        // Date / time pickers
        etDateFirstVisit.setOnClickListener  { showDatePicker(etDateFirstVisit) }
        etDateSecondVisit.setOnClickListener { showDatePicker(etDateSecondVisit) }
        etDateThirdVisit.setOnClickListener  { showDatePicker(etDateThirdVisit) }
        etMember1Birthdate.setOnClickListener { showDatePicker(etMember1Birthdate) }
        etMember2Birthdate.setOnClickListener { showDatePicker(etMember2Birthdate) }
        etMember3Birthdate.setOnClickListener { showDatePicker(etMember3Birthdate) }
        etMember4Birthdate.setOnClickListener { showDatePicker(etMember4Birthdate) }
        etMember5Birthdate.setOnClickListener { showDatePicker(etMember5Birthdate) }
        etTimeStarted.setOnClickListener  { showTimePicker(etTimeStarted) }
        etTimeFinished.setOnClickListener { showTimePicker(etTimeFinished) }
    }

    private fun setupSpinner() {
        val incomeOptions = listOf(
            "Select monthly income",
            "Less than 5,000", "5,001 – 10,000", "10,001 – 15,000",
            "15,001 – 20,000", "20,001 – 25,000", "25,001 – 30,000",
            "30,001 – 35,000", "35,001 – 40,000", "40,001 – 45,000",
            "45,001 – 50,000", "50,001 and above"
        )
        spinnerMonthlyIncome.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, incomeOptions)
    }

    private fun saveForm() {
        val controlNo = etControlNo.text.toString().trim()
        val address   = etAddress.text.toString().trim()
        if (address.isEmpty()) { etAddress.error = "Address is required"; return }

        fun t(v: EditText) = v.text.toString().trim()

        val data = hashMapOf(
            "controlNo"       to controlNo,
            "numberOfFamily"  to t(etNumberOfFamily),
            "address"         to address,
            "informant"       to t(etInformant),
            "surveyedBy"      to t(etSurveyedBy),
            "dateFirstVisit"  to t(etDateFirstVisit),
            "dateSecondVisit" to t(etDateSecondVisit),
            "dateThirdVisit"  to t(etDateThirdVisit),
            "timeStarted"     to t(etTimeStarted),
            "timeFinished"    to t(etTimeFinished),
            "statusLastVisit" to t(etStatusLastVisit),

            "garbageDisposal" to getRadioText(rgGarbageDisposal,
                mapOf(R.id.rbGarbageBurning to "Burning", R.id.rbGarbageBurying to "Burying",
                    R.id.rbGarbageComposting to "Composting",
                    R.id.rbGarbageCollected to "Collected by Garbage Truck",
                    R.id.rbGarbageOthers to "Others")),

            "foodStorage" to getRadioText(rgFoodStorage2,
                mapOf(R.id.rbFoodStorageCovered to "Covered",
                    R.id.rbFoodStorageUncovered to "Uncovered")),

            "animal1RabiesCarrier" to getRadioText(rgAnimal1RabiesCarrier,
                mapOf(R.id.rbAnimal1RabiesYes to "Yes", R.id.rbAnimal1RabiesNo to "No")),
            "animal2Kind"        to t(etAnimal2Kind),
            "animal2Heads"       to t(etAnimal2Heads),
            "animal2Vaccination" to getRadioText(rgAnimal2Vaccination,
                mapOf(R.id.rbAnimal2VaccinatedYes to "Vaccinated",
                    R.id.rbAnimal2VaccinatedNo to "Not Vaccinated",
                    R.id.rbAnimal2VaccinatedUnknown to "Unknown")),
            "animal2RabiesCarrier" to getRadioText(rgAnimal2RabiesCarrier,
                mapOf(R.id.rbAnimal2RabiesYes to "Yes", R.id.rbAnimal2RabiesNo to "No")),

            // ── Members 1-5
            "member1Name"              to t(etMember1Name),
            "member1Relation"          to t(etMember1Relation),
            "member1Gender"            to t(etMember1Gender),
            "member1Age"               to t(etMember1Age),
            "member1Birthdate"         to t(etMember1Birthdate),
            "member1MaritalStatus"     to t(etMember1MaritalStatus),
            "member1Religion"          to t(etMember1Religion),
            "member1Education"         to t(etMember1Education),
            "member1OccupationType"    to t(etMember1OccupationType),
            "member1PlaceOfWork"       to t(etMember1PlaceOfWork),
            "member1LengthOfResidence" to t(etMember1LengthOfResidence),
            "member1Dialect"           to t(etDialect),          // FIX: etDialect = Member 1 dialect

            "member2Name"              to t(etMember2Name),
            "member2Relation"          to t(etMember2Relation),
            "member2Gender"            to t(etMember2Gender),
            "member2Age"               to t(etMember2Age),
            "member2Birthdate"         to t(etMember2Birthdate),
            "member2MaritalStatus"     to t(etMember2MaritalStatus),
            "member2Religion"          to t(etMember2Religion),
            "member2Education"         to t(etMember2Education),
            "member2OccupationType"    to t(etMember2OccupationType),
            "member2PlaceOfWork"       to t(etMember2PlaceOfWork),
            "member2LengthOfResidence" to t(etMember2LengthOfResidence),
            "member2Dialect"           to t(etMember2Dialect),

            "member3Name"              to t(etMember3Name),
            "member3Relation"          to t(etMember3Relation),
            "member3Gender"            to t(etMember3Gender),
            "member3Age"               to t(etMember3Age),
            "member3Birthdate"         to t(etMember3Birthdate),
            "member3MaritalStatus"     to t(etMember3MaritalStatus),
            "member3Religion"          to t(etMember3Religion),
            "member3Education"         to t(etMember3Education),
            "member3OccupationType"    to t(etMember3OccupationType),
            "member3PlaceOfWork"       to t(etMember3PlaceOfWork),
            "member3LengthOfResidence" to t(etMember3LengthOfResidence),
            "member3Dialect"           to t(etMember3Dialect),

            "member4Name"              to t(etMember4Name),
            "member4Relation"          to t(etMember4Relation),
            "member4Gender"            to t(etMember4Gender),
            "member4Age"               to t(etMember4Age),
            "member4Birthdate"         to t(etMember4Birthdate),
            "member4MaritalStatus"     to t(etMember4MaritalStatus),
            "member4Religion"          to t(etMember4Religion),
            "member4Education"         to t(etMember4Education),
            "member4OccupationType"    to t(etMember4OccupationType),
            "member4PlaceOfWork"       to t(etMember4PlaceOfWork),
            "member4LengthOfResidence" to t(etMember4LengthOfResidence),
            "member4Dialect"           to t(etMember4Dialect),

            "member5Name"              to t(etMember5Name),
            "member5Relation"          to t(etMember5Relation),
            "member5Gender"            to t(etMember5Gender),
            "member5Age"               to t(etMember5Age),
            "member5Birthdate"         to t(etMember5Birthdate),
            "member5MaritalStatus"     to t(etMember5MaritalStatus),
            "member5Religion"          to t(etMember5Religion),
            "member5Education"         to t(etMember5Education),
            "member5OccupationType"    to t(etMember5OccupationType),
            "member5PlaceOfWork"       to t(etMember5PlaceOfWork),
            "member5LengthOfResidence" to t(etMember5LengthOfResidence),
            "member5Dialect"           to t(etMember5Dialect),

            "familyNuclear"          to cbNuclear.isChecked,
            "familyExtended"         to cbExtended.isChecked,
            "familyDyad"             to cbDyad.isChecked,
            "familyHomosexual"       to cbHomosexual.isChecked,
            "familyCohabiting"       to cbCohabiting.isChecked,
            "familyBlended"          to cbBlended.isChecked,
            "familyWithGrandparents" to cbWithGrandparents.isChecked,
            "familySingleParent"     to cbSingleParent.isChecked,
            "familyPower" to getRadioText(rgFamilyPower,
                mapOf(R.id.rbPatriarchal to "Patriarchal", R.id.rbMatriarchal to "Matriarchal",
                    R.id.rbEgalitarian to "Egalitarian", R.id.rbMatricentric to "Matricentric")),
            "familyResidence" to getRadioText(rgFamilyResidence,
                mapOf(R.id.rbPatrilocal to "Patrilocal", R.id.rbMatrilocal to "Matrilocal",
                    R.id.rbBilocal to "Bilocal", R.id.rbNeolocal to "Neolocal")),
            "familyDescent" to getRadioText(rgFamilyDescent,
                mapOf(R.id.rbPatrilineal to "Patrilineal", R.id.rbMatrilineal to "Matrilineal",
                    R.id.rbBilateral to "Bilateral")),

            "safetyPrecautions" to getRadioText(rgSafetyPrecautions,
                mapOf(R.id.rbSafetyPracticed to "Practiced",
                    R.id.rbSafetyNotPracticed to "Not Practiced")),
            "bmiRemarks"  to t(etBmiRemarks),
            "wcHcRemarks" to t(etWcHcRemarks),
            "muacRemarks" to t(etMuacRemarks),

            "serviceReligious"     to cbReligiousServices.isChecked,
            "serviceLivelihood"    to cbLivelihoodServices.isChecked,
            "serviceHealth"        to cbHealthServices.isChecked,
            "serviceGarbage"       to cbGarbageCollection.isChecked,
            "servicePeaceOrder"    to cbPeaceAndOrder.isChecked,
            "serviceOthers"        to cbCommOthers.isChecked,
            "serviceOthersText"    to t(etCommOthers),
            "facilityBrgyHall"     to cbBrgyHall.isChecked,
            "facilityHealthStation" to cbHealthStation.isChecked,
            "facilityChurch"       to cbChurch.isChecked,
            "facilitySchool"       to cbSchool.isChecked,
            "orgSeniorCitizen"     to cbSeniorCitizen.isChecked,
            "orgYouth"             to cbYouth.isChecked,
            "orgOthers"            to cbOrganizationsOthers.isChecked,
            "orgOthersText"        to t(etOrganizationsOthers),
            "tradBayanihan"        to cbBayanihan.isChecked,
            "tradPalabra"          to cbPalabraDeHonor.isChecked,
            "tradPakikisama"       to cbPakikisama.isChecked,
            "tradNingas"           to cbNingasKugon.isChecked,
            "tradFiestas"          to cbFiestas.isChecked,
            "tradFamilyTies"       to cbCloseFamilyTies.isChecked,
            "tradRespectElderly"   to cbRespectForElderly.isChecked,
            "tradOthers"           to cbTraditionOthers.isChecked,
            "tradOthersText"       to t(etTraditionOthers),
            "recCourt"             to cbRecreationCourt.isChecked,
            "recPlayground"        to cbPlayground.isChecked,
            "recPlaza"             to cbPlaza.isChecked,
            "recOthers"            to cbRecreationOthers.isChecked,
            "recOthersText"        to t(etRecreationOthers),
            "transTricycle"        to cbTricycle.isChecked,
            "transJeep"            to cbJeep.isChecked,
            "transPujPuv"          to cbPujPuv.isChecked,
            "transBicycle"         to cbBicycle.isChecked,
            "transPrivateVehicle"  to cbPrivateVehicle.isChecked,
            "commPostal"           to cbPostalSystem.isChecked,
            "commInternet"         to cbInternet.isChecked,
            "commTelephone"        to cbTelephone.isChecked,
            "commCellPhone"        to cbCellPhone.isChecked,
            "commTwoWayRadio"      to cbTwoWayRadio.isChecked,

            "housingCongestionObservation" to getRadioText(rgHousingCongestionObservation,
                mapOf(R.id.rbCongestionObsYes to "Yes", R.id.rbCongestionObsNo to "No")),

            "earner1Position"    to t(etEarner1Position),
            "earner1Income"      to t(etEarner1Income),
            "earner2Position"    to t(etEarner2Position),
            "earner2Income"      to t(etEarner2Income),
            "earner3Position"    to t(etEarner3Position),
            "earner3Income"      to t(etEarner3Income),
            "earner4Position"    to t(etEarner4Position),
            "earner4Income"      to t(etEarner4Income),
            "monthlyIncome"      to spinnerMonthlyIncome.selectedItem.toString(),
            "financeEmployment"  to cbFinanceEmployment.isChecked,
            "financeBusiness"    to cbFinanceBusiness.isChecked,
            "financePension"     to cbFinancePension.isChecked,
            "financeRelatives"   to cbFinanceRelatives.isChecked,
            "financeOthers"      to cbFinanceOthers.isChecked,
            "incomeAdequacy" to getRadioText(rgIncomeAdequacy,
                mapOf(R.id.rbIncomeAdequate to "Adequate", R.id.rbIncomeNotAdequate to "Not Adequate")),
            "healthBudgetAmount" to t(etHealthBudgetAmount),

            "homeOwnership" to getRadioText(rgHomeOwnership,
                mapOf(R.id.rbOwned to "Owned", R.id.rbRented to "Rented",
                    R.id.rbRentFree to "Rent-free", R.id.rbSquatting to "Squatting")),
            "construction" to getRadioText(rgConstruction,
                mapOf(R.id.rbLight to "Light", R.id.rbMixed to "Mixed",
                    R.id.rbConcrete to "Strong (Concrete)")),
            "numRooms"          to getCheckedText(rgNumRooms),
            "housingCongestion" to getCheckedText(rgHousingCongestion),
            "spaceAdequacy" to getRadioText(rgSpaceAdequacy,
                mapOf(R.id.rbSpaceAdequate to "Adequate", R.id.rbSpaceInadequate to "Inadequate")),
            "lightingFacility" to getRadioText(rgLightingFacility,
                mapOf(R.id.rbLightingElectricity to "Electricity",
                    R.id.rbLightingKerosene to "Kerosene Lamp", R.id.rbLightingCandle to "Candle",
                    R.id.rbLightingSolar to "Solar", R.id.rbLightingOthers to "Others")),

            "lightingAdequacy" to getCheckedText(rgLightingAdequacy),
            "ventilation" to getRadioText(rgVentilation,
                mapOf(R.id.rbVentilationAdequate to "Adequate",
                    R.id.rbVentilationInadequate to "Inadequate")),
            "sanitation" to getRadioText(rgSanitation,
                mapOf(R.id.rbClean to "Generally Clean", R.id.rbDirty to "Dirty")),
            "waterOwnership" to getRadioText(rgWaterOwnership,
                mapOf(R.id.rbWaterPrivate to "Private", R.id.rbWaterPublic to "Public")),
            "waterSource" to getRadioText(rgWaterSource,
                mapOf(R.id.rbWaterDeepWell to "Deep Well",
                    R.id.rbWaterLocalDistrict to "Local Water District",
                    R.id.rbWaterCommercial to "Commercial")),

            "foodStorageCover"    to getCheckedText(rgFoodStorage),
            "storageRefrigerator" to cbStorageRefrigerator.isChecked,
            "storageCabinet"      to cbStorageCabinet.isChecked,
            "storageBasket"       to cbStorageBasket.isChecked,
            "storageTable"        to cbStorageTable.isChecked,
            "cookElectricStove"   to cbCookElectricStove.isChecked,
            "cookGasStove"        to cbCookGasStove.isChecked,
            "cookFirewood"        to cbCookFirewood.isChecked,
            "cookOthers"          to cbCookOthers.isChecked,
            "cookOthersText"      to t(etCookOthers),
            "kitchenSanitation"   to getCheckedText(rgKitchenSanitation2),

            "garbageStorage" to getCheckedText(rgGarbageStorage),
            "wasteSegregation" to getRadioText(rgWasteSegregation,
                mapOf(R.id.rbSegregationPracticed to "Practiced",
                    R.id.rbSegregationNotPracticed to "Not Practiced")),
            "wasteHogFeeding"  to cbHogFeeding.isChecked,
            "wasteOpenDumping" to cbOpenDumping.isChecked,
            "wasteBurialPit"   to cbBurialInPit.isChecked,
            "wasteCollected"   to cbCollected.isChecked,
            "wasteComposting"  to cbComposting.isChecked,
            "wasteOpenBurning" to cbOpenBurning.isChecked,

            "toiletOwnership" to getRadioText(rgToiletOwnership,
                mapOf(R.id.rbToiletOwned to "Owned", R.id.rbToiletShared to "Shared/Public",
                    R.id.rbToiletNone to "None")),
            "toiletType" to getRadioText(rgToiletType,
                mapOf(R.id.rbBallotSystem to "Ballot", R.id.rbPailSystem to "Pail",
                    R.id.rbOverhungLatrine to "Overhung Latrine",
                    R.id.rbWaterSealed to "Water-sealed", R.id.rbFlushType to "Flush")),
            "drainageSystem" to getRadioText(rgDrainageSystem,
                mapOf(R.id.rbDrainageClosed to "Closed/Underground",
                    R.id.rbDrainageOpen to "Open Canal", R.id.rbDrainageNone to "None")),
            "drainageCondition" to getRadioText(rgDrainageCondition,
                mapOf(R.id.rbDrainageFlowing to "Free Flowing",
                    R.id.rbDrainageStagnant to "Stagnant", R.id.rbDrainageClogged to "Clogged")),

            "rabiesAnimals"    to getCheckedText(rgRabiesAnimals),
            "animal1Kind"      to t(etAnimal1Kind),
            "animal1Number"    to t(etAnimal1Number),
            "animal1KeptWhere" to t(etAnimal1KeptWhere),
            "animal1Vaccination" to getRadioText(rgAnimal1Vaccination,
                mapOf(R.id.rbAnimal1VaccinatedYes to "Vaccinated",
                    R.id.rbAnimal1VaccinatedNo to "Not Vaccinated",
                    R.id.rbAnimal1VaccinatedUnknown to "Unknown")),

            "smoker" to getRadioText(rgSmoker,
                mapOf(R.id.rbSmokerYes to "Yes", R.id.rbSmokerNo to "No")),
            "smokerFrequency"  to t(etSmokerFrequency),
            "smokerName"       to t(etSmokerName),
            "smokerAge"        to t(etSmokerAge),
            "smokerAgeStarted" to t(etSmokerAgeStarted),
            "smokerReason"     to t(etSmokerReason),
            "drugs" to getRadioText(rgDrugs,
                mapOf(R.id.rbDrugsYes to "Yes", R.id.rbDrugsNo to "No")),
            "drugTypesUsed"  to t(etDrugTypesUsed),
            "drugName"       to t(etDrugName),
            "drugAge"        to t(etDrugAge),
            "drugAgeStarted" to t(etDrugAgeStarted),
            "drugReason"     to t(etDrugReason),
            "alcohol" to getRadioText(rgAlcohol,
                mapOf(R.id.rbAlcoholYes to "Yes", R.id.rbAlcoholNo to "No")),
            "alcoholName"       to t(etAlcoholName),
            "alcoholAge"        to t(etAlcoholAge),
            "alcoholAgeStarted" to t(etAlcoholAgeStarted),
            "alcoholFrequency"  to t(etAlcoholFrequency),
            "alcoholReason"     to t(etAlcoholReason),

            "anthrName"          to t(etAnthrName),
            "anthrAge"           to t(etAnthrAge),
            "anthrWeight"        to t(etAnthrWeight),
            "anthrHeight"        to t(etAnthrHeight),
            "anthrBmi"           to t(etAnthrBmi),
            "anthrMuac"          to t(etAnthrMuac),
            "waistCircumference" to t(etWaistCircumference),
            "hipCircumference"   to t(etHipCircumference),
            "waistHipRatio"      to t(etWaistHipRatio),

            "foodFirstChoice" to getRadioText(rgFoodChoice,
                mapOf(R.id.rbMeat to "Meat", R.id.rbFish to "Fish",
                    R.id.rbVegetable to "Vegetable", R.id.rbFoodMixed to "Mixed")),
            "servings1"        to getCheckedText(rgServings1),
            "foodSecondChoice" to getCheckedText(rgFoodChoice2),
            "servings2"        to getCheckedText(rgServings2),
            "foodPrepFreq"     to getCheckedText(rgFoodPrepFreq),
            "foodPreparation"  to getCheckedText(rgFoodPreparation),
            "foodRestaurant"   to cbFoodRestaurant.isChecked,
            "foodCarinderia"   to cbFoodCarinderia.isChecked,
            "foodCart"         to cbFoodCart.isChecked,
            "foodBuyOthers"    to cbFoodBuyOthers.isChecked,
            "foodBuyOthersText" to t(etFoodBuyOthers),
            "foodConvenient"   to cbFoodConvenient.isChecked,
            "foodCheaper"      to cbFoodCheaper.isChecked,
            "foodHealthy"      to cbFoodHealthy.isChecked,
            "foodVariety"      to cbFoodVariety.isChecked,
            "cannedFood"          to getCheckedText(rgCannedFood),
            "grilledFood"         to getCheckedText(rgGrilledFood),
            "carbonatedBeverages" to getCheckedText(rgCarbonatedBeverages),

            "consultDoctor"          to cbConsultDoctor.isChecked,
            "consultNurse"           to cbConsultNurse.isChecked,
            "consultMidwife"         to cbConsultMidwife.isChecked,
            "consultHilot"           to cbConsultHilot.isChecked,
            "consultAlbularyo"       to cbConsultAlbularyo.isChecked,
            "consultFaithHealer"     to cbConsultFaithHealer.isChecked,
            "consultElderly"         to cbConsultElderly.isChecked,
            "measurePrivateWorker"   to cbMeasurePrivateWorker.isChecked,
            "measureCommunityHealer" to cbMeasureCommunityHealer.isChecked,
            "measureRHU"             to cbMeasureRHU.isChecked,
            "measureSelfMedication"  to cbMeasureSelfMedication.isChecked,
            "measureNone"            to cbMeasureNone.isChecked,
            "medPrescribed"          to cbMedPrescribed.isChecked,
            "medOTC"                 to cbMedOTC.isChecked,
            "medHerbals"             to cbMedHerbals.isChecked,
            "medOthers"              to cbMedOthers.isChecked,
            "medOthersText"          to t(etMedOthers),
            "medCheckup" to getRadioText(rgMedCheckup,
                mapOf(R.id.rbMedOnceYear to "Once a year", R.id.rbMedTwiceYear to "Twice a year",
                    R.id.rbMedMoreThanYear to "More than once a year")),
            "dentalCheckup" to getCheckedText(rgDentalCheckup),

            "bhcHealthServices"       to t(etBhcHealthServices),
            "immunizationName"        to t(etImmunizationName),
            "immunizationAge"         to t(etImmunizationAge),
            "immunizationGender"      to t(etImmunizationGender),
            "immunizationBcg"         to t(etImmunizationBcg),
            "immunizationDpt1"        to t(etImmunizationDpt1),
            "immunizationDpt2"        to t(etImmunizationDpt2),
            "immunizationDpt3"        to t(etImmunizationDpt3),
            "immunizationHepa1"       to t(etImmunizationHepa1),
            "immunizationHepa2"       to t(etImmunizationHepa2),
            "immunizationHepa3"       to t(etImmunizationHepa3),
            "immunizationOpv1"        to t(etImmunizationOpv1),
            "immunizationOpv2"        to t(etImmunizationOpv2),
            "immunizationOpv3"        to t(etImmunizationOpv3),
            "immunizationMeasles"     to t(etImmunizationMeasles),
            "immunizationCompleteAge" to t(etImmunizationCompleteAge),
            "immunizationIncomplete"  to t(etImmunizationIncomplete),
            "immunizationFic"         to t(etImmunizationFic),
            "antenatalName"           to t(etAntenatalName),
            "antenatalAog"            to t(etAntenatalAog),
            "antenatalCheckup"        to getCheckedText(rgAntenatalCheckup),
            "antenatalTetanus"        to getCheckedText(rgAntenatalTetanus),

            "familyPlanning" to getRadioText(rgFamilyPlanning,
                mapOf(R.id.rbFpAcceptor to "Acceptor", R.id.rbFpNonAcceptor to "Non-Acceptor")),
            "fpReasonGoodHealth"        to cbFpReasonGoodHealth.isChecked,
            "fpReasonPersonalBelief"    to cbFpReasonPersonalBelief.isChecked,
            "fpReasonReligious"         to cbFpReasonReligious.isChecked,
            "fpReasonInfluence"         to cbFpReasonInfluence.isChecked,
            "fpReasonOthers"            to cbFpReasonOthers.isChecked,
            "fpReasonOthersText"        to t(etFpReasonOthers),
            "fpNonReasonBadHealth"      to cbFpNonReasonBadHealth.isChecked,
            "fpNonReasonPersonalBelief" to cbFpNonReasonPersonalBelief.isChecked,
            "fpNonReasonReligious"      to cbFpNonReasonReligious.isChecked,
            "fpNonReasonInfluence"      to cbFpNonReasonInfluence.isChecked,
            "fpNonReasonOthers"         to cbFpNonReasonOthers.isChecked,
            "fpNonReasonOthersText"     to t(etFpNonReasonOthers),
            "fpBtl"           to cbFpBtl.isChecked,
            "fpVasectomy"     to cbFpVasectomy.isChecked,
            "fpPills"         to cbFpPills.isChecked,
            "fpIud"           to cbFpIud.isChecked,
            "fpInjectable"    to cbFpInjectable.isChecked,
            "fpCondoms"       to cbFpCondoms.isChecked,
            "fpImplant"       to cbFpImplant.isChecked,
            "fpBillings"      to cbFpBillings.isChecked,
            "fpBbt"           to cbFpBbt.isChecked,
            "fpSymptoThermal" to cbFpSymptoThermal.isChecked,
            "fpSdm"           to cbFpSdm.isChecked,
            "fpLam"           to cbFpLam.isChecked,

            "morbidityName"         to t(etMorbidityName),
            "morbidityAge"          to t(etMorbidityAge),
            "morbidityGender"       to t(etMorbidityGender),
            "morbidityCause"        to t(etMorbidityCause),
            "morbidityIntervention" to getCheckedText(rgMorbidityIntervention),
            "morbidityAdmitted"     to getCheckedText(rgMorbidityAdmitted),
            "mortalityName"         to t(etMortalityName),
            "mortalityAge"          to t(etMortalityAge),
            "mortalityGender"       to t(etMortalityGender),
            "mortalityCause"        to t(etMortalityCause),
            "ncdName"   to t(etNcdName),
            "ncdAge"    to t(etNcdAge),
            "ncdGender" to t(etNcdGender),
            "ncdType"   to t(etNcdType),
            "cdName"    to t(etCdName),
            "cdAge"     to t(etCdAge),
            "cdGender"  to t(etCdGender),
            "cdType"    to t(etCdType),
            "bpName"    to t(etBpName),
            "bpAge"     to t(etBpAge),
            "bpGender"  to t(etBpGender),
            "bpReading" to t(etBpReading),
            "bhcAwareness" to getRadioText(rgBhcAwareness,
                mapOf(R.id.rbBhcAware to "Aware", R.id.rbBhcUnaware to "Unaware")),

            "bhcSchedule"          to t(etBhcSchedule),
            "rhuPhysician"         to t(etRhuPhysician),
            "rhuNurse"             to t(etRhuNurse),
            "bhcMidwife"           to t(etBhcMidwife),
            "suppliesAvailability" to getCheckedText(rgSuppliesAvailability),

            "leaderCaptain"          to cbLeaderCaptain.isChecked,
            "leaderKagawad"          to cbLeaderKagawad.isChecked,
            "leaderElderly"          to cbLeaderElderly.isChecked,
            "leaderBhw"              to cbLeaderBhw.isChecked,
            "leaderInfluential"      to cbLeaderInfluential.isChecked,
            "leaderReligious"        to cbLeaderReligious.isChecked,
            "leaderNeighbor"         to cbLeaderNeighbor.isChecked,
            "conflictGossip"         to cbConflictGossip.isChecked,
            "conflictFamilyConflict" to cbConflictFamilyConflict.isChecked,
            "conflictDrugs"          to cbConflictDrugs.isChecked,
            "conflictRiot"           to cbConflictRiot.isChecked,
            "conflictAlcohol"        to cbConflictAlcohol.isChecked,
            "conflictOthers"         to cbConflictOthers.isChecked,
            "conflictOthersText"     to t(etConflictOthers),
            "resolutionSettlement"   to cbResolutionSettlement.isChecked,
            "resolutionBrgyHearing"  to cbResolutionBrgyHearing.isChecked,
            "resolutionPolice"       to cbResolutionPolice.isChecked,
            "resolutionOthers"       to cbResolutionOthers.isChecked,
            "resolutionOthersText"   to t(etResolutionOthers),

            "concernsSuggestions" to t(etConcernsSuggestions),
            "createdAt"  to System.currentTimeMillis(),
            "updatedAt"  to System.currentTimeMillis(),
            "nurseUid"   to (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""),
            "patientUid" to intentPatientUid
        )

        btnSave.isEnabled = false
        db.collection("assessments")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Assessment Saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                btnSave.isEnabled = true
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun getRadioText(group: RadioGroup, map: Map<Int, String>): String =
        map[group.checkedRadioButtonId] ?: ""

    private fun getCheckedText(group: RadioGroup): String {
        val id = group.checkedRadioButtonId
        if (id == -1) return ""
        return group.findViewById<RadioButton>(id)?.text?.toString() ?: ""
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}