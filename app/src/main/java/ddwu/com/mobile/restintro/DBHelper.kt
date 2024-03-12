package ddwu.com.mobile.restintro

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ddwu.com.mobile.restintro.data.RestDto

class DBHelper(context:Context,filename:String):SQLiteOpenHelper(context,filename,null,1) {
    companion object {
        var dbhelper: DBHelper? = null
        fun getInstance(context: Context, filename: String): DBHelper {
            if (dbhelper == null) {
                dbhelper = DBHelper(context, filename)
            }
            return dbhelper!!
        }

    }

    override fun onCreate(db: SQLiteDatabase?) {
        var sql: String = "CREATE TABLE IF NOT EXISTS REST( " +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PHOTONAME STRING, " +
                "NAME STRING, " +
                "LOCATION STRING, " +
                "REVIEW STRING, " +
                "RATING STRING ) "

        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    fun insertRest(vo: RestDto) {
        var sql = " INSERT INTO REST(photoName, name, location, review, rating) " +
                " VALUES('${vo.photoName}', '${vo.name}', '${vo.location}', '${vo.review}', '${vo.rating}')"

        var db = this.writableDatabase
        db.execSQL(sql)
    }

    fun updateRest(restDto: RestDto) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("name", restDto.name)
            put("location", restDto.location)
            put("review", restDto.review)
            put("rating", restDto.rating)
        }
        db.update("REST", values, "ID = ?", arrayOf(restDto.id.toString()))
    }

    fun allRest(): List<RestDto> {
        val restList = mutableListOf<RestDto>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM REST", null)
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndex("ID"))
                val photoName = it.getString(it.getColumnIndex("PHOTONAME"))
                val name = it.getString(it.getColumnIndex("NAME"))
                val location = it.getString(it.getColumnIndex("LOCATION"))
                val review = it.getString(it.getColumnIndex("REVIEW"))
                val rating = it.getString(it.getColumnIndex("RATING"))

                val rest = RestDto(id, photoName, name, location, review, rating)
                restList.add(rest)
            }
        }
        cursor.close()
        return restList
    }
}