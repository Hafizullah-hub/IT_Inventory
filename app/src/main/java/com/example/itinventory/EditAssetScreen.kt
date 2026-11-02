package com.example.itinventory

import com.example.itinventory.ui.theme.*

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAssetScreen(assetId: String, onBackClick: () -> Unit, onDeleteCompleted: () -> Unit = {}) {
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    // Core Details
    var assetNo by remember { mutableStateOf(assetId) }
    var serial by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var compName by remember { mutableStateOf("") }
    var ad by remember { mutableStateOf("") }
    var boxLocate by remember { mutableStateOf("") }

    // Assignment
    var user by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var lastUse by remember { mutableStateOf("") }

    // Hardware
    var processor by remember { mutableStateOf("") }
    var ram by remember { mutableStateOf("") }
    var hardisk by remember { mutableStateOf("") }
    var additionalHardware by remember { mutableStateOf("") }

    // Software
    var osName by remember { mutableStateOf("") }
    var osBit by remember { mutableStateOf("") }
    var osProductKey by remember { mutableStateOf("") }
    var officeVersion by remember { mutableStateOf("") }
    var officeLicenceKey by remember { mutableStateOf("") }
    var antivirusName by remember { mutableStateOf("") }
    var antivirusProductKey by remember { mutableStateOf("") }
    var antivirusStartDate by remember { mutableStateOf("") }
    var antivirusExpiryDate by remember { mutableStateOf("") }
    var antivirusLastUpdate by remember { mutableStateOf("") }
    var otherApp by remember { mutableStateOf("") }

    // Procurement
    var supplier by remember { mutableStateOf("") }
    var capexNo by remember { mutableStateOf("") }
    var poNo by remember { mutableStateOf("") }
    var poDate by remember { mutableStateOf("") }
    var invoiceNo by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var warranty by remember { mutableStateOf("") }

    // Metadata
    var remark1 by remember { mutableStateOf("") }
    var remark2 by remember { mutableStateOf("") }
    var fileSeq by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(assetId) {
        db.collection("assets").document(assetId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val asset = document.toObject(Asset::class.java)
                if (asset != null) {
                    assetNo = asset.coreDetails?.assetNo ?: assetId
                    serial = asset.coreDetails?.serial ?: ""
                    type = asset.coreDetails?.type ?: ""
                    manufacturer = asset.coreDetails?.manufacturer ?: ""
                    model = asset.coreDetails?.model ?: ""
                    compName = asset.coreDetails?.compName ?: ""
                    ad = asset.coreDetails?.ad ?: ""
                    boxLocate = asset.coreDetails?.boxLocate ?: ""

                    user = asset.assignment?.user ?: ""
                    dept = asset.assignment?.dept ?: ""
                    company = asset.assignment?.company ?: ""
                    location = asset.assignment?.location ?: ""
                    lastUse = asset.assignment?.lastUse ?: ""

                    processor = asset.hardware?.processor ?: ""
                    ram = asset.hardware?.ram ?: ""
                    hardisk = asset.hardware?.hardisk ?: ""
                    additionalHardware = asset.hardware?.additionalHardware ?: ""

                    osName = asset.software?.os?.name ?: ""
                    osBit = asset.software?.os?.bit ?: ""
                    osProductKey = asset.software?.os?.productKey ?: ""
                    officeVersion = asset.software?.office?.version ?: ""
                    officeLicenceKey = asset.software?.office?.licenceKey ?: ""
                    antivirusName = asset.software?.antivirus?.name ?: ""
                    antivirusProductKey = asset.software?.antivirus?.productKey ?: ""
                    antivirusStartDate = asset.software?.antivirus?.startDate ?: ""
                    antivirusExpiryDate = asset.software?.antivirus?.expiryDate ?: ""
                    antivirusLastUpdate = asset.software?.antivirus?.lastUpdate ?: ""
                    otherApp = asset.software?.otherApp ?: ""

                    supplier = asset.procurement?.supplier ?: ""
                    capexNo = asset.procurement?.capexNo ?: ""
                    poNo = asset.procurement?.poNo ?: ""
                    poDate = asset.procurement?.poDate ?: ""
                    invoiceNo = asset.procurement?.invoiceNo ?: ""
                    amount = asset.procurement?.amount?.toString() ?: ""
                    warranty = asset.procurement?.warranty ?: ""

                    remark1 = asset.metadata?.remark1 ?: ""
                    remark2 = asset.metadata?.remark2 ?: ""
                    fileSeq = asset.metadata?.fileSeq ?: ""
                }
            }
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Asset", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextDark
                )
            )
        },
        containerColor = LightBg
    ) { paddingValues ->
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete Asset") },
                text = { Text("Are you sure you want to delete this asset? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            db.collection("assets").document(assetId).delete()
                                .addOnSuccessListener {
                                    showDeleteConfirm = false
                                    onDeleteCompleted()
                                }
                        }
                    ) {
                        Text("Delete", color = Color(0xFFDC2626))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Editing Asset: $assetId",
                    fontSize = 14.sp,
                    color = TextLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FormSection("Core Details") {
                    FormField("Asset No (Document ID)*", assetNo) { assetNo = it }
                    FormField("Serial Number", serial) { serial = it }
                    FormField("Type (e.g., Laptop, Desktop)", type) { type = it }
                    FormField("Manufacturer", manufacturer) { manufacturer = it }
                    FormField("Model", model) { model = it }
                    FormField("Computer Name", compName) { compName = it }
                    FormField("Active Directory (AD)", ad) { ad = it }
                    FormField("Box Location", boxLocate) { boxLocate = it }
                }

                FormSection("Assignment") {
                    FormField("User", user) { user = it }
                    FormField("Department", dept) { dept = it }
                    FormField("Company", company) { company = it }
                    FormField("Location", location) { location = it }
                    FormField("Last Use Date", lastUse) { lastUse = it }
                }

                FormSection("Hardware") {
                    FormField("Processor", processor) { processor = it }
                    FormField("RAM", ram) { ram = it }
                    FormField("Hard Disk", hardisk) { hardisk = it }
                    FormField("Additional Hardware", additionalHardware) { additionalHardware = it }
                }

                FormSection("Software - OS") {
                    FormField("OS Name", osName) { osName = it }
                    FormField("OS Architecture (Bit)", osBit) { osBit = it }
                    FormField("OS Product Key", osProductKey) { osProductKey = it }
                }
                
                FormSection("Software - Office") {
                    FormField("Office Version", officeVersion) { officeVersion = it }
                    FormField("Office Licence Key", officeLicenceKey) { officeLicenceKey = it }
                }

                FormSection("Software - Antivirus") {
                    FormField("Antivirus Name", antivirusName) { antivirusName = it }
                    FormField("Antivirus Product Key", antivirusProductKey) { antivirusProductKey = it }
                    FormField("Start Date", antivirusStartDate) { antivirusStartDate = it }
                    FormField("Expiry Date", antivirusExpiryDate) { antivirusExpiryDate = it }
                    FormField("Last Update", antivirusLastUpdate) { antivirusLastUpdate = it }
                }

                FormSection("Software - Other") {
                    FormField("Other Applications", otherApp) { otherApp = it }
                }

                FormSection("Procurement") {
                    FormField("Supplier", supplier) { supplier = it }
                    FormField("CAPEX No", capexNo) { capexNo = it }
                    FormField("PO No", poNo) { poNo = it }
                    FormField("PO Date", poDate) { poDate = it }
                    FormField("Invoice No", invoiceNo) { invoiceNo = it }
                    FormField("Amount", amount) { amount = it }
                    FormField("Warranty", warranty) { warranty = it }
                }

                FormSection("Metadata") {
                    FormField("Remark 1", remark1) { remark1 = it }
                    FormField("Remark 2", remark2) { remark2 = it }
                    FormField("File Seq", fileSeq) { fileSeq = it }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        if (assetNo.isNotBlank()) {
                            val assetData = mapOf(
                                "core_details" to mapOf(
                                    "asset_no" to assetNo,
                                    "serial" to serial,
                                    "type" to type,
                                    "manufacturer" to manufacturer,
                                    "model" to model,
                                    "comp_name" to compName,
                                    "ad" to ad,
                                    "box_locate" to boxLocate
                                ),
                                "assignment" to mapOf(
                                    "user" to user,
                                    "dept" to dept,
                                    "company" to company,
                                    "location" to location,
                                    "last_use" to lastUse
                                ),
                                "hardware" to mapOf(
                                    "processor" to processor,
                                    "ram" to ram,
                                    "hardisk" to hardisk,
                                    "additional_hardware" to additionalHardware
                                ),
                                "software" to mapOf(
                                    "os" to mapOf(
                                        "name" to osName,
                                        "bit" to osBit,
                                        "product_key" to osProductKey
                                    ),
                                    "office" to mapOf(
                                        "version" to officeVersion,
                                        "licence_key" to officeLicenceKey
                                    ),
                                    "antivirus" to mapOf(
                                        "name" to antivirusName,
                                        "product_key" to antivirusProductKey,
                                        "start_date" to antivirusStartDate,
                                        "expiry_date" to antivirusExpiryDate,
                                        "last_update" to antivirusLastUpdate
                                    ),
                                    "other_app" to otherApp
                                ),
                                "procurement" to mapOf(
                                    "supplier" to supplier,
                                    "capex_no" to capexNo,
                                    "po_no" to poNo,
                                    "po_date" to poDate,
                                    "invoice_no" to invoiceNo,
                                    "amount" to amount.toDoubleOrNull(),
                                    "warranty" to warranty
                                ),
                                "metadata" to mapOf(
                                    "remark1" to remark1,
                                    "remark2" to remark2,
                                    "file_seq" to fileSeq
                                )
                            )
                            
                            db.collection("assets").document(assetNo).set(assetData)
                                .addOnSuccessListener {
                                    onBackClick()
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
                ) {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}
