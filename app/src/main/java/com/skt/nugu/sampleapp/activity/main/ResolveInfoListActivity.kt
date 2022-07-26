package com.skt.nugu.sampleapp.activity.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.skt.nugu.sampleapp.R
import com.skt.nugu.sampleapp.adapter.RiRecyclerViewAdapter
import com.skt.nugu.sampleapp.databinding.ActivityResolveInfoListBinding
import com.skt.nugu.sampleapp.utils.TLog


class ResolveInfoListActivity : AppCompatActivity() {

    private val binding: ActivityResolveInfoListBinding by lazy {
        ActivityResolveInfoListBinding.inflate(layoutInflater)
    }

    private var intentList = ArrayList<String>()
    private var resolveInfoList = ArrayList<ArrayList<ResolveInfo>>()
    private var mainIndex: Int = 0

    companion object {
        private const val TAG = "ResolveInfoListActivity"
        fun invokeActivity(context: Context) {
            context.startActivity(Intent(context, ResolveInfoListActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun init() {
        disableAllButton()
        getIntentResolveInfo(0)

        binding.mainBtn.setOnClickListener {
            getIntentResolveInfo(0)
        }
        binding.searchBtn.setOnClickListener {
            getIntentResolveInfo(1)
        }
        binding.viewBtn.setOnClickListener {
            getIntentResolveInfo(2)
        }
        binding.webSearchBtn.setOnClickListener {
            getIntentResolveInfo(3)
        }
    }

    private fun getIntentResolveInfo(index: Int) {
        disableAllButton()
        Thread {
            packageManager?.let {
                var packageManager = it
                val installedPackage = packageManager.getInstalledPackages(0)
                // TODO: 외부에서 설정값 유입 가능 후보(1)
                val actionList: List<String> = listOf(
                    Intent.ACTION_MAIN,
                    Intent.ACTION_SEARCH,
                    Intent.ACTION_VIEW,
                    Intent.ACTION_WEB_SEARCH
                )
                //  println("\"applicationList\" : [ {")

                intentList.clear()
                var tmpResolveInfoArrayList = ArrayList<ArrayList<ResolveInfo>>()

                for (packageItem in installedPackage) {
                    val pi: PackageInfo = packageItem
                    val packageName = pi.packageName
                    val versionName = pi.versionName
                    val appLabel = pi.applicationInfo.loadLabel(packageManager)

                    for (action in listOf(actionList[index])) {
                        val intent = Intent(action)
                        // TODO: 외부에서 설정값 유입 가능 후보(2)
                        //intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        intent.setPackage(packageName)
                        val ris: ArrayList<ResolveInfo> =
                            packageManager.queryIntentActivities(
                                intent,
                                0
                            ) as ArrayList<ResolveInfo>

                        if (ris.isNotEmpty()) {
                            tmpResolveInfoArrayList.add(ris)
                        } else {
                            continue
                        }

                        for (ri in ris) {
                            val packageName = ri.activityInfo.packageName
                            val className = ri.activityInfo.name
                            val label = ri.loadLabel(packageManager).toString()
                            //val icon = ri.loadIcon(packageManager)
                            val intent2 = Intent(action)

//                            if (ri.filter != null) {
//                                var schemes = ri.filter.schemesIterator()
//                                TLog.d(ResolveInfoListActivity.TAG, "schemes:$schemes")
//                                for (s in schemes) {
//                                    TLog.w(ResolveInfoListActivity.TAG, "s.length:${s.length}")
//                                    TLog.w(ResolveInfoListActivity.TAG, "s:${s}")
//                                }
//                            }

                            intent2.setClassName(packageName, className)
                            val intentUri = intent2.toUri(Intent.URI_INTENT_SCHEME)

                            intentList.add(intentUri)
                        }
                    }
                }

                //  println("} ]")
                resolveInfoList.clear()
                resolveInfoList.addAll(tmpResolveInfoArrayList)
                runOnUiThread {
                    Toast.makeText(applicationContext, "[done] Get Intent", Toast.LENGTH_LONG)
                        .show()

                    binding.resolveInfoRecyclerView.adapter =
                        RiRecyclerViewAdapter(this, resolveInfoList, index)
                    binding.resolveInfoRecyclerView.layoutManager = LinearLayoutManager(this)
                    (binding.resolveInfoRecyclerView.adapter)!!.notifyDataSetChanged()

                    enableAllButton()

                    Log.d("huss", resolveInfoList.toString())
                }
            }
        }.start()
    }

    private fun disableAllButton() {
        binding.apply {
            viewBtn.isClickable = false
            mainBtn.isClickable = false
            webSearchBtn.isClickable = false
            searchBtn.isClickable = false
        }
    }

    private fun enableAllButton() {
        binding.apply {
            viewBtn.isClickable = true
            mainBtn.isClickable = true
            webSearchBtn.isClickable = true
            searchBtn.isClickable = true
        }
    }
}