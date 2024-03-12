package ddwu.com.mobile.restintro.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ddwu.com.mobile.restintro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val mainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        mainBinding.btnList.setOnClickListener {
            val intent = Intent (this, RestListActivity::class.java)
            startActivity(intent)
        }
        mainBinding.btnFind.setOnClickListener {
            val intent = Intent (this, AroundRestActivity::class.java)
            startActivity(intent)
        }
    }
}