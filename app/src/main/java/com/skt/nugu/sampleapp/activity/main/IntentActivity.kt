package com.skt.nugu.sampleapp.activity.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skt.nugu.sampleapp.databinding.ActivityIntentBinding
import java.net.URLEncoder

class IntentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntentBinding
    lateinit var pkgList: MutableList<PackageInfo>

    companion object {
        private const val TAG = "IntentActivity"
        fun invokeActivity(context: Context) {
            context.startActivity(Intent(context, IntentActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun init() {
        binding = ActivityIntentBinding.inflate(layoutInflater)
        pkgList = getAndDisplayAllPackages()

        binding.intentDialBtn.setOnClickListener {
            dialPhoneNumber("01075705994")
        }

        binding.intentNaverOpenBtn.setOnClickListener {
            launchAppWithPackageName("com.nhn.android.search")
        }

        binding.intentMapOpenBtn.setOnClickListener {
            launchMapApp(binding.mapEditText.text.toString())
        }

        binding.intentNaverSearchBtn.setOnClickListener {
            onlyForSearch(binding.naverSearchEditText.text.toString())
        }

        binding.launchAppKeywordBtn.setOnClickListener {
            launchAppWithKeywordByUser(binding.keywordEditText.text.toString())
        }
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun launchAppWithPackageName(packageName: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            val url = "market://details?id=" + packageName
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun launchMapApp(locationInfo: String) {
        val encodedLocationInfo = URLEncoder.encode(locationInfo, "UTF-8")
        var intent = Uri.parse("geo:0,0?q=${encodedLocationInfo}")?.let { locationUri ->
            Intent(Intent.ACTION_VIEW, locationUri)
        }

        //구글 맵으로만 호출
        if (binding.googleMapCheckBox.isChecked) {
            intent = intent?.let { makeIntentSpecific(it, "google") }
        }

        startActivity(intent)
    }

    private fun onlyForSearch(query: String) {
        var intent = Intent(Intent.ACTION_WEB_SEARCH)

        intent = makeIntentSpecific(intent, "nhn")

        intent.putExtra(SearchManager.QUERY, query)

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "naver app not installed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getAndDisplayAllPackages(): MutableList<PackageInfo> {
        val packs = packageManager.getInstalledPackages(0)
        Log.d("packs", packs.toString())

        for (pack in packs) {
            binding.pkgScrollViewInnerLayout.addView(TextView(this).apply {
                textSize = 10f
                text = pack.toString()
            })
        }
        return packs
    }

    // 패키지명으로 라벨 가져오기
    // packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0))

    private fun launchAppWithKeywordByUser(keyword: String) {
        val keywordList: List<String> = keyword.split(" ")

        pkgList.forEach {
            if (keywordList.all { word: String -> it.packageName.contains(word) }) {
                Log.d("selectedPkg", "keyword : ${keywordList}, packageName : ${it.packageName}")
                return launchAppWithPackageName(it.packageName)
            }
        }
        return Toast.makeText(this, "can't find app with given keyword", Toast.LENGTH_SHORT).show()
    }

    private fun makeIntentSpecific(intent: Intent, pkgKeyword: String): Intent {
        val pkgList: List<ResolveInfo> = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY or PackageManager.GET_RESOLVED_FILTER
        )

        Log.d("resolveList", pkgList.toString())

        if (pkgList.isNotEmpty()) {
            for (resolveInfo in pkgList) {
                if (resolveInfo.activityInfo.packageName.contains(pkgKeyword)) {
                    val info: ResolveInfo = resolveInfo
                    val packageName = info.activityInfo.packageName
                    val className = info.activityInfo.name
                    intent.setClassName(packageName, className)
                    break
                }
            }
        }

        return intent
    }
}