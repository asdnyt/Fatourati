package com.facturation.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import com.facturation.client.ui.theme.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.facturation.client.data.local.ClientWithBalance
import com.facturation.client.ui.MainViewModel

@Composable
fun ClientsScreen(
    viewModel: MainViewModel,
    onClientClick: (ClientWithBalance) -> Unit
) {
    val clients by viewModel.clientsWithBalance.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val filtered = clients.filter {
        it.name.contains(query, ignoreCase = true) || it.phone.contains(query)
    }
    val debtCount = clients.count { it.remainingBalance > 0 }
    val totalDebt = clients.sumOf { it.remainingBalance }

    Column(modifier = Modifier.fillMaxSize().background(NavyBg)) {
        Column(
            modifier = Modifier.fillMaxWidth().background(BlueHeader).padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(46.dp).clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.People, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Clients", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text("${clients.size} clients enregistrés", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BlueHeader)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Nouveau", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Rechercher par nom ou téléphone...", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.8f)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().background(NavyBar).padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Créances en cours : $debtCount", color = TextMuted, fontSize = 13.sp)
            Text("Solde total : %.2f DH".format(totalDebt), color = RedDebt, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucun client. Appuyez sur Nouveau pour ajouter.", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { client ->
                    ClientCard(client = client, onClick = { onClientClick(client) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddClientDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, address ->
                viewModel.addClient(name, phone, address)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ClientCard(client: ClientWithBalance, onClick: () -> Unit) {
    val owes = client.remainingBalance > 0
    val accent = if (owes) RedDebt else GreenPay

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(NavyCard)
            .clickable(onClick = onClick)
            .height(IntrinsicSize.Min)
    ) {
        Box(modifier = Modifier.width(5.dp).fillMaxHeight().background(accent))
        Column(modifier = Modifier.padding(16.dp).weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(client.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp, modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text("SOLDE RESTANT", color = TextMuted, fontSize = 10.sp)
                    Text("%.2f DH".format(client.remainingBalance), color = accent, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(
                        if (owes) "Reste à payer" else "Soldé",
                        color = accent,
                        fontSize = 10.sp,
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                            .background(accent.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp), tint = BlueHeader)
                Spacer(Modifier.width(6.dp))
                Text(client.phone, color = TextMuted, fontSize = 14.sp)
            }
            if (client.address.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(15.dp), tint = TextMuted)
                    Spacer(Modifier.width(6.dp))
                    Text(client.address, color = TextMuted, fontSize = 13.sp, maxLines = 1)
                }
            }
            Spacer(Modifier.height(10.dp))
            Divider(color = Color(0xFF1E2D4D))
            Spacer(Modifier.height(10.dp))
            Text("Créer une facture  ›", color = Color(0xFF60A5FA), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun AddClientDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un client", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du client *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse (facultatif)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank() && phone.isNotBlank()) onConfirm(name, phone, address) },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
