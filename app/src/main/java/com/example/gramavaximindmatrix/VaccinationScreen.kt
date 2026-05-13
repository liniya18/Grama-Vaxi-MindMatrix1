package com.example.gramavaximindmatrix

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScreen(
    viewModel: VaccinationViewModel,
    onBackClick: () -> Unit,
    onAddVaccinationClick: () -> Unit
) {
    val records by viewModel.vaccinationRecords.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<VaccinationStatus?>(null) }

    val filteredRecords = remember(records, searchQuery, selectedStatus) {
        records.filter { record ->
            val matchesSearch = (record.animalName?.contains(searchQuery, ignoreCase = true) ?: false) ||
                    record.vaccineName.contains(searchQuery, ignoreCase = true)
            val matchesFilter = selectedStatus == null || record.status == selectedStatus
            matchesSearch && matchesFilter
        }
    }

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.vaccination_records),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddVaccinationClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(id = R.string.add_vaccination_title), fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(padding)
        ) {
            if (records.isEmpty()) {
                EmptyVaccinationState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Statistics / Summary Row
                    item {
                        VaccinationSummaryRow(records)
                    }

                    // Search Bar
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text(stringResource(R.string.search_records)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = if (searchQuery.isNotEmpty()) {
                                {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = null)
                                    }
                                }
                            } else null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            singleLine = true
                        )
                    }

                    // Filter Chips
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedStatus == null,
                                    onClick = { selectedStatus = null },
                                    label = { Text(stringResource(R.string.filter_all)) },
                                    leadingIcon = if (selectedStatus == null) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            items(VaccinationStatus.values()) { status ->
                                val label = when (status) {
                                    VaccinationStatus.OVERDUE -> stringResource(R.string.filter_overdue)
                                    VaccinationStatus.UPCOMING -> stringResource(R.string.filter_upcoming)
                                    VaccinationStatus.SAFE -> stringResource(R.string.filter_safe)
                                }
                                FilterChip(
                                    selected = selectedStatus == status,
                                    onClick = { selectedStatus = status },
                                    label = { Text(label) },
                                    leadingIcon = if (selectedStatus == status) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = if (searchQuery.isEmpty() && selectedStatus == null) 
                                stringResource(id = R.string.all_records) 
                            else 
                                "${stringResource(R.string.all_records)} (${filteredRecords.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    if (filteredRecords.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.no_results),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    } else {
                        itemsIndexed(filteredRecords, key = { _, record -> record.id }) { index, record ->
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(record.id) { visible = true }
                            
                            AnimatedVisibility(
                                visible = visible,
                                enter = slideInVertically(
                                    initialOffsetY = { 50 * (index + 1) },
                                    animationSpec = tween(durationMillis = 500, delayMillis = index * 50)
                                ) + fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = index * 50))
                            ) {
                                VaccinationCard(record)
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(80.dp)) } // Space for FAB
                }
            }
        }
    }
}

@Composable
fun VaccinationSummaryRow(records: List<VaccinationDisplayRecord>) {
    val overdue = records.count { it.status == VaccinationStatus.OVERDUE }
    val upcoming = records.count { it.status == VaccinationStatus.UPCOMING }
    val safe = records.count { it.status == VaccinationStatus.SAFE }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryStatItem(
            label = "Overdue",
            count = overdue,
            color = MaterialTheme.colorScheme.error,
            icon = Icons.Default.PriorityHigh,
            modifier = Modifier.weight(1f)
        )
        SummaryStatItem(
            label = "Upcoming",
            count = upcoming,
            color = Color(0xFFFBC02D),
            icon = Icons.Default.Update,
            modifier = Modifier.weight(1f)
        )
        SummaryStatItem(
            label = "Safe",
            count = safe,
            color = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.CheckCircle,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryStatItem(label: String, count: Int, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun VaccinationCard(record: VaccinationDisplayRecord) {
    val isDark = isSystemInDarkTheme()
    
    val statusColor = when (record.status) {
        VaccinationStatus.OVERDUE -> MaterialTheme.colorScheme.error
        VaccinationStatus.UPCOMING -> if (isDark) Color(0xFFFBC02D) else Color(0xFFF9A825)
        VaccinationStatus.SAFE -> MaterialTheme.colorScheme.primary
    }

    val statusBg = when (record.status) {
        VaccinationStatus.OVERDUE -> if (isDark) Color(0xFF421010) else Color(0xFFFFEBEE)
        VaccinationStatus.UPCOMING -> if (isDark) Color(0xFF332B00) else Color(0xFFFFFDE7)
        VaccinationStatus.SAFE -> if (isDark) Color(0xFF0D2D10) else Color(0xFFE8F5E9)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        color = statusColor.copy(alpha = 0.12f),
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vaccines,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = record.animalName ?: stringResource(id = R.string.unknown_animal),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = record.vaccineName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(12.dp),
                    border = if (isDark) null else BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusIcon = when (record.status) {
                            VaccinationStatus.OVERDUE -> Icons.Default.Dangerous
                            VaccinationStatus.UPCOMING -> Icons.Default.NotificationImportant
                            VaccinationStatus.SAFE -> Icons.Default.CheckCircle
                        }
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = statusColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = record.status.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = statusColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn(
                    label = stringResource(id = R.string.vaccinated_on),
                    value = record.date,
                    icon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )

                InfoColumn(
                    label = stringResource(id = R.string.next_due),
                    value = record.nextDueDate,
                    icon = Icons.Default.EventAvailable,
                    color = statusColor,
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                )
            }
        }
    }
}

@Composable
fun InfoColumn(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (horizontalAlignment == Alignment.Start) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium
            )
            if (horizontalAlignment == Alignment.End) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = color.copy(alpha = 0.7f)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (color == MaterialTheme.colorScheme.onSurfaceVariant) MaterialTheme.colorScheme.onSurface else color
        )
    }
}

@Composable
fun EmptyVaccinationState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = CircleShape,
                modifier = Modifier.size(120.dp)
            ) {
                Icon(
                    Icons.Default.Vaccines,
                    contentDescription = null,
                    modifier = Modifier.padding(32.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.no_vaccination_records),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
