package com.mxt.anitrend.view.activity.base

import android.Manifest
import android.os.Bundle

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.presenter.base.BasePresenter

import android.os.Environment

import android.widget.TextView
import android.widget.Toast

import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader

class ReportActivity : ActivityBase<Void, BasePresenter>() {

    private var log: StringBuilder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        log = StringBuilder()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        findViewById<View>(R.id.save_logcat_button).setOnClickListener({ view ->
            if (requestPermissionIfMissing(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                try {
                    val root = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "AniTrend Logcat.txt"
                    )
                    val writer = FileWriter(root)
                    writer.append(log!!.toString())
                    writer.flush()
                    writer.close()
                    Toast.makeText(applicationContext, R.string.bug_report_saved, Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        })
        onActivityReady()
    }

    override fun onActivityReady() {
        updateUI()
    }

    override fun updateUI() {
        try {
            val process = Runtime.getRuntime().exec("logcat -d -v threadtime com.mxt.anitrend:*")
            val bufferedReader = BufferedReader(
                InputStreamReader(process.inputStream)
            )

            var line: String
            while ((line = bufferedReader.readLine()) != null) {
                log!!.append(line).append("\n")
            }
            (findViewById<View>(R.id.report_display) as TextView).text = log!!.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun makeRequest() {}
}
