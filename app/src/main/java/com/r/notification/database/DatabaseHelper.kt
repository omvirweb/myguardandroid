package com.r.notification.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_GEO_LOCATION (LATITUDE TEXT,LONGITUDE TEXT,DATETIME TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertData(latitude: String, longitude: String, datetime: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, latitude)
        contentValues.put(COL_3, longitude)
        contentValues.put(COL_4, datetime)
        db.insert(TABLE_GEO_LOCATION, null, contentValues)
    }

    fun deleteData(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_GEO_LOCATION, "ID = ?", arrayOf(id))
    }

    val allData: Cursor
        get() {
            val db = this.writableDatabase
            return db.rawQuery("SELECT * FROM $TABLE_GEO_LOCATION", null)
        }

    companion object {
        const val DATABASE_NAME = "app_database.db"
        const val TABLE_GEO_LOCATION = "geo_location"
        const val TABLE_NOTIFICATION = "notification"
        const val COL_1 = "ID"
        const val COL_2 = "LATITUDE"
        const val COL_3 = "LONGITUDE"
        const val COL_4 = "TAKEN_DATETIME"
    }
}