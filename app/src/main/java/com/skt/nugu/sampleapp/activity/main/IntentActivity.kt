package com.skt.nugu.sampleapp.activity.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.skt.nugu.sampleapp.activity.SettingsActivity
import com.skt.nugu.sampleapp.databinding.ActivityIntentBinding


class IntentActivity : AppCompatActivity() {

    lateinit var binding : ActivityIntentBinding
    companion object {
        private const val TAG = "IntentActivity"
        fun invokeActivity(context: Context) {
            context.startActivity(Intent(context, IntentActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIntentBinding.inflate(layoutInflater)

        binding.intentDialBtn.setOnClickListener {
            dialPhoneNumber("01075705994")
        }

        binding.intentNaverOpenBtn.setOnClickListener {
            launchNaverApp("com.nhn.android.search")
        }

        binding.intentMapOpenBtn.setOnClickListener {
            launchMapApp("0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California")
        }

        binding.intentNaverSearchBtn.setOnClickListener {
            onlyForSearch(binding.naverSearchEditText.text.toString())
        }

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun launchNaverApp(packageName : String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent?.action = Intent.ACTION_WEB_SEARCH
            intent?.putExtra(SearchManager.QUERY, "강아지")
            startActivity(intent)
        } catch (e : Exception) {
            e.printStackTrace()
            val url = "market://details?id=" + packageName
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun launchMapApp(locationInfo : String) {
        val intent = Uri.parse("geo:$locationInfo")?.let { locationUri ->
            Intent(Intent.ACTION_VIEW, locationUri)
        }
        //
        startActivity(intent)
    }

    private fun onlyForSearch(query : String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)

        val pkgList : List<ResolveInfo> = this.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY or PackageManager.GET_RESOLVED_FILTER
        )
        Log.d("resolveList", pkgList.toString())
        val info: ResolveInfo = pkgList[0]
        val packageName = info.activityInfo.packageName
        val className = info.activityInfo.name
        intent.setClassName(packageName, className)

        intent.putExtra(SearchManager.QUERY, query)
        startActivity(intent)
    }

}