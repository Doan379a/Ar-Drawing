package com.ardrawing.sketch.anime.drawing.ui.ar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.AppOpenManager
import com.amazic.ads.util.BannerGravity
import com.ardrawing.sketch.anime.drawing.R
import com.ardrawing.sketch.anime.drawing.ads.InterAdHelper
import com.ardrawing.sketch.anime.drawing.base.BaseActivity
import com.ardrawing.sketch.anime.drawing.databinding.ActivityArCameraBinding
import com.ardrawing.sketch.anime.drawing.dialog.DialogSaveVideo
import com.ardrawing.sketch.anime.drawing.sharePreferent.SharePrefRemote
import com.ardrawing.sketch.anime.drawing.utils.CameraUtils
import com.google.common.util.concurrent.ListenableFuture
//import org.opencv.android.OpenCVLoader
//import org.opencv.android.Utils
//import org.opencv.core.Core
//import org.opencv.core.CvType
//import org.opencv.core.Mat
//import org.opencv.core.Scalar
//import org.opencv.core.Size
//import org.opencv.imgproc.Imgproc
import java.io.File
import com.ardrawing.sketch.anime.drawing.widget.tap
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ArUploadInllustrationActivity : BaseActivity<ActivityArCameraBinding>() {
    private var isFlashOn = false
    private var isFlipped = false
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var recordStartTime: Long = 0L
    private val handler = Handler()
    private var imageCapture: ImageCapture? = null
    private var videoFilePath: String? = null
    private var elapsedTimeWhenPaused: Long = 0L
    private var ischeckClock = true
    private lateinit var imgall: Bitmap
    override fun setViewBinding(): ActivityArCameraBinding {
        return ActivityArCameraBinding.inflate(layoutInflater)
    }

    override fun initView() {
        AppOpenManager.getInstance().enableAppResumeWithActivity(this.javaClass)
        if (SharePrefRemote.get_config(
                this,
                SharePrefRemote.banner_draw
            ) && AdsConsentManager.getConsentResult(this)
        ) {
            Admob.getInstance().loadCollapsibleBanner(
                this,
                getString(R.string.banner_draw),
                BannerGravity.bottom
            )
            binding.include.visibility = View.VISIBLE
        } else {
            binding.include.visibility = View.GONE
        }
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this, "Không thể tải OpenCV", Toast.LENGTH_SHORT).show()
            return
        }
        val imageUriString = intent.getStringExtra("image_uri")

        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            Log.d("ArCameraActivity", "Image URI: $imageUri")
            imgall = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
            binding.progressBar.visibility = View.VISIBLE

            Thread {
                val mat = Mat()
                Utils.bitmapToMat(imgall, mat)
                processImage(mat)
                runOnUiThread {
                    binding.imageView.setImageBitmap(imgall)
                    binding.progressBar.visibility = View.GONE
                }
            }.start()
        }
        ZoomImg()
        binding.imageView.alpha = 1f
        binding.seekBar.max = 100
        binding.seekBar.progress = 100
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.imageView.alpha = progress / 100f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        startCamera()
    }


    private fun processImage(mat: Mat) {

        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)
        val equalized = Mat()
        Imgproc.equalizeHist(gray, equalized)
        val sobelX = Mat()
        val sobelY = Mat()
        Imgproc.Sobel(equalized, sobelX, CvType.CV_16S, 1, 0)
        Imgproc.Sobel(equalized, sobelY, CvType.CV_16S, 0, 1)
        val absSobelX = Mat()
        val absSobelY = Mat()
        Core.convertScaleAbs(sobelX, absSobelX)
        Core.convertScaleAbs(sobelY, absSobelY)
        val edges = Mat()
        Core.addWeighted(absSobelX, 0.5, absSobelY, 0.5, 0.0, edges)
        val blurred = Mat()
        Imgproc.GaussianBlur(edges, blurred, Size(1.0, 1.0), 0.0)
        val sketch = Mat(mat.size(), CvType.CV_8UC4, Scalar(0.0, 0.0, 0.0, 0.0))  // Nền trong suốt
        for (i in 0 until blurred.rows()) {
            for (j in 0 until blurred.cols()) {
                if (blurred.get(i, j)[0] > 50.0) {
                    sketch.put(i, j, byteArrayOf(0, 0, 0, 255.toByte()))
                }
            }
        }


        imgall = Bitmap.createBitmap(sketch.cols(), sketch.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(sketch, imgall)
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        bindCamera(cameraProviderFuture)
    }

    private fun bindCamera(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            val preview = Preview.Builder().build().also { preview ->
                preview.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            imageCapture = ImageCapture.Builder()
                .setFlashMode(if (isFlashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    videoCapture,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("ArCameraActivity", "Failed to bind camera", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startRecording() {
        val videoCapture = this.videoCapture ?: return
        videoFilePath = CameraUtils.saveVideo(this).absolutePath
        val outputOptions = FileOutputOptions.Builder(File(videoFilePath)).build()
        recording = videoCapture.output
            .prepareRecording(this, outputOptions)
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.txtTimeRecod.visibility = View.VISIBLE
                        binding.btnRecod.setImageResource(R.drawable.ic_recod_start)
                        recordStartTime = System.currentTimeMillis()
                        binding.txtSave.visibility = View.GONE
                        handler.post(updateTimeRunnable)
                    }

                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            binding.txtTimeRecod.visibility = View.GONE
                            binding.btnRecod.setImageResource(R.drawable.ic_recod_stop)
                            handler.removeCallbacks(updateTimeRunnable)
                            binding.txtSave.setText(R.string.video_saved)
                            binding.txtSave.visibility = View.VISIBLE
                            MediaScannerConnection.scanFile(
                                this,
                                arrayOf(videoFilePath),
                                null
                            ) { path, uri ->
                                Log.d("MediaScanner", "Đã quét $path: $uri")
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.txtSave.visibility = View.GONE
                            }, 2000)
                        } else {
                            Log.e("CameraX", "Recording error: ${recordEvent.error}")
                        }
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        recording?.close()
        recording = null
    }


    override fun onResume() {
        super.onResume()
        startCamera()
        CameraUtils.toggleFlash(
            context = this,
            lifecycleOwner = this,
            enable = isFlashOn,
            onSuccess = {
                Log.d("ArCameraActivity", "Flash được bật/tắt thành công")
            },
            onError = { errorMessage ->
                Log.e("ArCameraActivity", errorMessage)
            }
        )
    }

    override fun onPause() {
        super.onPause()
        recording?.stop()
        recording = null
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(this))
    }

    override fun viewListener() {
        binding.tvFlash.setOnClickListener {
            isFlashOn = !isFlashOn
            CameraUtils.toggleFlash(
                context = this,
                lifecycleOwner = this,
                enable = isFlashOn,
                onSuccess = {
                    Log.d("ArCameraActivity", "Flash được bật/tắt thành công")
                },
                onError = { errorMessage ->
                    Log.e("ArCameraActivity", errorMessage)
                }
            )
            binding.tvFlash.setCompoundDrawablesWithIntrinsicBounds(
                0,
                if (isFlashOn) R.drawable.ic_flash_select else R.drawable.ic_flash,
                0,
                0
            )
        }
        binding.tvFinish.setOnClickListener {
            if (recording != null) {
                recording?.pause()
                handler.removeCallbacks(updateTimeRunnable)
                elapsedTimeWhenPaused = System.currentTimeMillis() - recordStartTime
                val saveVideoDialog = DialogSaveVideo(this,

                    onSave = {
                        CameraUtils.saveVideo(this)
//                    Toast.makeText(this, "Video saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onDiscard = {
                        videoFilePath?.let { path ->
                            val videoFile = File(path)
                            if (videoFile.exists()) {
                                videoFile.delete()
//                            Toast.makeText(this, "Video discarded.", Toast.LENGTH_SHORT).show()
                            }
                        }
//                    Toast.makeText(this, "Video discarded.", Toast.LENGTH_SHORT).show()

                        backMain()
                    }

                )
                saveVideoDialog.setOnDismissListener {
                    recording?.resume()
                    recordStartTime = System.currentTimeMillis() - elapsedTimeWhenPaused
                    handler.post(updateTimeRunnable)
                }
                saveVideoDialog.show()
            } else {
                backMain()
            }

        }
        binding.btnRecod.tap {
            Log.d("CameraX", "btnRecod")

            if (recording != null) {
                recording?.stop()
                recording = null
            } else {
                startRecording()
            }
        }
        binding.tvFlip.setOnClickListener {
            isFlipped = !isFlipped
            binding.imageView.scaleX = if (isFlipped) -1f else 1f
        }
        binding.tvPhoto.tap {
            Log.d("CameraX", "click photo")

            CameraUtils.takePicture(
                imageCapture = this.imageCapture,
                isFlashOn = isFlashOn,
                context = this,
                executor = ContextCompat.getMainExecutor(this),
                onImageSaved = { photoPath ->
                    Log.d("CameraX", "Ảnh đã được lưu thành công: $photoPath")
                    binding.txtSave.setText(R.string.photo_saved)
                    binding.txtSave.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.txtSave.visibility = View.GONE
                    }, 3000)
                },
                onError = { exception ->
                    Log.e("CameraX", "Chụp ảnh không thành công: ${exception.message}", exception)
                }
            )
        }

        binding.btnClock.setOnClickListener {
            ischeckClock = !ischeckClock
            binding.btnClock.setImageResource(if (ischeckClock) R.drawable.ic_un_clock else R.drawable.ic_clock)
            if (ischeckClock) {
                ZoomImg()
            } else {
                binding.imageView.controller.settings.apply {
                    setPanEnabled(false)
                    setDoubleTapEnabled(false)
                    setZoomEnabled(false)
                    setRotationEnabled(false)
                    setFillViewport(false);
                }
            }
        }
    }

    fun backMain() {
        showLoading()
        InterAdHelper.showInterAd(
            this, SharePrefRemote.get_config(
                this,
                SharePrefRemote.inter_draw
            ),
            getString(R.string.inter_draw)
        ) {
            finish()
            hideLoading()
        }
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val elapsedTime = System.currentTimeMillis() - recordStartTime
            val seconds = (elapsedTime / 1000) % 60
            val minutes = (elapsedTime / (1000 * 60)) % 60
            val hours = (elapsedTime / (1000 * 60 * 60))

            binding.txtTimeRecod.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            handler.postDelayed(this, 1000)
        }
    }

    override fun onBackPressed() {
        if (recording != null) {
            recording?.stop()
            recording = null
            handler.removeCallbacks(updateTimeRunnable)
            binding.txtSave.visibility = View.GONE
            val saveVideoDialog = DialogSaveVideo(this,
                onSave = {
                    CameraUtils.saveVideo(this)
//                    Toast.makeText(this, "Video saved!", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onDiscard = {
                    videoFilePath?.let { path ->
                        val videoFile = File(path)
                        if (videoFile.exists()) {
                            videoFile.delete()
//                            Toast.makeText(this, "Video discarded.", Toast.LENGTH_SHORT).show()
                        }
                    }
//                    Toast.makeText(this, "Video discarded.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            )
            saveVideoDialog.setOnDismissListener {
                recording?.resume()
                recordStartTime = System.currentTimeMillis() - elapsedTimeWhenPaused
                handler.post(updateTimeRunnable)
            }
            saveVideoDialog.show()
        } else {
            super.onBackPressed()
        }
    }

    fun ZoomImg() {
        binding.imageView.controller.settings.apply {
            setMaxZoom(5f)
            setMinZoom(0.01f)
            setPanEnabled(true)
            setDoubleTapEnabled(false)
            setZoomEnabled(true)
            setRotationEnabled(true)
            setOverzoomFactor(1000f)
            setFillViewport(false);
            setOverscrollDistance(this@ArUploadInllustrationActivity, 1000f, 1000f)
        }
    }

    override fun dataObservable() {}
}