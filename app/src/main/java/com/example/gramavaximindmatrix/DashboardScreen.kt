package com.example.gramavaximindmatrix

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddAnimalClick: () -> Unit,
    onVaccinationClick: () -> Unit,
    onReportsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onVetCampsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onEmergencyClick: () -> Unit
) {
    val animals by viewModel.animals.collectAsState()
    val healthAlerts by viewModel.healthAlerts.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val upcomingCamp by viewModel.upcomingCamp.collectAsState()
    var showReminderDialog by remember { mutableStateOf(false) }

    val gradientBrush = Brush.verticalGradient(
        colors = if (isSystemInDarkTheme()) {
            listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.background
            )
        } else {
            listOf(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                MaterialTheme.colorScheme.background
            )
        }
    )

    LaunchedEffect(healthAlerts) {
        if (healthAlerts.any { it.type == AlertType.OVERDUE }) {
            showReminderDialog = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(id = R.string.farmer_profile),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Statistics Section
                item {
                    StatisticsRow(stats)
                }

                // Health Alerts Section
                if (healthAlerts.isNotEmpty()) {
                    item {
                        SectionHeader(stringResource(id = R.string.health_alerts), Icons.Default.NewReleases)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            healthAlerts.forEach { alert ->
                                HealthAlertCard(alert)
                            }
                        }
                    }
                }

                // Upcoming Camp Section
                upcomingCamp?.let { camp ->
                    item {
                        SectionHeader(stringResource(id = R.string.upcoming_camp), Icons.AutoMirrored.Filled.EventNote)
                        UpcomingCampCard(camp = camp, onClick = onVetCampsClick)
                    }
                }

                // Dashboard Actions Section
                item {
                    SectionHeader(stringResource(id = R.string.quick_actions), Icons.Default.GridView)
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DashboardActionCard(
                                title = stringResource(id = R.string.add_animal),
                                icon = Icons.Default.Pets,
                                modifier = Modifier.weight(1f),
                                onClick = onAddAnimalClick,
                                colors = listOf(Color(0xFF43A047), Color(0xFF2E7D32))
                            )
                            DashboardActionCard(
                                title = stringResource(id = R.string.vaccination),
                                icon = Icons.Default.Vaccines,
                                modifier = Modifier.weight(1f),
                                onClick = onVaccinationClick,
                                colors = listOf(Color(0xFF1E88E5), Color(0xFF1565C0))
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DashboardActionCard(
                                title = stringResource(id = R.string.vet_camps),
                                icon = Icons.Default.Campaign,
                                modifier = Modifier.weight(1f),
                                onClick = onVetCampsClick,
                                colors = listOf(Color(0xFFFB8C00), Color(0xFFEF6C00))
                            )
                            DashboardActionCard(
                                title = stringResource(id = R.string.reports),
                                icon = Icons.Default.Assessment,
                                modifier = Modifier.weight(1f),
                                onClick = onReportsClick,
                                colors = listOf(Color(0xFF8E24AA), Color(0xFF6A1B9A))
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DashboardActionCard(
                                title = stringResource(id = R.string.ai_assistant),
                                icon = Icons.Default.AutoAwesome,
                                modifier = Modifier.weight(1f),
                                onClick = onAiAssistantClick,
                                colors = listOf(Color(0xFFE91E63), Color(0xFFC2185B))
                            )
                            DashboardActionCard(
                                title = stringResource(id = R.string.emergency),
                                icon = Icons.Default.Error,
                                modifier = Modifier.weight(1f),
                                onClick = onEmergencyClick,
                                colors = listOf(Color(0xFFD32F2F), Color(0xFFB71C1C))
                            )
                        }
                    }
                }

                // Animals List Section
                item {
                    SectionHeader(stringResource(id = R.string.my_animals) + " (${animals.size})", Icons.AutoMirrored.Filled.FormatListBulleted)
                }

                if (animals.isEmpty()) {
                    item {
                        EmptyStateUI()
                    }
                } else {
                    itemsIndexed(animals) { index, animal ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { visible = true }
                        AnimatedVisibility(
                            visible = visible,
                            enter = slideInVertically(
                                initialOffsetY = { 50 * (index + 1) },
                                animationSpec = tween(durationMillis = 500, delayMillis = index * 50)
                            ) + fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = index * 50))
                        ) {
                            AnimalListItem(animal)
                        }
                    }
                }
            }
        }

        // Reminder Popup UI
        if (showReminderDialog) {
            val overdueAlerts = healthAlerts.filter { it.type == AlertType.OVERDUE }
            if (overdueAlerts.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { showReminderDialog = false },
                    icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    title = { Text(stringResource(id = R.string.overdue_alerts)) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            overdueAlerts.take(3).forEach { alert ->
                                Text(
                                    text = "• ${alert.animalName}: ${alert.vaccineName} (${alert.dueDate})",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            if (overdueAlerts.size > 3) {
                                Text(text = "...and ${overdueAlerts.size - 3} more", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showReminderDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(stringResource(id = R.string.ok))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun StatisticsRow(stats: DashboardStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatItem(
            label = "Animals",
            value = stats.totalAnimals.toString(),
            icon = Icons.Default.Pets,
            gradient = listOf(Color(0xFF66BB6A), Color(0xFF43A047)),
            modifier = Modifier.weight(1f)
        )
        StatItem(
            label = "Safe",
            value = stats.vaccinatedToday.toString(),
            icon = Icons.Default.VerifiedUser,
            gradient = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5)),
            modifier = Modifier.weight(1f)
        )
        StatItem(
            label = "Alerts",
            value = stats.upcomingAlerts.toString(),
            icon = Icons.Default.ReportProblem,
            gradient = listOf(Color(0xFFEF5350), Color(0xFFE53935)),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector, gradient: List<Color>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradient))
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HealthAlertCard(alert: HealthAlert) {
    val isDark = isSystemInDarkTheme()
    val containerColor = when (alert.type) {
        AlertType.OVERDUE -> if (isDark) Color(0xFF311212) else Color(0xFFFFEBEE)
        AlertType.UPCOMING -> if (isDark) Color(0xFF332B00) else Color(0xFFFFFDE7)
    }
    
    val contentColor = when (alert.type) {
        AlertType.OVERDUE -> if (isDark) Color(0xFFFFCDD2) else Color(0xFFC62828)
        AlertType.UPCOMING -> if (isDark) Color(0xFFFFF59D) else Color(0xFFFBC02D)
    }

    val icon = when (alert.type) {
        AlertType.OVERDUE -> Icons.Default.Dangerous
        AlertType.UPCOMING -> Icons.Default.NotificationImportant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        border = if (isDark) null else androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${alert.animalName}: ${alert.vaccineName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = if (alert.type == AlertType.OVERDUE) 
                        "${stringResource(id = R.string.due_on)} ${alert.dueDate}"
                        else "${stringResource(id = R.string.upcoming_vaccination)}: ${alert.dueDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun UpcomingCampCard(camp: CampEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        val gradient = Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.tertiaryContainer
            )
        )
        Row(
            modifier = Modifier
                .background(gradient)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = camp.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "${camp.date} • ${camp.location}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun DashboardActionCard(
    title: String,
    icon: ImageVector,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(colors))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AnimalListItem(animal: AnimalEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                if (animal.imageUri != null) {
                    AsyncImage(
                        model = animal.imageUri,
                        contentDescription = animal.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${animal.species} • ${animal.breed}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${animal.age} " + stringResource(id = R.string.years_label),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateUI() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = CircleShape,
            modifier = Modifier.size(120.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                modifier = Modifier.padding(32.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.no_animals_yet),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Medium
        )
    }
}
