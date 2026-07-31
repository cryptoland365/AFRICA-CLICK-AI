package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.MainViewModel

/**
 * UserDashboardScreen delegates directly to UserProfileScreen for full profile & dashboard functionality.
 */
@Composable
fun UserDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    UserProfileScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
