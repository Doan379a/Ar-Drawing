package com.ardrawing.sketch.anime.drawing.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

object CameraUtils {
    fun takePicture(
        imageCapture: ImageCapture?,
        isFlashOn: Boolean,
        context: Context,
        executor: Executor,
        onImageSaved: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        imageCapture ?: return

        imageCapture.flashMode =
            if (isFlashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF

        val timestamp = getCurrentTime()
        val photoFile = File(context.externalMediaDirs.firstOrNull(), "photo_${timestamp}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(photoFile.absolutePath),
                        null
                    ) { path, uri ->
                        Log.d("MediaScanner", "Scanned $path: $uri")
                    }
                    onImageSaved(photoFile.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }
    fun getCurrentTime(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    }

    fun saveVideo(context: Context): File {
        val timestamp = System.currentTimeMillis()
        return File(context.externalMediaDirs.firstOrNull(), "video_${timestamp}.mp4")
    }
    fun toggleFlash(
        context:Context,
        lifecycleOwner:LifecycleOwner,
        enable:Boolean,
        onSuccess:()->Unit,
        onError:(String)->Unit
    ){
        val cameraProviderFuture=ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider=cameraProviderFuture.get()
            val cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA
            val camera=cameraProvider.bindToLifecycle(lifecycleOwner,cameraSelector)
            if (camera.cameraInfo.hasFlashUnit()){
                camera.cameraControl.enableTorch(enable)
                onSuccess()
            }else{
                onError("Thiết bị không có flash")
            }
        },ContextCompat.getMainExecutor(context))
    }
}