package com.example.itinventory

import com.example.itinventory.ui.theme.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailScreen(assetId: String, onBackClick: () -> Unit = {}, onEditClick: () -> Unit = {}) {
    var asset by remember { mutableStateOf<Asset?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(assetId) {
        db.collection("assets").document(assetId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val parsed = document.toObject(Asset::class.java)
                if (parsed != null) {
                    if (parsed.documentId == null) parsed.documentId = document.id
                    asset = parsed
                }
            }
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = LightBg,
        topBar = { AssetDetailTopBar(onBackClick) },
        floatingActionButton = {
            if (!isLoading && asset != null) {
                ExtendedFloatingActionButton(
                    onClick = onEditClick,
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Asset", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (asset == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Asset not found.", color = TextLight)
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
                // Main Header Card
                AssetHeaderCard(asset)

                // Expandable Sections
                var coreExpanded by remember { mutableStateOf(true) }
                ExpandableSection(
                    title = "Core Details",
                    icon = Icons.Outlined.Badge,
                    badgeText = "8 items",
                    isExpanded = coreExpanded,
                    onToggle = { coreExpanded = !coreExpanded }
                ) {
                    CoreDetailsContent(asset)
                }

                var assignmentExpanded by remember { mutableStateOf(false) }
                val user = asset?.assignment?.user?.takeIf { it.isNotBlank() } ?: "Unassigned"
                val dept = asset?.assignment?.dept?.takeIf { it.isNotBlank() } ?: "Unknown Dept"
                ExpandableSection(
                    title = "Assignment & Custody",
                    icon = Icons.Outlined.PersonOutline,
                    subtitle = "$user • $dept",
                    isExpanded = assignmentExpanded,
                    onToggle = { assignmentExpanded = !assignmentExpanded }
                ) {
                    AssignmentContent(asset)
                }

                var hardwareExpanded by remember { mutableStateOf(false) }
                val cpu = asset?.hardware?.processor?.takeIf { it.isNotBlank() } ?: "No CPU"
                val ram = asset?.hardware?.ram?.takeIf { it.isNotBlank() } ?: "No RAM"
                ExpandableSection(
                    title = "Hardware Specs",
                    icon = Icons.Outlined.Memory,
                    subtitle = "$cpu • $ram",
                    isExpanded = hardwareExpanded,
                    onToggle = { hardwareExpanded = !hardwareExpanded }
                ) {
                    HardwareContent(asset)
                }

                var softwareExpanded by remember { mutableStateOf(false) }
                val osName = asset?.software?.os?.name?.takeIf { it.isNotBlank() } ?: "No OS"
                val avName = asset?.software?.antivirus?.name?.takeIf { it.isNotBlank() } ?: "No AV"
                ExpandableSection(
                    title = "Software & Licenses",
                    icon = Icons.Outlined.Security,
                    subtitle = "$osName • $avName",
                    isExpanded = softwareExpanded,
                    onToggle = { softwareExpanded = !softwareExpanded }
                ) {
                    SoftwareContent(asset)
                }

                var procurementExpanded by remember { mutableStateOf(false) }
                val supplier = asset?.procurement?.supplier?.takeIf { it.isNotBlank() } ?: "Unknown Supplier"
                val poNo = asset?.procurement?.poNo?.takeIf { it.isNotBlank() } ?: "No PO"
                ExpandableSection(
                    title = "Procurement & Financials",
                    icon = Icons.Outlined.Payments,
                    subtitle = "$supplier • $poNo",
                    isExpanded = procurementExpanded,
                    onToggle = { procurementExpanded = !procurementExpanded }
                ) {
                    ProcurementContent(asset)
                }

                var metadataExpanded by remember { mutableStateOf(false) }
                val remark1 = asset?.metadata?.remark1?.takeIf { it.isNotBlank() } ?: "No Remarks"
                ExpandableSection(
                    title = "Metadata & Logs",
                    icon = Icons.Outlined.Description,
                    subtitle = remark1.ifEmpty { "No remarks available" },
                    isExpanded = metadataExpanded,
                    onToggle = { metadataExpanded = !metadataExpanded }
                ) {
                    MetadataContent(asset)
                }
                
                Spacer(modifier = Modifier.height(60.dp)) // Padding for FAB
            }
        }
    }
}

@Composable
fun AssetDetailTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "Asset Details",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.weight(1f)
        )
        
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Outlined.Search, contentDescription = "Search", tint = TextDark)
        }
        
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(PrimaryBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun AssetHeaderCard(asset: Asset?) {
    val assetNo = asset?.documentId ?: asset?.coreDetails?.assetNo ?: "UNKNOWN_ID"
    val manufacturer = asset?.coreDetails?.manufacturer ?: ""
    val model = asset?.coreDetails?.model ?: ""
    val title = "$manufacturer $model".trim().ifEmpty { "Unknown Asset" }
    val user = asset?.assignment?.user?.takeIf { it.isNotBlank() } ?: "Unassigned"
    val location = asset?.assignment?.location?.takeIf { it.isNotBlank() } ?: "Unknown Location"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Icon, ID, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(PrimaryBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.DesktopWindows, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ASSET IDENTIFIER",
                            fontSize = 10.sp,
                            color = TextLight,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = asset?.coreDetails?.serial?.takeIf { it.isNotBlank() } ?: assetNo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = TextLight, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(PrimaryBlue, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Titles
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Checked out to $user • $location",
                fontSize = 13.sp,
                color = TextLight
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// HeaderActionItem removed

@Composable
fun ExpandableSection(
    title: String,
    icon: ImageVector,
    subtitle: String? = null,
    badgeText: String? = null,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = if (isExpanded) 1.dp else 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            if (badgeText != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFFE0E7FF),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        if (subtitle != null) {
                            Text(subtitle, fontSize = 13.sp, color = TextLight)
                        }
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = "Expand",
                    tint = TextDark
                )
            }
            
            AnimatedVisibility(visible = isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun CoreDetailsContent(asset: Asset?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                DetailField("Serial Number", asset?.coreDetails?.serial?.takeIf { it.isNotBlank() } ?: "N/A")
            }
            Box(modifier = Modifier.weight(1f)) {
                DetailField("Manufacturer", asset?.coreDetails?.manufacturer?.takeIf { it.isNotBlank() } ?: "N/A")
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                DetailField("Model", asset?.coreDetails?.model?.takeIf { it.isNotBlank() } ?: "N/A")
            }
            Box(modifier = Modifier.weight(1f)) {
                DetailField("Computer Name", asset?.coreDetails?.compName?.takeIf { it.isNotBlank() } ?: "N/A", isMonospace = true)
            }
        }

        DetailField(
            label = "Active Directory OU",
            value = asset?.coreDetails?.ad?.takeIf { it.isNotBlank() } ?: "N/A",
            isMonospace = true,
            isChip = true
        )
        DetailField(
            label = "Warehouse Box Location",
            value = asset?.coreDetails?.boxLocate?.takeIf { it.isNotBlank() } ?: "N/A",
            isMonospace = true,
            isChip = true
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun AssignmentContent(asset: Asset?) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailField("User", asset?.assignment?.user?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Department", asset?.assignment?.dept?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Company", asset?.assignment?.company?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Location", asset?.assignment?.location?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Last Use", asset?.assignment?.lastUse?.takeIf { it.isNotBlank() } ?: "N/A")
    }
}

@Composable
fun HardwareContent(asset: Asset?) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailField("Processor", asset?.hardware?.processor?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("RAM", asset?.hardware?.ram?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Hard Disk", asset?.hardware?.hardisk?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Additional Hardware", asset?.hardware?.additionalHardware?.takeIf { it.isNotBlank() } ?: "N/A")
    }
}

@Composable
fun SoftwareContent(asset: Asset?) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Operating System", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 14.sp)
        DetailField("OS Name", asset?.software?.os?.name?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("OS Bit", asset?.software?.os?.bit?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Product Key", asset?.software?.os?.productKey?.takeIf { it.isNotBlank() } ?: "N/A", isMonospace = true)
        
        Spacer(modifier = Modifier.height(8.dp))
        Text("Office", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 14.sp)
        DetailField("Version", asset?.software?.office?.version?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Licence Key", asset?.software?.office?.licenceKey?.takeIf { it.isNotBlank() } ?: "N/A", isMonospace = true)
        
        Spacer(modifier = Modifier.height(8.dp))
        Text("Antivirus", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 14.sp)
        DetailField("Name", asset?.software?.antivirus?.name?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Expiry Date", asset?.software?.antivirus?.expiryDate?.takeIf { it.isNotBlank() } ?: "N/A")
        
        Spacer(modifier = Modifier.height(8.dp))
        Text("Other", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 14.sp)
        DetailField("Other Apps", asset?.software?.otherApp?.takeIf { it.isNotBlank() } ?: "N/A")
    }
}

@Composable
fun ProcurementContent(asset: Asset?) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailField("Supplier", asset?.procurement?.supplier?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("CAPEX No", asset?.procurement?.capexNo?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("PO No", asset?.procurement?.poNo?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Invoice No", asset?.procurement?.invoiceNo?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Amount", asset?.procurement?.amount?.toString()?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Warranty", asset?.procurement?.warranty?.takeIf { it.isNotBlank() } ?: "N/A")
    }
}

@Composable
fun MetadataContent(asset: Asset?) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailField("Remark 1", asset?.metadata?.remark1?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("Remark 2", asset?.metadata?.remark2?.takeIf { it.isNotBlank() } ?: "N/A")
        DetailField("File Seq", asset?.metadata?.fileSeq?.takeIf { it.isNotBlank() } ?: "N/A")
    }
}

@Composable
fun DetailField(
    label: String,
    value: String,
    isMonospace: Boolean = false,
    isChip: Boolean = false,
    showCopy: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextLight,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isChip) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = value,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                        fontSize = 13.sp,
                        color = TextDark
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (leadingIcon != null) {
                        Icon(leadingIcon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = value,
                        fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                        fontSize = 14.sp,
                        color = TextDark,
                        fontWeight = if (!isMonospace) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
            
            if (showCopy) {
                Icon(
                    Icons.Outlined.ContentCopy, 
                    contentDescription = "Copy", 
                    tint = TextLight, 
                    modifier = Modifier.size(18.dp).clickable { }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AssetDetailScreenPreview() {
    MaterialTheme {
        AssetDetailScreen(assetId = "MOCK_ID")
    }
}
