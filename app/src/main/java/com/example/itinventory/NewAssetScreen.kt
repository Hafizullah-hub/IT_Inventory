package com.example.itinventory

import com.example.itinventory.ui.theme.*

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAssetScreen(
    initialAssetNo: String = "",
    initialSerial: String = "",
    initialCapex: String = "",
    initialDate: String = "",
    onBackClick: () -> Unit
) {
    // Core Details
    var assetNo by remember { mutableStateOf(initialAssetNo) }
    var serial by remember { mutableStateOf(initialSerial) }
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

    // Software (OS, Office, Antivirus)
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
    var capexNo by remember { mutableStateOf(initialCapex) }
    var poNo by remember { mutableStateOf("") }
    var poDate by remember { mutableStateOf(initialDate) }
    var invoiceNo by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var warranty by remember { mutableStateOf("") }

    // Metadata
    var remark1 by remember { mutableStateOf("") }
    var remark2 by remember { mutableStateOf("") }
    var fileSeq by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Asset", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Fill in all asset information below:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        val db = FirebaseFirestore.getInstance()
                        // Construct the nested asset object structure
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Create New Asset", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
