package ddwu.com.mobile.restintro.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.restintro.DBHelper
import ddwu.com.mobile.restintro.databinding.ActivityRestListBinding

class RestListActivity : AppCompatActivity() {

    val restListBinding by lazy {
        ActivityRestListBinding.inflate(layoutInflater)
    }

    val adapter : RestAdapter by lazy {
        RestAdapter()
    }

    companion object {
        const val REQUEST_CODE_EDIT_REST = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(restListBinding.root)

        restListBinding.rvRest.adapter = adapter
        restListBinding.rvRest.layoutManager = LinearLayoutManager(this)

        restListBinding.floatingBtnAdd.setOnClickListener{
            val addIntent = Intent(this@RestListActivity, AddRestActivity::class.java)
            startActivity(addIntent)
        }

        adapter.setOnItemClickListener(object: RestAdapter.OnRestItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent (this@RestListActivity, ShowRestActivity::class.java )
                intent.putExtra("restDto", adapter.restList?.get(position))
                startActivity(intent)
            }
        })

        adapter.setOnItemLongClickListener(object : RestAdapter.OnRestItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                val selectedDto = adapter.restList?.get(position)

                val dialogBuilder = AlertDialog.Builder(this@RestListActivity)
                dialogBuilder.setMessage("삭제하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("삭제") { _, _ ->
                        val dbHelper = DBHelper.getInstance(this@RestListActivity, "rest.db")
                        selectedDto?.let {
                            dbHelper.writableDatabase.delete("REST", "ID = ?", arrayOf(it.id.toString()))
                            showAllRest()
                        }
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle("삭제 확인")
                alert.show()
            }
        })

        adapter.setOnItemClickListener(object : RestAdapter.OnRestItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@RestListActivity, ShowRestActivity::class.java)
                intent.putExtra("restDto", adapter.restList?.get(position))
                startActivityForResult(intent, REQUEST_CODE_EDIT_REST)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        showAllRest()
    }

    private fun showAllRest() {
        val dbHelper = DBHelper.getInstance(this, "rest.db")
        val restList = dbHelper.allRest()

        adapter.restList = restList
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_REST && resultCode == Activity.RESULT_OK) {
            showAllRest()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }
}