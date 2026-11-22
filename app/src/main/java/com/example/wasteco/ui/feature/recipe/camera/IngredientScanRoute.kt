package com.example.wasteco.ui.feature.recipe.camera

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wasteco.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import kotlin.math.roundToInt

private const val MAX_UPLOAD_BYTES = 3_900_000
private const val MAX_IMAGE_SIDE = 1600

class IngredientScanFileProvider : FileProvider()

private val takeIngredientPicture = object : ActivityResultContracts.TakePicture() {
    override fun createIntent(context: Context, input: Uri): Intent =
        super.createIntent(context, input).apply {
            clipData = ClipData.newRawUri("ingredient photo", input)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
}

@Composable
fun IngredientScanRoute(
    onClose: () -> Unit,
    onSaved: () -> Unit,
    viewModel: IngredientScanViewModel = viewModel(factory = IngredientScanViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resolver = context.contentResolver
    val directory = File(context.cacheDir, "ingredient-scan")
    val scope = rememberCoroutineScope()
    var cameraFileName by rememberSaveable { mutableStateOf<String?>(null) }

    fun readPhoto(photo: ImageDecoder.Source, temporaryFile: File? = null) {
        viewModel.readingPhoto()
        scope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) { prepareImage(photo) }
                viewModel.setPhoto(bytes, "image/jpeg")
            } catch (error: CancellationException) {
                viewModel.cameraReady()
                throw error
            } catch (_: Exception) {
                viewModel.photoUnavailable(R.string.scan_photo_failed)
            } finally {
                if (temporaryFile != null) withContext(NonCancellable + Dispatchers.IO) {
                    temporaryFile.delete()
                }
            }
        }
    }

    val camera = rememberLauncherForActivityResult(takeIngredientPicture) { captured ->
        val file = cameraFileName?.let { File(directory, it) }
        cameraFileName = null
        if (file != null && captured) {
            readPhoto(ImageDecoder.createSource(file), temporaryFile = file)
        } else if (file != null) {
            scope.launch(Dispatchers.IO) { file.delete() }
        }
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { readPhoto(ImageDecoder.createSource(resolver, it)) }
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }
    val photo = state.photo
    BackHandler(enabled = photo != null || state.isReadingPhoto || state.isSaving) {
        if (!state.isReadingPhoto && !state.isSaving) viewModel.retake()
    }

    if (photo == null) {
        CameraCaptureScreen(
            state = state,
            onCloseClick = onClose,
            onCaptureClick = {
                viewModel.readingPhoto()
                scope.launch {
                    var file: File? = null
                    var launched = false
                    try {
                        withContext(Dispatchers.IO) {
                            directory.mkdirs()
                            file = File.createTempFile("photo-", ".jpg", directory)
                        }
                        val captureFile = checkNotNull(file)
                        val uri = FileProvider.getUriForFile(
                            context, "${context.packageName}.ingredient-scan", captureFile
                        )
                        cameraFileName = captureFile.name
                        camera.launch(uri)
                        launched = true
                        viewModel.cameraReady()
                    } catch (error: CancellationException) {
                        viewModel.cameraReady()
                        throw error
                    } catch (_: ActivityNotFoundException) {
                        viewModel.photoUnavailable(R.string.scan_camera_unavailable)
                    } catch (_: Exception) {
                        viewModel.photoUnavailable(R.string.scan_photo_failed)
                    } finally {
                        if (!launched) {
                            cameraFileName = null
                            withContext(NonCancellable + Dispatchers.IO) { file?.delete() }
                        }
                    }
                }
            },
            onGalleryClick = {
                try {
                    gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } catch (_: ActivityNotFoundException) {
                    viewModel.photoUnavailable(R.string.scan_gallery_unavailable)
                }
            }
        )
    } else {
        ReviewPhotoScreen(
            state = state,
            onRetake = viewModel::retake,
            onRescan = viewModel::scan,
            onChangeItem = viewModel::changeItem,
            onRemoveItem = viewModel::removeItem,
            onSave = viewModel::save
        )
    }
}

private fun prepareImage(source: ImageDecoder.Source): ByteArray {
    val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
        val largestSide = maxOf(info.size.width, info.size.height)
        if (largestSide > MAX_IMAGE_SIDE) {
            val scale = MAX_IMAGE_SIDE.toDouble() / largestSide
            decoder.setTargetSize(
                (info.size.width * scale).roundToInt().coerceAtLeast(1),
                (info.size.height * scale).roundToInt().coerceAtLeast(1)
            )
        }
        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
    }
    try {
        return ByteArrayOutputStream().use { buffer ->
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 88, buffer) || buffer.size() > MAX_UPLOAD_BYTES) {
                throw IOException()
            }
            buffer.toByteArray()
        }
    } finally {
        bitmap.recycle()
    }
}
