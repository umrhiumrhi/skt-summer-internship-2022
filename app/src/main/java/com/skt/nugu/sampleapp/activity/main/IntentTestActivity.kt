package com.skt.nugu.sampleapp.activity.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skt.nugu.sampleapp.utils.SimilarityChecker
import com.skt.nugu.sampleapp.databinding.ActivityIntentTestBinding
import com.skt.nugu.sampleapp.utils.RetrofitInterface
import com.skt.nugu.sampleapp.utils.TestingDirective
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder

class IntentTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntentTestBinding
    private lateinit var pkgList: MutableList<PackageInfo>
    private lateinit var retrofitService : RetrofitInterface

    var dataToPost = HashMap<String, Any>()

    companion object {
        private const val TAG = "IntentTestActivity"
        fun invokeActivity(context: Context) {
            context.startActivity(Intent(context, IntentTestActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun init() {
        binding = ActivityIntentTestBinding.inflate(layoutInflater)
        pkgList = getAndDisplayAllPackages()
        initRetrofit()

        binding.intentDialBtn.setOnClickListener {
            dialPhoneNumber("01075705994")
        }

        binding.intentNaverOpenBtn.setOnClickListener {
            launchAppWithPackageName("com.nhn.android.search")
        }

        binding.intentMapOpenBtn.setOnClickListener {
            launchMapApp(binding.queryEditText.text.toString())
        }

        binding.intentNaverSearchBtn.setOnClickListener {
            onlyForSearch(binding.queryEditText.text.toString())
        }

        binding.launchAppKeywordBtn.setOnClickListener {
            launchAppWithKeywordByUser(binding.queryEditText.text.toString())
        }
        binding.intentMediaPlaySearch.setOnClickListener {
            playSearchArtist()
        }

        binding.commandSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> dataToPost["appExec"] = "실행"
                    1 -> dataToPost["appExec"] = "검색"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.appSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> dataToPost["appName"] = "유튜브"
                    1 -> dataToPost["appName"] = "구글"
                    2 -> dataToPost["appName"] = "네이버"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.postAndLaunchBtn.setOnClickListener {
            dataToPost["searchKeyword"] = binding.query2EditText.text.toString()
            postCommand(dataToPost)
        }

    }

    private fun initRetrofit() {
        val retrofit = Retrofit.Builder().baseUrl("https://b4qhlvm5rokz3gwxqsyj6jqozu0rnvzz.lambda-url.ap-northeast-2.on.aws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitService = retrofit.create(RetrofitInterface::class.java)
    }

    private fun playSearchArtist() {

        var artist = binding.queryEditText.text.toString()

        val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
            putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*")
            putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist)
            putExtra(SearchManager.QUERY, artist)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
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
        val input = HashMap<String, Any>()
        var intent : Intent? = null

        input.put("appName", "네이버")
        input.put("appExec", "검색")
        input.put("searchKeyword", query)

        postCommand(input)
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

    private fun postCommand(data : HashMap<String, Any>) {
        retrofitService.postData(data).enqueue(object : Callback<TestingDirective> {
            override fun onResponse(
                call: Call<TestingDirective>,
                response: Response<TestingDirective>
            ) {
                Log.d("directiveCheck", response.body().toString())
                response.body()?.let{
                    val intentUri = it.directives[0].data.url
                    try {
                        intent = Intent.parseUri(intentUri, Intent.URI_INTENT_SCHEME)
                        intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@IntentTestActivity, "allocated app doesn't exist", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<TestingDirective>, t: Throwable) {
                Log.d("directiveCheck", "failed")
            }
        })
    }
}