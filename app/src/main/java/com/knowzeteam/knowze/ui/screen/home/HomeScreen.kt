package com.knowzeteam.knowze.ui.screen.home

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knowzeteam.knowze.R
import com.knowzeteam.knowze.ui.component.MenuItem
import com.knowzeteam.knowze.ui.component.MiniMenuItem
import com.knowzeteam.knowze.ui.screen.auth.login.LoginViewModel
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.firebase.auth.FirebaseAuth
import com.knowzeteam.knowze.data.remote.response.dashboard.CoursesItem
import com.knowzeteam.knowze.data.remote.response.newsresponse.NewsResponseItem
import com.knowzeteam.knowze.ui.ViewModelFactory
import com.knowzeteam.knowze.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    viewModelFactory: ViewModelFactory = ViewModelFactory(LocalContext.current)
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val showLogoutDialog = remember { mutableStateOf(false) }

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val newestCourses by viewModel.newestCourses.collectAsState()
    val newsViewModel: NewsViewModel = viewModel(factory = viewModelFactory)

    val newsItems by newsViewModel.newsItems.collectAsState()

    val loginState by viewModel.loginState.collectAsState()
    val userName = firebaseAuth.currentUser?.displayName.toString()// Observe the user's name
    val userEmail = firebaseAuth.currentUser?.email.toString() // Observe the user's email
    val userPhotoUrl = firebaseAuth.currentUser?.photoUrl.toString() // Observe

    LaunchedEffect(Unit) {
        newsViewModel.fetchNews()
    }

    if (loginState is LoginViewModel.LoginState.Logout) {
        navController.navigate(Screen.Login.route)
    }

    Box {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(showLogoutDialog, viewModel, userName, userEmail, userPhotoUrl)
            }
        ) {
            HomeContent(drawerState = drawerState, userName, navController, newestCourses, newsItems)
        }
    }
}

@Composable
fun DrawerContent(
    showLogoutDialog: MutableState<Boolean>,
    viewModel: LoginViewModel,
    userName: String?,
    userEmail: String?,
    userPhotoUrl: String?
) {
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxHeight()
            .width(260.dp)
    ) {
        Column {
            DrawerHeader(userName, userEmail, userPhotoUrl)
            DrawerItem("Edit Profile", Icons.Default.Person)
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { showLogoutDialog.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "Log Out",
                    color = Color.White,
                    )
            }
        }
    }

    if (showLogoutDialog.value) {
        LogoutDialog(showLogoutDialog) {
            viewModel.logout()
        }
    }
}

@Composable
fun DrawerHeader(userName: String?, userEmail: String?, userPhotoUrl: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!userPhotoUrl.isNullOrBlank()) {
            Image(
                painter = rememberCoilPainter(request = userPhotoUrl),
                contentDescription = "User Photo",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text( text = userName.orEmpty(), style = MaterialTheme.typography.titleMedium, color = Color.Black,)
        Text(text = userEmail.orEmpty(), style = MaterialTheme.typography.bodySmall, color = Color.Black,)
    }
}

@Composable
fun DrawerItem(text: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.Black)
        Spacer(Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.Black,)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    drawerState: DrawerState,
    userName: String?,
    navController: NavController,
    newestCourses: List<CoursesItem?>?,
    newsItems: List<NewsResponseItem>,
    modifier: Modifier = Modifier
){

    val context = LocalContext.current
    // State to track if the drawer should open
    var openDrawer by remember { mutableStateOf(false) }

    // When openDrawer changes to true, open the drawer
    LaunchedEffect(openDrawer) {
        if (openDrawer && drawerState.isClosed) {
            drawerState.open()
            openDrawer = false // Reset the state
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 30.dp)
        ) {
            IconButton(
                onClick = {
                    openDrawer = true
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "image description",
                    contentScale = ContentScale.None,
                )
            }
            Spacer(modifier = modifier.width(10.dp))
            Text(
                text = stringResource(id = R.string.selamat) + " " + userName + "?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }

        Column(
            modifier.padding(horizontal = 16.dp)
        ) {
            ClickableSearchBar(
                placeholderText = stringResource(R.string.search_value),
                onSearchBarClick = {
                    navController.navigate("${Screen.HomeS.route}/focus")
                }
            )
            Spacer(modifier = modifier.height(10.dp))
            SuggestionBox(text = "Belajar edit video memakai capcut")
            Spacer(modifier = modifier.height(16.dp))

        }
        Column {
            LazyRow(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(newestCourses ?: emptyList()) { course ->
                    CardCourseItem(course = course, navController = navController)
                }
            }
            Spacer(modifier = modifier.height(16.dp))
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp)
                    )
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Divider(
                            modifier = modifier
                                .height(2.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            color = Color.LightGray
                        )
                        Spacer(modifier = modifier.height(16.dp))
                        MenuItem(
                            text = stringResource(id = R.string.menu_rekomendasi),
                            imageResId = R.drawable.ic_cari_rekomendasi,
                            boxColor = Color(0xFF43936C),
                            onClick = {
                                Toast.makeText(context, "Maaf, fitur dalam proses pengembangan", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    item {
                        Spacer(modifier = modifier.height(16.dp))
                        MenuItem(
                            text = stringResource(id = R.string.menu_keyword),
                            subText = stringResource(id = R.string.menu_keyword_detail),
                            imageResId = R.drawable.ic_trending_keyword,
                            boxColor = MaterialTheme.colorScheme.primary,
                            onClick = { navController.navigate(Screen.TrendingKeyword.route)}
                        )
                    }
                    item {
                        Spacer(modifier = modifier.height(16.dp))
                        MenuItem(
                            text = stringResource(id = R.string.menu_galeri),
                            subText = stringResource(id = R.string.menu_galeri_detail),
                            imageResId = R.drawable.ic_gallery,
                            boxColor = MaterialTheme.colorScheme.primary,
                            onClick = {navController.navigate(Screen.CourseGallery.route) }
                        )
                        Spacer(modifier = modifier.height(16.dp))
                        Divider(
                            modifier = modifier
                                .height(2.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            color = Color.LightGray
                        )
                    }
                    item {
                        Spacer(modifier = modifier.height(25.dp))
                        Text(
                            text = stringResource(id = R.string.title_menu_mini),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF3334CC)
                            ),
                            modifier = modifier
                                .fillMaxWidth()
                        )
                        Spacer(modifier = modifier.height(10.dp))
                    }
                    items(newsItems) { newsItem ->
                        MiniMenuItem(
                            newsResponseItem = newsItem,
                        )
                        Spacer(modifier = modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ClickableSearchBar(
    placeholderText: String,
    onSearchBarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State for animated typing effect
    val animatedPlaceholder = remember { Animatable(0f) }
    LaunchedEffect(key1 = true) {
        animatedPlaceholder.animateTo(
            targetValue = placeholderText.length.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }
    val displayText = placeholderText.take(animatedPlaceholder.value.toInt())

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onSearchBarClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = displayText,
            color = Color.Gray,
        )
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search Icon",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.CenterEnd)
        )
    }
}



@Composable
fun LogoutDialog(showLogoutDialog: MutableState<Boolean>, onConfirmLogout: () -> Unit) {
    AlertDialog(
        onDismissRequest = { showLogoutDialog.value = false },
        title = {
            Text(
                "Konfirmasi Keluar",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        },
        text = {
            Text(
                "Apakah kamu yakin ingin keluar?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    showLogoutDialog.value = false
                    onConfirmLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCB3A31) ),
            ) {
                Text(
                    "Keluar",
                    style = MaterialTheme.typography.bodyMedium, color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { showLogoutDialog.value = false }) {
                Text("Tidak",
                    style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    )
}

@Composable
fun SuggestionBox(
    text: String,
    modifier: Modifier = Modifier
) {
    Column{
        Text(
            text = stringResource(id = R.string.coba_ini),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(10.dp)
                .height(16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CardCourseItem(
    course: CoursesItem?,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .width(240.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                if (course != null) {
                    navController.navigate("${Screen.AboutCourse.route}/${course.id}")
                }
            }
    ) {
        Image(
            painter = painterResource(R.drawable.bg_knowze),
            contentDescription = stringResource(R.string.theme_course_pict),
            contentScale = ContentScale.Crop,
        )
//        BoxContentOverlay(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = course?.title ?: "title",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.Black
            )
        }
    }
}

//
//@Preview(showBackground = true, device = Devices.PIXEL_4)
//@Composable
//fun HomeScreenPreview(){
//    KnowzeTheme {
//        HomeScreen(
//
//        )
//    }
//}