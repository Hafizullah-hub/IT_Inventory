package com.example.itinventory

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun MainScreen() {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedAssetId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingAssetId by rememberSaveable { mutableStateOf<String?>(null) }

    var showNewAssetScreen by rememberSaveable { mutableStateOf(false) }
    
    var scannedAssetNo by rememberSaveable { mutableStateOf("") }
    var scannedSerial by rememberSaveable { mutableStateOf("") }
    var scannedCapex by rememberSaveable { mutableStateOf("") }
    var scannedDate by rememberSaveable { mutableStateOf("") }

    if (showNewAssetScreen) {
        NewAssetScreen(
            initialAssetNo = scannedAssetNo,
            initialSerial = scannedSerial,
            initialCapex = scannedCapex,
            initialDate = scannedDate,
            onBackClick = { 
                showNewAssetScreen = false
                scannedAssetNo = ""
                scannedSerial = ""
                scannedCapex = ""
                scannedDate = ""
            }
        )
        return
    }

    if (editingAssetId != null) {
        EditAssetScreen(
            assetId = editingAssetId!!,
            onBackClick = { editingAssetId = null },
            onDeleteCompleted = {
                editingAssetId = null
                selectedAssetId = null // Route back to the home dashboard
            }
        )
        return
    }

    if (selectedAssetId != null) {
        AssetDetailScreen(
            assetId = selectedAssetId!!,
            onBackClick = { selectedAssetId = null },
            onEditClick = { editingAssetId = selectedAssetId }
        )
        return
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        }
    ) { paddingValues ->
        when (selectedIndex) {
            0 -> HomeScreen(
                    modifier = Modifier.padding(paddingValues),
                    onAssetClick = { selectedAssetId = it },
                    onNewAssetClick = { showNewAssetScreen = true }
                 )
            1 -> OcrIngestionScreen(
                    onBackClick = { selectedIndex = 0 },
                    onProceed = { assetNo, serial, capex, date ->
                        scannedAssetNo = assetNo
                        scannedSerial = serial
                        scannedCapex = capex
                        scannedDate = date
                        showNewAssetScreen = true
                        selectedIndex = 0
                    }
                 )
            2 -> SettingsScreen(modifier = Modifier.padding(paddingValues))
        }
    }
}
