package com.example.gramavaximindmatrix

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(
    navController: NavHostController,
    onLanguageChange: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(database.animalDao(), database.vaccinationDao(), database.campDao())
            )
            DashboardScreen(
                viewModel = dashboardViewModel,
                onAddAnimalClick = { navController.navigate("add_animal") },
                onVaccinationClick = { navController.navigate("vaccination") },
                onReportsClick = { navController.navigate("reports") },
                onSettingsClick = { navController.navigate("settings") },
                onAiAssistantClick = { navController.navigate("ai_assistant") },
                onVetCampsClick = { navController.navigate("camp_list") },
                onProfileClick = { navController.navigate("profile") },
                onEmergencyClick = { navController.navigate("emergency") }
            )
        }
        composable("profile") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(database.animalDao())
            )
            ProfileScreen(
                viewModel = profileViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("emergency") {
            EmergencyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("add_animal") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val addAnimalViewModel: AddAnimalViewModel = viewModel(
                factory = AddAnimalViewModelFactory(database.animalDao())
            )
            
            AddAnimalScreen(
                viewModel = addAnimalViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("vaccination") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val vaccinationViewModel: VaccinationViewModel = viewModel(
                factory = VaccinationViewModelFactory(database.animalDao(), database.vaccinationDao())
            )
            VaccinationScreen(
                viewModel = vaccinationViewModel,
                onBackClick = { navController.popBackStack() },
                onAddVaccinationClick = { navController.navigate("add_vaccination") }
            )
        }
        composable("add_vaccination") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val viewModel: AddVaccinationViewModel = viewModel(
                factory = AddVaccinationViewModelFactory(database.animalDao(), database.vaccinationDao())
            )
            AddVaccinationScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("reports") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val viewModel: ReportsViewModel = viewModel(
                factory = ReportsViewModelFactory(database.animalDao(), database.vaccinationDao())
            )
            ReportsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLanguageChange = onLanguageChange
            )
        }
        composable("ai_assistant") {
            AiAssistantScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("camp_list") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val viewModel: CampViewModel = viewModel(
                factory = CampViewModelFactory(database.campDao())
            )
            CampListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onAddCampClick = { navController.navigate("add_camp") }
            )
        }
        composable("add_camp") {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val viewModel: CampViewModel = viewModel(
                factory = CampViewModelFactory(database.campDao())
            )
            AddCampScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
