package com.example.itinventory

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.LaptopMac
import androidx.compose.material.icons.outlined.Tv
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope

class AssetViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _assets = MutableStateFlow<List<AssetUiModel>>(emptyList())
    val assets: StateFlow<List<AssetUiModel>> = _assets.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<AssetStatus?>(null)
    val selectedStatus: StateFlow<AssetStatus?> = _selectedStatus.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    private val _selectedDepartment = MutableStateFlow<String?>(null)
    val selectedDepartment: StateFlow<String?> = _selectedDepartment.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val filteredAssets: StateFlow<List<AssetUiModel>> = combine(
        _assets, _searchQuery, _selectedStatus, _selectedType, _selectedDepartment
    ) { assetList, query, status, type, department ->
        assetList.filter { asset ->
            val matchesQuery = query.isBlank() ||
                    asset.id.contains(query, ignoreCase = true) ||
                    asset.userName.contains(query, ignoreCase = true) ||
                    asset.typeName.contains(query, ignoreCase = true) ||
                    asset.location.contains(query, ignoreCase = true) ||
                    asset.serialNumber.contains(query, ignoreCase = true)
            
            val matchesStatus = status == null || asset.status == status
            val matchesType = type == null || asset.typeName.equals(type, ignoreCase = true)
            val matchesDepartment = department == null || asset.userRole.equals(department, ignoreCase = true)

            matchesQuery && matchesStatus && matchesType && matchesDepartment
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setStatusFilter(status: AssetStatus?) { _selectedStatus.value = status }
    fun setTypeFilter(type: String?) { _selectedType.value = type }
    fun setDepartmentFilter(department: String?) { _selectedDepartment.value = department }
    fun clearFilters() {
        _selectedStatus.value = null
        _selectedType.value = null
        _selectedDepartment.value = null
    }

    init {
        fetchAssets()
    }

    private fun fetchAssets() {
        _isLoading.value = true
        db.collection("assets")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("AssetViewModel", "Listen failed.", exception)
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val assetList = mutableListOf<AssetUiModel>()
                    for (document in snapshot.documents) {
                        try {
                            val asset = document.toObject(Asset::class.java)
                            if (asset != null) {
                                if (asset.documentId == null) {
                                    asset.documentId = document.id
                                }
                                assetList.add(mapToUiModel(asset))
                            }
                        } catch (e: Exception) {
                            Log.e("AssetViewModel", "Error parsing document ${document.id}", e)
                        }
                    }
                    _assets.value = assetList
                }
                _isLoading.value = false
            }
    }

    private fun mapToUiModel(asset: Asset): AssetUiModel {
        val type = asset.coreDetails?.type ?: "Unknown Type"
        val manufacturer = asset.coreDetails?.manufacturer ?: ""
        val model = asset.coreDetails?.model ?: ""
        val location = asset.assignment?.location ?: "Unknown Location"
        val user = asset.assignment?.user ?: "Unassigned"
        val dept = asset.assignment?.dept ?: "Unknown Dept"

        val icon = when (type.lowercase()) {
            "laptop" -> Icons.Outlined.LaptopMac
            "desktop" -> Icons.Outlined.Computer
            "monitor" -> Icons.Outlined.Tv
            else -> Icons.Outlined.Devices
        }

        // Get up to 2 initials from the user's name
        val userInitials = user.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")

        // Determine status (Fallback to ACTIVE as Asset.java doesn't have a status field)
        val status = AssetStatus.ACTIVE

        return AssetUiModel(
            id = asset.documentId ?: asset.coreDetails?.assetNo ?: "UNKNOWN",
            typeName = type,
            icon = icon,
            status = status,
            userInitials = userInitials,
            userName = user,
            userRole = dept,
            location = "$manufacturer $model • $location",
            serialNumber = asset.coreDetails?.serial ?: ""
        )
    }
}
