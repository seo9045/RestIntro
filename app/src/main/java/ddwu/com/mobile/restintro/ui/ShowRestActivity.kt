package ddwu.com.mobile.restintro.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.bumptech.glide.Glide
import ddwu.com.mobile.restintro.DBHelper
import ddwu.com.mobile.restintro.data.RestDto
import ddwu.com.mobile.restintro.databinding.ActivityShowRestBinding
import java.io.File

class ShowRestActivity : AppCompatActivity() {

    val showRestBinding by lazy {
        ActivityShowRestBinding.inflate(layoutInflater)
    }

    lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(showRestBinding.root)

        dbHelper = DBHelper.getInstance(this, "rest.db")

        val restDto = intent.getSerializableExtra("restDto") as RestDto

        showRestBinding.etModifyName.setText(restDto.name)
        showRestBinding.etModifyLocation.setText(restDto.location)

        val imagePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), restDto.photoName)

        if (imagePath.exists()) {
            Glide.with(this)
                .load(imagePath)
                .into(showRestBinding.ivModifyPhoto)
        } else {

        }
        showRestBinding.etModifyReview.setText(restDto.review)
        showRestBinding.modifyRatingBar.rating = restDto.rating.toFloat()

        showRestBinding.btnModify.setOnClickListener {
            val newName = showRestBinding.etModifyName.text.toString()
            val newLocation = showRestBinding.etModifyLocation.text.toString()
            val newReview = showRestBinding.etModifyReview.text.toString()
            val newRating = showRestBinding.modifyRatingBar.rating.toString()
            restDto.name = newName
            restDto.location = newLocation
            restDto.review = newReview
            restDto.rating = newRating

            dbHelper.updateRest(restDto)

            Toast.makeText(this@ShowRestActivity, "맛집 정보가 수정되었습니다!", Toast.LENGTH_SHORT).show()

            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        showRestBinding.btnClose.setOnClickListener {
            finish()
        }
    }
}