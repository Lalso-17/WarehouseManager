package com.warehouse.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.warehouse.manager.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val vm: WarehouseViewModel by viewModels {
        val db = WarehouseDatabase.get(applicationContext)
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                WarehouseViewModel(WarehouseRepository(db)) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { WarehouseApp(vm) } }
    }
}

class WarehouseViewModel(private val repo: WarehouseRepository) : ViewModel() {
    val products = repo.products.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val movements = repo.movements.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    var message by mutableStateOf<String?>(null)
        private set

    fun save(product: Product) = viewModelScope.launch {
        runCatching { repo.saveProduct(product) }
            .onSuccess { message = "Product saved" }
            .onFailure { message = it.message ?: "Could not save product" }
    }
    fun delete(product: Product) = viewModelScope.launch {
        runCatching { repo.removeProduct(product) }
            .onSuccess { message = "Product deleted" }
            .onFailure { message = it.message ?: "Could not delete product" }
    }
    fun movement(product: Product, type: String, amount: Int) = viewModelScope.launch {
        runCatching { repo.moveStock(product.id, type, amount, "") }
            .onSuccess { message = "Stock updated" }
            .onFailure { message = it.message ?: "Operation failed" }
    }
    fun clearMessage() { message = null }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseApp(vm: WarehouseViewModel) {
    val products by vm.products.collectAsStateWithLifecycle()
    val movements by vm.movements.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(0) }
    var editing by remember { mutableStateOf<Product?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var operationProduct by remember { mutableStateOf<Product?>(null) }
    var operationType by remember { mutableStateOf("IN") }
    var amount by remember { mutableStateOf("1") }
    val snackbar = remember { SnackbarHostState() }
    val message = vm.message
    LaunchedEffect(message) {
        if (message != null) { snackbar.showSnackbar(message); vm.clearMessage() }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Warehouse Manager") }) },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar {
                listOf("Dashboard", "Products", "Operations", "Reports").forEachIndexed { i, label ->
                    NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = {
                        Text(listOf("⌂", "▤", "⇄", "▥")[i])
                    }, label = { Text(label) })
                }
            }
        },
        floatingActionButton = {
            if (tab == 1) FloatingActionButton(onClick = { editing = null; showForm = true }) { Text("+") }
        }
    ) { padding ->
        when (tab) {
            0 -> Dashboard(products, movements, Modifier.padding(padding))
            1 -> ProductList(
                products, Modifier.padding(padding),
                onEdit = { editing = it; showForm = true },
                onDelete = vm::delete,
                onOperation = { operationProduct = it; operationType = "IN"; amount = "1" }
            )
            2 -> MovementList(movements, Modifier.padding(padding))
            else -> Reports(products, Modifier.padding(padding))
        }
    }

    if (showForm) ProductDialog(editing, onDismiss = { showForm = false }, onSave = {
        vm.save(it); showForm = false
    })
    operationProduct?.let { product ->
        AlertDialog(
            onDismissRequest = { operationProduct = null },
            title = { Text("Stock operation: ${product.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = operationType == "IN", onClick = { operationType = "IN" }, label = { Text("Receive") })
                        FilterChip(selected = operationType == "OUT", onClick = { operationType = "OUT" }, label = { Text("Issue") })
                    }
                    OutlinedTextField(amount, { amount = it }, label = { Text("Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Text("Available: ${product.quantity}")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    amount.toIntOrNull()?.let { vm.movement(product, operationType, it) }
                    operationProduct = null
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { operationProduct = null }) { Text("Cancel") } }
        )
    }
}

@Composable
fun Dashboard(products: List<Product>, movements: List<StockMovement>, modifier: Modifier = Modifier) {
    val low = products.count { it.quantity <= it.minQuantity }
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Overview", style = MaterialTheme.typography.headlineMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryCard("Products", products.size.toString(), Modifier.weight(1f))
            SummaryCard("Low stock", low.toString(), Modifier.weight(1f))
        }
        SummaryCard("Total units", products.sumOf { it.quantity }.toString(), Modifier.fillMaxWidth())
        Text("Recent activity", style = MaterialTheme.typography.titleLarge)
        movements.take(5).forEach { Text("${it.type} · ${it.productName} · ${it.quantity}") }
        if (movements.isEmpty()) Text("No stock operations yet.")
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) { Column(Modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.headlineMedium)
    } }
}

@Composable
fun ProductList(
    products: List<Product>, modifier: Modifier = Modifier,
    onEdit: (Product) -> Unit, onDelete: (Product) -> Unit, onOperation: (Product) -> Unit
) {
    var query by remember { mutableStateOf("") }
    Column(modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), label = { Text("Search name / SKU / barcode") }, singleLine = true)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products.filter { it.name.contains(query, true) || it.sku.contains(query, true) || it.barcode.contains(query, true) }, key = { it.id }) { p ->
                Card {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                        Text("SKU ${p.sku} · Stock ${p.quantity} · ${p.category}")
                        if (p.quantity <= p.minQuantity) Text("LOW STOCK", color = MaterialTheme.colorScheme.error)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(onClick = { onEdit(p) }) { Text("Edit") }
                            TextButton(onClick = { onOperation(p) }) { Text("IN / OUT") }
                            TextButton(onClick = { onDelete(p) }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDialog(initial: Product?, onDismiss: () -> Unit, onSave: (Product) -> Unit) {
    var name by remember(initial) { mutableStateOf(initial?.name ?: "") }
    var sku by remember(initial) { mutableStateOf(initial?.sku ?: "") }
    var barcode by remember(initial) { mutableStateOf(initial?.barcode ?: "") }
    var category by remember(initial) { mutableStateOf(initial?.category ?: "General") }
    var price by remember(initial) { mutableStateOf(initial?.price?.toString() ?: "0") }
    var qty by remember(initial) { mutableStateOf(initial?.quantity?.toString() ?: "0") }
    var min by remember(initial) { mutableStateOf(initial?.minQuantity?.toString() ?: "0") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add product" else "Edit product") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Name *") }, singleLine = true)
                OutlinedTextField(sku, { sku = it }, label = { Text("SKU *") }, singleLine = true)
                OutlinedTextField(barcode, { barcode = it }, label = { Text("Barcode / QR value") }, singleLine = true)
                OutlinedTextField(category, { category = it }, label = { Text("Category") }, singleLine = true)
                OutlinedTextField(price, { price = it }, label = { Text("Price") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(qty, { qty = it }, label = { Text("Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(min, { min = it }, label = { Text("Minimum stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val p = price.toDoubleOrNull()
                val q = qty.toIntOrNull()
                val m = min.toIntOrNull()
                error = when {
                    name.isBlank() || sku.isBlank() -> "Name and SKU are required"
                    p == null || q == null || m == null -> "Enter valid price and quantities"
                    p < 0 || q < 0 || m < 0 -> "Values cannot be negative"
                    else -> null
                }
                if (error == null) onSave(Product(initial?.id ?: 0, name.trim(), sku.trim(), barcode.trim(), category.trim(), p!!, q!!, m!!))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun MovementList(movements: List<StockMovement>, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Stock history", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            items(movements, key = { it.id }) { m ->
                ListItem(
                    headlineContent = { Text("${m.type}: ${m.productName}") },
                    supportingContent = { Text("${m.quantity} units · ${java.text.DateFormat.getDateTimeInstance().format(m.createdAt)}") }
                )
                HorizontalDivider()
            }
        }
        if (movements.isEmpty()) Text("No movements recorded.")
    }
}

@Composable
fun Reports(products: List<Product>, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            val result = runCatching {
                val output = context.contentResolver.openOutputStream(uri)
                    ?: error("Could not open the selected file")
                output.bufferedWriter().use { it.write(CsvFormatter.products(products)) }
            }
            android.widget.Toast.makeText(
                context,
                if (result.isSuccess) "CSV report exported" else "Export failed: ${result.exceptionOrNull()?.localizedMessage ?: "unknown error"}",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Reports", style = MaterialTheme.typography.headlineMedium)
        SummaryCard("Product count", products.size.toString(), Modifier.fillMaxWidth())
        SummaryCard("Units in stock", products.sumOf { it.quantity }.toString(), Modifier.fillMaxWidth())
        SummaryCard("Low-stock products", products.count { it.quantity <= it.minQuantity }.toString(), Modifier.fillMaxWidth())
        Button(onClick = { launcher.launch("warehouse-report.csv") }) { Text("Export products to CSV") }
    }
}
