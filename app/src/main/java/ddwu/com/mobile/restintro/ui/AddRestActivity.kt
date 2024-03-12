package ddwu.com.mobile.restintro.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import ddwu.com.mobile.restintro.DBHelper
import ddwu.com.mobile.restintro.data.RestDto
import ddwu.com.mobile.restintro.databinding.ActivityAddRestBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class AddRestActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1

    val addRestBinding by lazy {
        ActivityAddRestBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(addRestBinding.root)

        addRestBinding.webView.setOnClickListener {
            val intent = Intent (this, FindRestActivity::class.java)
            startActivity(intent)
            Toast.makeText(this@AddRestActivity, "항목을 클릭해 맛집 정보를 저장하세요!", Toast.LENGTH_SHORT).show()
        }

        val receivedPlaceName = intent.getStringExtra("PLACE_NAME_KEY")
        val receivedAddress = intent.getStringExtra("ADDRESS_KEY")

        // TextView에 주소 정보를 설정
        addRestBinding.etAddName.setText(receivedPlaceName)
        addRestBinding.tvAddress.text = receivedAddress

        addRestBinding.btnAdd.setOnClickListener {
            val res = RestDto(
                0,
                addRestBinding.ivAddPhoto.toString().trim(),
                addRestBinding.etAddName.text.toString(),
                addRestBinding.tvAddress.text.toString(),
                addRestBinding.etAddReview.text.toString(),
                addRestBinding.ratingBar.rating.toString())

            val dbHelper = DBHelper.getInstance(this, "rest.db",)
            dbHelper.insertRest(res)

            Toast.makeText(this@AddRestActivity, "새로운 맛집이 추가되었습니다!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RestListActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 카메라 앱을 실행하는 기능 구현
        addRestBinding.ivAddPhoto.setOnClickListener {
            dispatchTakePictureIntent()
            Toast.makeText(
                this@AddRestActivity,
                "음식 사진을 촬영해보세요!",
                Toast.LENGTH_SHORT
            ).show()
        }

        addRestBinding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun dispatchTakePictureIntent() {   // 원본 사진 요청
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        if (photoFile != null) {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "ddwu.com.mobile.restintro.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    setPic()
                }
            }
        }
    }

    lateinit var currentPhotoPath: String   // 현재 이미지 파일의 경로 저장
    var currentPhotoFileName: String? = null    // 현재 이미지 파일명

    /*카메라 앱 호출 관련 기능 구현*/
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val file = File("${storageDir?.path}/${timeStamp}.jpg")

        currentPhotoFileName = file.name
        currentPhotoPath = file.absolutePath
        return file
    }

    private fun setPic() {
        val targetW: Int = addRestBinding.ivAddPhoto.width
        val targetH: Int = addRestBinding.ivAddPhoto.height

        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight


            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            addRestBinding.ivAddPhoto.setImageBitmap(bitmap)
        }
    }
}