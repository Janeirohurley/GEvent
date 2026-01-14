package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.data.model.Transaction
import com.janeirohurley.gevent.viewmodel.AuthViewModel
import com.janeirohurley.gevent.viewmodel.UserViewModel
import com.janeirohurley.gevent.viewmodel.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    authViewModel: AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    walletViewModel: WalletViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val authUser by authViewModel.userProfile.collectAsState()
    val user = currentUser ?: authUser
    
    val balance by walletViewModel.balance.collectAsState()
    val transactions by walletViewModel.transactions.collectAsState()
    val isLoading by walletViewModel.isLoading.collectAsState()
    
    var showDepositDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        if (currentUser == null) userViewModel.loadProfile()
        walletViewModel.loadBalance()
        walletViewModel.loadTransactions()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { authViewModel.logout() },) {
                        Icon(painterResource(R.drawable.fi_rr_sign_out), "deconnection")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header avec photo
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (user?.profileImage != null) {
                            AsyncImage(
                                model = user.profileImage,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                painterResource(R.drawable.fi_rr_portrait),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = user?.let {
                                    if (!it.firstName.isNullOrBlank() && !it.lastName.isNullOrBlank()) {
                                        "${it.firstName} ${it.lastName}"
                                    } else it.username
                                } ?: "Utilisateur",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user?.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            // Wallet Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Mon Wallet",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    painterResource(R.drawable.fi_rr_wallet),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            
                            Text(
                                text = "${balance?.balance ?: "0"} ${balance?.currency ?: "Fbu"}",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(Modifier.height(16.dp))
                            
                            Button(
                                onClick = { showDepositDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    painterResource(R.drawable.fi_rr_plus),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Recharger", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            
            // Transactions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Transactions récentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { /* Voir tout */ }) {
                        Text("Voir tout")
                    }
                }
            }
            
            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Aucune transaction",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                items(transactions.take(5)) { transaction ->
                    TransactionItem(transaction)
                }
            }
            
            // Actions rapides
            item {
                Text(
                    "Actions rapides",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon = R.drawable.fi_rr_edit,
                        label = "Modifier profil",
                        modifier = Modifier.weight(1f),
                        onClick = { /* TODO */ }
                    )
                    QuickActionCard(
                        icon = R.drawable.fi_rr_settings,
                        label = "Paramètres",
                        modifier = Modifier.weight(1f),
                        onClick = { navController?.navigate("setting") }
                    )
                }
            }
            

        }
    }
    
    if (showDepositDialog) {
        DepositDialog(
            onDismiss = { showDepositDialog = false },
            onDeposit = { amount ->
                walletViewModel.deposit(amount)
                showDepositDialog = false
            }
        )
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = when (transaction.transactionType) {
                        "deposit" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        "purchase" -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        "refund" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painterResource(
                            when (transaction.transactionType) {
                                "deposit" -> R.drawable.fi_rr_plus
                                "purchase" -> R.drawable.fi_rr_shopping_cart
                                "refund" -> R.drawable.fi_rr_time_past
                                else -> R.drawable.fi_rr_wallet
                            }
                        ),
                        contentDescription = null,
                        tint = when (transaction.transactionType) {
                            "deposit" -> Color(0xFF4CAF50)
                            "purchase" -> MaterialTheme.colorScheme.error
                            "refund" -> Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(8.dp).size(20.dp)
                    )
                }
                
                Spacer(Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.description,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = com.janeirohurley.gevent.utils.DateUtils.formatDateFromString(
                            transaction.createdAt,
                            "yyyy-MM-dd'T'HH:mm:ss",
                            "dd MMM yyyy, HH:mm"
                        ) ?: transaction.createdAt,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(Modifier.width(8.dp))
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.amount.toDoubleOrNull() ?: 0.0 >= 0) "+" else ""}${transaction.amount} Fbu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (transaction.amount.toDoubleOrNull() ?: 0.0 >= 0) 
                        Color(0xFF4CAF50) 
                    else 
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DepositDialog(
    onDismiss: () -> Unit,
    onDeposit: (String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recharger le wallet") },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Montant (Fbu)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (amount.isNotBlank()) onDeposit(amount) },
                enabled = amount.isNotBlank()
            ) {
                Text("Recharger")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
