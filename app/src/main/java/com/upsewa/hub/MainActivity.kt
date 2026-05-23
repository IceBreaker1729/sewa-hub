package com.upsewa.hub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.upsewa.hub.data.Catalog
import com.upsewa.hub.data.Category
import com.upsewa.hub.data.Prefs
import com.upsewa.hub.data.Service
import com.upsewa.hub.ui.theme.UPSewaTheme
import com.upsewa.hub.webview.ServiceWebViewActivity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UPSewaTheme { AppRoot() }
        }
    }
}

/* ----------------------------------- Root + Nav ----------------------------------- */

private object Routes {
    const val HOME = "home"
    const val FAV = "favorites"
    const val ABOUT = "about"
    const val CATEGORY = "category/{id}"
    const val DETAIL = "detail/{id}"
    fun category(id: String) = "category/$id"
    fun detail(id: String) = "detail/$id"
}

@Composable
private fun AppRoot() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val lang by Prefs.languageFlow(context).collectAsStateWithLifecycle(initialValue = "en")
    val favs by Prefs.favoritesFlow(context).collectAsStateWithLifecycle(initialValue = emptySet())

    val onToggleFav: (String) -> Unit = { id ->
        scope.launch { Prefs.toggleFavorite(context, id) }
    }
    val onSetLang: (String) -> Unit = { l ->
        scope.launch { Prefs.setLanguage(context, l) }
    }
    val onOpenService: (Service) -> Unit = { svc ->
        val i = Intent(context, ServiceWebViewActivity::class.java).apply {
            putExtra(ServiceWebViewActivity.EXTRA_URL, svc.url)
            putExtra(ServiceWebViewActivity.EXTRA_TITLE, svc.name(lang))
        }
        context.startActivity(i)
    }

    Scaffold(
        bottomBar = { BottomBar(nav, lang) }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding).fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable(Routes.HOME) {
                HomeScreen(lang, favs, onSetLang, onToggleFav, onOpenService, nav)
            }
            composable(Routes.FAV) {
                FavoritesScreen(lang, favs, onToggleFav, onOpenService, nav)
            }
            composable(Routes.ABOUT) {
                AboutScreen(lang)
            }
            composable(Routes.CATEGORY) { backStack ->
                val id = backStack.arguments?.getString("id") ?: return@composable
                val cat = Catalog.categories.firstOrNull { it.id == id } ?: return@composable
                CategoryScreen(cat, lang, favs, onToggleFav, onOpenService, nav)
            }
            composable(Routes.DETAIL) { backStack ->
                val id = backStack.arguments?.getString("id") ?: return@composable
                val svc = Catalog.byId(id) ?: return@composable
                DetailScreen(svc, lang, favs, onToggleFav, onOpenService, nav)
            }
        }
    }
}

@Composable
private fun BottomBar(nav: NavHostController, lang: String) {
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = current == Routes.HOME,
            onClick = { nav.navigate(Routes.HOME) { launchSingleTop = true; popUpTo(Routes.HOME) } },
            icon = { Icon(Icons.Outlined.Home, null) },
            label = { Text(if (lang == "hi") "होम" else "Home") }
        )
        NavigationBarItem(
            selected = current == Routes.FAV,
            onClick = { nav.navigate(Routes.FAV) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.StarBorder, null) },
            label = { Text(if (lang == "hi") "सहेजे" else "Saved") }
        )
        NavigationBarItem(
            selected = current == Routes.ABOUT,
            onClick = { nav.navigate(Routes.ABOUT) { launchSingleTop = true } },
            icon = { Icon(Icons.Outlined.Info, null) },
            label = { Text(if (lang == "hi") "बारे में" else "About") }
        )
    }
}

/* ----------------------------------- Home ----------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    lang: String,
    favs: Set<String>,
    onSetLang: (String) -> Unit,
    onToggleFav: (String) -> Unit,
    onOpenService: (Service) -> Unit,
    nav: NavHostController
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query, lang) {
        if (query.isBlank()) emptyList() else Catalog.search(query, lang)
    }

    Column(Modifier.fillMaxSize()) {
        // Header with brand bar + language toggle + search
        Surface(color = MaterialTheme.colorScheme.primary, tonalElevation = 0.dp) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(9.dp))
                            .background(Color(0xFFC79A3A)),
                        contentAlignment = Alignment.Center
                    ) { Text("उ", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                              color = Color(0xFF1A1306)) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.app_name),
                             color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(stringResource(R.string.app_tagline),
                             color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                    LanguageToggle(lang, onSetLang)
                }
                Spacer(Modifier.height(12.dp))
                SearchBox(query, onChange = { query = it })
            }
        }

        if (results.isNotEmpty() || query.isNotBlank()) {
            // Search results
            LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
                item {
                    Text(
                        "🔎 \"$query\" — ${results.size} ${stringResource(R.string.services_count)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 8.dp)
                    )
                }
                if (results.isEmpty()) item { EmptyState("🤷", stringResource(R.string.no_results)) }
                items(results, key = { it.id }) { ServiceRow(it, lang, favs.contains(it.id), onToggleFav, onOpenService) }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)) {
                item {
                    Text(
                        if (lang == "hi")
                            "उत्तर प्रदेश की सभी सरकारी सेवाएँ, एक ही जगह।"
                        else
                            "All Uttar Pradesh government services, in one place.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp, 6.dp)
                    )
                }
                item { SectionLabel(stringResource(R.string.section_popular)) }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(Catalog.services.filter { it.popular }, key = { it.id }) { svc ->
                            QuickCard(svc, lang) { onOpenService(svc) }
                        }
                    }
                }
                item { SectionLabel(stringResource(R.string.section_categories)) }
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.heightIn(max = 1000.dp),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        userScrollEnabled = false
                    ) {
                        items(Catalog.categories, key = { it.id }) { cat ->
                            CategoryCard(cat, lang) { nav.navigate(Routes.category(cat.id)) }
                        }
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}

/* ----------------------------------- Category list ----------------------------------- */

@Composable
private fun CategoryScreen(
    cat: Category,
    lang: String,
    favs: Set<String>,
    onToggleFav: (String) -> Unit,
    onOpenService: (Service) -> Unit,
    nav: NavHostController
) {
    val items = remember(cat.id) { Catalog.byCategory(cat.id) }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        BackButton(nav)
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)) {
            Box(
                Modifier.size(46.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) { Text(cat.icon, fontSize = 22.sp) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(cat.name(lang), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                     color = MaterialTheme.colorScheme.onBackground)
                Text(cat.desc(lang), fontSize = 12.sp,
                     color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items, key = { it.id }) {
                ServiceRow(it, lang, favs.contains(it.id), onToggleFav, onOpenService)
            }
        }
    }
}

/* ----------------------------------- Detail ----------------------------------- */

@Composable
private fun DetailScreen(
    svc: Service,
    lang: String,
    favs: Set<String>,
    onToggleFav: (String) -> Unit,
    onOpenService: (Service) -> Unit,
    nav: NavHostController
) {
    val cat = Catalog.categoryOf(svc)
    val isFav = svc.id in favs
    LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { BackButton(nav) }
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(54.dp).clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) { Text(svc.icon, fontSize = 26.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(svc.name(lang), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                                 color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                "${cat?.name(lang) ?: ""} • ${svc.desc(lang)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onToggleFav(svc.id) }) {
                            Icon(
                                if (isFav) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = null,
                                tint = if (isFav) Color(0xFFC79A3A) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = { onOpenService(svc) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.open_official),
                             fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Text(
                        svc.url, fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    LabeledBlock(stringResource(R.string.uses_label)) {
                        svc.uses(lang).forEach { Bullet(it) }
                    }
                    LabeledBlock(stringResource(R.string.how_label)) {
                        svc.how(lang).forEachIndexed { i, step -> Numbered(i + 1, step) }
                    }
                    if (svc.docs.isNotEmpty()) {
                        LabeledBlock(stringResource(R.string.docs_label)) {
                            FlowChips(svc.docs)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    DisclaimerBox()
                }
            }
        }
    }
}

/* ----------------------------------- Favorites ----------------------------------- */

@Composable
private fun FavoritesScreen(
    lang: String,
    favs: Set<String>,
    onToggleFav: (String) -> Unit,
    onOpenService: (Service) -> Unit,
    nav: NavHostController
) {
    val list = remember(favs) { Catalog.services.filter { it.id in favs } }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        SectionLabel("★ " + (if (lang == "hi") "सहेजे" else "Saved"))
        if (list.isEmpty()) {
            EmptyState("★", stringResource(R.string.no_favorites))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(list, key = { it.id }) {
                    ServiceRow(it, lang, true, onToggleFav, onOpenService)
                }
            }
        }
    }
}

/* ----------------------------------- About ----------------------------------- */

@Composable
private fun AboutScreen(lang: String) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        SectionLabel("ⓘ " + (if (lang == "hi") "बारे में" else "About"))
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.app_name) + "  v1.0",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(10.dp))
        Text(stringResource(R.string.about_blurb),
             color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.about_for),
             color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        DisclaimerBox()
        Spacer(Modifier.height(40.dp))
        Text(
            if (lang == "hi") "उत्तर प्रदेश के नागरिकों के लिए" else "Made for citizens of Uttar Pradesh",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/* ----------------------------------- Reusable widgets ----------------------------------- */

@Composable
private fun LanguageToggle(lang: String, onSetLang: (String) -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(3.dp)
    ) {
        LangChip("EN", lang == "en") { onSetLang("en") }
        LangChip("हिं", lang == "hi") { onSetLang("hi") }
    }
}

@Composable
private fun LangChip(label: String, on: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (on) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            label, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            color = if (on) MaterialTheme.colorScheme.primary else Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBox(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        leadingIcon = { Icon(Icons.Default.Search, null) },
        placeholder = { Text(stringResource(R.string.search_hint), fontSize = 14.sp, maxLines = 1) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color(0xFF1A2421),
            unfocusedTextColor = Color(0xFF1A2421),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedLeadingIconColor = Color(0xFF5A6360),
            unfocusedLeadingIconColor = Color(0xFF5A6360),
            focusedPlaceholderColor = Color(0xFF7C8480),
            unfocusedPlaceholderColor = Color(0xFF7C8480)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(4.dp, 14.dp, 4.dp, 6.dp)
    )
}

@Composable
private fun CategoryCard(cat: Category, lang: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Box(
                Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) { Text(cat.icon, fontSize = 20.sp) }
            Spacer(Modifier.height(8.dp))
            Text(cat.name(lang), fontWeight = FontWeight.Bold, fontSize = 15.sp,
                 color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Text(
                "${Catalog.byCategory(cat.id).size} ${stringResource(R.string.services_count)}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickCard(svc: Service, lang: String, onClick: () -> Unit) {
    val cat = Catalog.categoryOf(svc)
    Card(
        modifier = Modifier.width(150.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(svc.icon, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(svc.name(lang), fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                 color = MaterialTheme.colorScheme.onSurface, maxLines = 2)
            Spacer(Modifier.height(2.dp))
            Text(cat?.name(lang) ?: "", fontSize = 11.sp,
                 color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
private fun ServiceRow(
    svc: Service,
    lang: String,
    isFav: Boolean,
    onToggleFav: (String) -> Unit,
    onOpen: (Service) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onOpen(svc) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) { Text(svc.icon, fontSize = 20.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(svc.name(lang), fontWeight = FontWeight.Bold, fontSize = 15.sp,
                     color = MaterialTheme.colorScheme.onSurface, maxLines = 2)
                Text(svc.desc(lang), fontSize = 12.sp,
                     color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2,
                     modifier = Modifier.padding(top = 3.dp))
            }
            IconButton(onClick = { onToggleFav(svc.id) }) {
                Icon(
                    if (isFav) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = null,
                    tint = if (isFav) Color(0xFFC79A3A) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BackButton(nav: NavHostController) {
    TextButton(onClick = { nav.popBackStack() }) {
        Icon(Icons.Default.ArrowBack, null)
        Spacer(Modifier.width(6.dp))
        Text(stringResource(R.string.back), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun LabeledBlock(label: String, content: @Composable () -> Unit) {
    Column(Modifier.padding(top = 14.dp)) {
        Text(
            label.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        content()
    }
}

@Composable
private fun Bullet(text: String) {
    Row(Modifier.padding(vertical = 3.dp)) {
        Text("• ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun Numbered(n: Int, text: String) {
    Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
        Text("$n. ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun FlowChips(items: List<String>) {
    // Simple wrap using FlowRow
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEach { d ->
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp
            ) {
                Text(d, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                     color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun DisclaimerBox() {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            stringResource(R.string.disclaimer),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun EmptyState(emoji: String, text: String) {
    Column(
        Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 42.sp)
        Spacer(Modifier.height(8.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant,
             textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
