package com.example.itinventory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}

// Data classes for the UI mock
data class AssetUiModel(
    val id: String,
    val typeName: String,
    val icon: ImageVector,
    val status: AssetStatus,
    val userInitials: String,
    val userName: String,
    val userRole: String,
    val userRoleColor: Color = Color.Gray,
    val location: String,
    val locationIcon: ImageVector = Icons.Outlined.Place,
    val serialNumber: String = ""
)

enum class AssetStatus(val label: String, val color: Color, val bgColor: Color) {
    ACTIVE("ACTIVE", Color(0xFF0D9344), Color(0xFFE6F4EA)),
    DISPOSED("DISPOSED", Color(0xFFD93025), Color(0xFFFCE8E6)),
    IN_REPAIR("IN REPAIR", Color(0xFFE37400), Color(0xFFFEF7E0))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: AssetViewModel? = if (LocalInspectionMode.current) null else viewModel(),
    onAssetClick: (String) -> Unit = {},
    onNewAssetClick: () -> Unit = {}
) {
    if (viewModel != null) {
        val allAssets by viewModel.assets.collectAsState()
        val filteredAssets by viewModel.filteredAssets.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        val selectedStatus by viewModel.selectedStatus.collectAsState()
        val selectedType by viewModel.selectedType.collectAsState()
        val selectedDepartment by viewModel.selectedDepartment.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        val availableTypes = remember(allAssets) { allAssets.map { it.typeName }.distinct().sorted() }
        val availableDepartments = remember(allAssets) { allAssets.map { it.userRole }.distinct().sorted() }

        HomeScreenContent(
            modifier = modifier,
            allAssets = allAssets,
            filteredAssets = filteredAssets,
            searchQuery = searchQuery,
            selectedStatus = selectedStatus,
            selectedType = selectedType,
            selectedDepartment = selectedDepartment,
            availableTypes = availableTypes,
            availableDepartments = availableDepartments,
            isLoading = isLoading,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            onStatusSelect = { viewModel.setStatusFilter(it) },
            onTypeSelect = { viewModel.setTypeFilter(it) },
            onDepartmentSelect = { viewModel.setDepartmentFilter(it) },
            onClearFilters = { viewModel.clearFilters() },
            onAssetClick = onAssetClick,
            onNewAssetClick = onNewAssetClick
        )
    } else {
        val sampleAssets = remember {
            listOf(
                AssetUiModel(
                    id = "AST-001",
                    typeName = "Laptop",
                    icon = Icons.Outlined.LaptopMac,
                    status = AssetStatus.ACTIVE,
                    userInitials = "JD",
                    userName = "John Doe",
                    userRole = "Engineering",
                    location = "Apple MacBook Pro • NYC Office",
                    serialNumber = "SN-123456"
                ),
                AssetUiModel(
                    id = "AST-002",
                    typeName = "Desktop",
                    icon = Icons.Outlined.Computer,
                    status = AssetStatus.DISPOSED,
                    userInitials = "AS",
                    userName = "Alice Smith",
                    userRole = "Design",
                    location = "Dell XPS • SF Office",
                    serialNumber = "SN-789012"
                )
            )
        }
        HomeScreenContent(
            modifier = modifier,
            allAssets = sampleAssets,
            filteredAssets = sampleAssets,
            searchQuery = "",
            selectedStatus = null,
            selectedType = null,
            selectedDepartment = null,
            availableTypes = listOf("Laptop", "Desktop"),
            availableDepartments = listOf("Engineering", "Design"),
            isLoading = false,
            onSearchQueryChange = {},
            onStatusSelect = {},
            onTypeSelect = {},
            onDepartmentSelect = {},
            onClearFilters = {},
            onAssetClick = onAssetClick,
            onNewAssetClick = onNewAssetClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    allAssets: List<AssetUiModel>,
    filteredAssets: List<AssetUiModel>,
    searchQuery: String,
    selectedStatus: AssetStatus?,
    selectedType: String?,
    selectedDepartment: String?,
    availableTypes: List<String>,
    availableDepartments: List<String>,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onStatusSelect: (AssetStatus?) -> Unit,
    onTypeSelect: (String?) -> Unit,
    onDepartmentSelect: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onAssetClick: (String) -> Unit = {},
    onNewAssetClick: () -> Unit = {}
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBar() },
        bottomBar = { },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewAssetClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Asset", fontWeight = FontWeight.SemiBold)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderSection() }
            item { 
                SearchSection(
                    query = searchQuery, 
                    onQueryChange = onSearchQueryChange,
                    onFilterClick = { showFilterSheet = true }
                ) 
            }
            item {
                FilterChips(
                    allCount = allAssets.size,
                    activeCount = allAssets.count { it.status == AssetStatus.ACTIVE },
                    disposedCount = allAssets.count { it.status == AssetStatus.DISPOSED },
                    selectedStatus = selectedStatus,
                    onStatusSelect = onStatusSelect
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (filteredAssets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No assets found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(filteredAssets) { asset ->
                    AssetCard(asset = asset, onClick = { onAssetClick(asset.id) })
                }
            }
            item { FooterSection(totalAssets = filteredAssets.size) }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Filter Assets",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text("Asset Type", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SelectableChip(
                        text = "All",
                        dotColor = null,
                        isSelected = selectedType == null,
                        onClick = { onTypeSelect(null) }
                    )
                    availableTypes.forEach { type ->
                        SelectableChip(
                            text = type,
                            dotColor = null,
                            isSelected = selectedType == type,
                            onClick = { onTypeSelect(type) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Department", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SelectableChip(
                        text = "All",
                        dotColor = null,
                        isSelected = selectedDepartment == null,
                        onClick = { onDepartmentSelect(null) }
                    )
                    availableDepartments.forEach { dept ->
                        SelectableChip(
                            text = dept,
                            dotColor = null,
                            isSelected = selectedDepartment == dept,
                            onClick = { onDepartmentSelect(dept) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onClearFilters,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear All")
                    }
                    Button(
                        onClick = { showFilterSheet = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "IT Inventory",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Asset Management",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection(
    query: String, 
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search asset tag, serial, employee...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingIcon = {
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Outlined.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun FilterChips(
    allCount: Int,
    activeCount: Int,
    disposedCount: Int,
    selectedStatus: AssetStatus?,
    onStatusSelect: (AssetStatus?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SelectableChip(
            text = "All ($allCount)",
            dotColor = null,
            isSelected = selectedStatus == null,
            onClick = { onStatusSelect(null) }
        )
        SelectableChip(
            text = "Active ($activeCount)",
            dotColor = AssetStatus.ACTIVE.color,
            isSelected = selectedStatus == AssetStatus.ACTIVE,
            onClick = { onStatusSelect(AssetStatus.ACTIVE) }
        )
        SelectableChip(
            text = "Disposed ($disposedCount)",
            dotColor = AssetStatus.DISPOSED.color,
            isSelected = selectedStatus == AssetStatus.DISPOSED,
            onClick = { onStatusSelect(AssetStatus.DISPOSED) }
        )
    }
}

@Composable
fun SelectableChip(text: String, dotColor: Color?, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = contentColor)
                Spacer(modifier = Modifier.width(4.dp))
            } else if (dotColor != null) {
                Box(modifier = Modifier.size(6.dp).background(dotColor, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal)
        }
    }
}

@Composable
fun AssetCard(asset: AssetUiModel, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(asset.status.color)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(asset.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (asset.serialNumber.isNotBlank()) asset.serialNumber else asset.id,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = asset.status.bgColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(asset.status.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = asset.status.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = asset.status.color
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = asset.typeName,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (asset.userInitials.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = asset.userInitials,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (asset.status == AssetStatus.IN_REPAIR) Icons.Outlined.Warning else Icons.Outlined.GroupWork,
                                    contentDescription = null,
                                    tint = if (asset.status == AssetStatus.IN_REPAIR) asset.status.color else MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = asset.userName,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = asset.userRole,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(asset.locationIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = asset.location,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Details", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun FooterSection(totalAssets: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "END OF ACTIVE CACHE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$totalAssets remaining assets indexed on network",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(Icons.Outlined.Inventory2, contentDescription = "Assets") },
            label = { Text("Assets", fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan") },
            label = { Text("Scan / QR", fontWeight = if (selectedIndex == 1) FontWeight.Medium else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) },
            icon = { Icon(Icons.Outlined.Tune, contentDescription = "Settings") },
            label = { Text("Settings", fontWeight = if (selectedIndex == 2) FontWeight.Medium else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
