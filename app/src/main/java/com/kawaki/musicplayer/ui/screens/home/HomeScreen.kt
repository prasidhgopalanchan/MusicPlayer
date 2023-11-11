package com.kawaki.musicplayer.ui.screens.home

import android.Manifest
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.kawaki.musicplayer.ui.components.AudioCards
import com.kawaki.musicplayer.ui.components.checkStoragePermission

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Enable notification in settings", Toast.LENGTH_LONG).show()
            }
        }
    )

    val audioListState = viewModel.audioList.collectAsState(initial = listOf())
    val mediaItemList = mutableListOf<Uri>()
    audioListState.value.forEach { audio ->
        mediaItemList.add(audio.uri)
    }

    val playerState = viewModel.playerState.collectAsState()

    LaunchedEffect(key1 = true) {
        if (audioListState.value.isNotEmpty()) viewModel.addMediaItems(uriList = mediaItemList)
    }

    LaunchedEffect(key1 = true) {
        if (!checkStoragePermission(context)) {
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AudioCards(
                audioList = audioListState.value,
                viewModel = viewModel
            )
        }
    }
}