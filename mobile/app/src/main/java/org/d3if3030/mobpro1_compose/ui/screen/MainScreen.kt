package org.d3if3030.mobpro1_compose.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if3030.mobpro1_compose.BuildConfig
import org.d3if3030.mobpro1_compose.R
import org.d3if3030.mobpro1_compose.model.LogDay
import org.d3if3030.mobpro1_compose.model.User
import org.d3if3030.mobpro1_compose.network.ApiStatus
import org.d3if3030.mobpro1_compose.network.UserDataStore
import org.d3if3030.mobpro1_compose.ui.theme.Mobpro1composeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage

    var showDialog by remember {
        mutableStateOf(false)
    }
    var showHewanDialog by remember {
        mutableStateOf(false)
    }

    var bitmap: Bitmap? by remember {
        mutableStateOf(null)
    }
    val launcher = rememberLauncherForActivityResult(contract = CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) showHewanDialog = true
    }

    Scaffold(floatingActionButton = {
        if (user.email.isNotEmpty()) {
            FloatingActionButton(onClick = {
                val options = CropImageContractOptions(
                    null, CropImageOptions(
                        imageSourceIncludeCamera = true,
                        imageSourceIncludeGallery = false,
                        fixAspectRatio = true
                    )
                )
                launcher.launch(options)
            }) {
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = stringResource(
                        id = R.string.tambah_hewan
                    )
                )

            }
        }
    }, topBar = {
        TopAppBar(title = {
            Text(text = stringResource(id = R.string.app_name))
        }, colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ), actions = {
            if (user.email.isNotEmpty()) {
                IconButton(onClick = {
                    if (user.email.isEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                    } else {
                        showDialog = true
                    }

                }) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.baseline_account_circle_24
                        ),
                        contentDescription = stringResource(id = R.string.profile),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        })

    }) { padding ->
        if (user.email.isNotEmpty()) {
            ScreenContent(viewModel, user.email, Modifier.padding(padding))
        } else {
            OnboardingScreen {}
        }

        if (showDialog) {
            ProfilDialog(user = user, onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }

        if (showHewanDialog) {
            LogDayDialog(bitmap = bitmap,
                onDismissRequest = { showHewanDialog = false }) { description ->
                viewModel.saveData(user.email, description, bitmap!!)
                showHewanDialog = false
            }
        }

        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }
}

@Composable
fun ScreenContent(viewModel: MainViewModel, userId: String, modifier: Modifier) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    LaunchedEffect(userId) {
        viewModel.retrieveData(userId)
    }

    when (status) {
        ApiStatus.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        ApiStatus.SUCCESS -> {
            if (data.size == 0) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.guide_post),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(data) { ListItem(viewModel, userId, it) }
                }
            }

        }

        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.error))
                Button(
                    onClick = { viewModel.retrieveData(userId) },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }
        }
    }
}

@Composable
fun ListItem(viewModel: MainViewModel, userId: String, logDay: LogDay) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(horizontal = 8.dp, vertical = 4.dp) // Adjust padding here
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Gray)
            .clickable { showDialog = true }, // Show dialog on click
        contentAlignment = Alignment.BottomStart
    ) {
        // AsyncImage to load the image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(logDay.file_location)
                .crossfade(true)
                .build(),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clickable { showDialog = true } // Show dialog on click
        )

        // Overlay with description
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            Text(
                text = logDay.description,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, title = {
            Text(text = stringResource(id = R.string.confirm_delete_title))
        }, text = {
            Text(text = stringResource(id = R.string.confirm_delete_message))
        }, confirmButton = {
            Button(onClick = {
                // Handle the delete action here
                viewModel.deleteData(userId, logDay.id)
                showDialog = false
            }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }, dismissButton = {
            Button(onClick = { showDialog = false }) {
                Text(text = stringResource(id = R.string.batal))
            }
        })
    }
}

suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption =
        GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.API_KEY).build()

    val request: GetCredentialRequest =
        GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleIdToken.displayName ?: ""
            val email = googleIdToken.id
            val photoUrl = googleIdToken.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun getCroppedImage(resolver: ContentResolver, result: CropImageView.CropResult): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "ERROR: ${result.error}")
        return null
    }
    val uri = result.uriContent ?: return null
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    Mobpro1composeTheme {
        MainScreen()
    }
}