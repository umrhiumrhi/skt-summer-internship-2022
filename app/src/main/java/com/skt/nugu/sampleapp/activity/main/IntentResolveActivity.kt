package com.skt.nugu.sampleapp.activity.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skt.nugu.sampleapp.databinding.ActivityIntentResolveBinding
import com.skt.nugu.sampleapp.utils.TLog
import org.json.JSONArray
import org.json.JSONObject

class IntentResolveActivity : AppCompatActivity() {

    lateinit var binding: ActivityIntentResolveBinding

    private var intentList = ArrayList<String>()

    companion object {
        private const val TAG = "IntentResolveActivity"
        fun invokeActivity(context: Context) {
            context.startActivity(Intent(context, IntentResolveActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIntentResolveBinding.inflate(layoutInflater)

        binding.btnGetResolveInfo.setOnClickListener {
            getIntentResolveInfo()
        }

        binding.btnBuildContext.setOnClickListener {
        }

        binding.btnSelectedIntentSet.setOnClickListener {
            try {
                var index = binding.etSelectedIntent.text.toString().toInt()
                var intent = intentList.get(index)
                TLog.d(TAG, "index:$index, intent:$intent")
                binding.tvSelectedIntent.text = intent
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.btnSelectedIntentExec.setOnClickListener {
            try {
                var index = binding.etSelectedIntent.text.toString().toInt()
                var uriString = intentList[index]
                TLog.d(TAG, "index:$index, uriString:$uriString")
                openIntent(uriString)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.tvIntentInfo.movementMethod = ScrollingMovementMethod()

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun openIntent(uri: String?) {
        TLog.d(TAG, "openIntent() : uri = <$uri>")

        try {
            Intent.parseUri(uri, Intent.URI_INTENT_SCHEME).let { intent ->
                TLog.d(TAG, "openIntent() : intent($intent)")
                resolveActivity(baseContext, intent)?.let {
                    TLog.d(TAG, "openIntent() : resolveActivity / resolveInfo : $it")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.action = Intent.ACTION_MAIN
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resolveActivity(
        context: Context,
        intent: Intent
    ) = context.packageManager.resolveActivity(intent, PackageManager.MATCH_ALL)

    private fun getIntentResolveInfo() {

        Thread {
            packageManager?.let {
                var packageManager = it
                val installedPackage = packageManager.getInstalledPackages(0)
                // TODO: 외부에서 설정값 유입 가능 후보(1)
                val actionList: List<String> = listOf(Intent.ACTION_MAIN/*,Intent.ACTION_SEARCH,Intent.ACTION_VIEW*/)
                //  println("\"applicationList\" : [ {")
                val jsonObjectApplcation = JSONObject()
                val jsonObjectApplcationList = JSONArray()

                intentList.clear()

                for (packageItem in installedPackage) {
                    val pi: PackageInfo = packageItem
                    val packageName = pi.packageName
                    val versionName = pi.versionName
                    val appLabel = pi.applicationInfo.loadLabel(packageManager)

                    /*val browser = "com.polestar.vivaldi.production.android"

                    if (browser.equals(packageName)) {
                        TLog.w(TAG, "found!")
                    }
*/
                    for (action in actionList) {
                        val intent = Intent(action)
                        // TODO: 외부에서 설정값 유입 가능 후보(2)
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        intent.setPackage(packageName)
                        val ris: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

                        if (ris.isEmpty()) {
                            continue
                        }
//                println("\"appInfo\" : [{")
//                println("\"appName\" =  \" $appLabel\" \n \"packageName\" :\" $packageName\"")
//                println("\"intentList\" : [{")
                        val jsonObjectAppinfo = JSONObject()
                        val jsonObjectintentList = JSONArray()
                        for (ri in ris) {
                            val packageName = ri.activityInfo.packageName
                            val className = ri.activityInfo.name
                            val label = ri.loadLabel(packageManager).toString()
                            val icon = ri.loadIcon(packageManager)
                            val intent2 = Intent(action)

                            if (ri.filter != null) {
                                var schemes = ri.filter.schemesIterator()
                                TLog.d(TAG, "schemes:$schemes")
                                for (s in schemes) {
                                    TLog.w(TAG, "s.length:${s.length}")
                                    TLog.w(TAG, "s:${s}")
                                }
                            }

                            intent2.setClassName(packageName, className)
//                    println(" info $packageName $className  $label ")
                            //                   println("\"activitName\" : \"$label\" \n \"Intents\" : \"${intent2.toUri(0)}\" " ,)
                            val jsonObjectIntentinfo = JSONObject()
                            val jsonObjectIntentinfoValue = JSONObject()
                            val intentUri = intent2.toUri(Intent.URI_INTENT_SCHEME)
                            jsonObjectIntentinfoValue.put("name", label)
                            jsonObjectIntentinfoValue.put("intent", intentUri)
                            jsonObjectIntentinfo.put("intentInfo", jsonObjectIntentinfoValue)
                            jsonObjectintentList.put(jsonObjectIntentinfo)
                            //
                            intentList.add(intentUri)
                        }
                        jsonObjectAppinfo.put("appName", appLabel)
                        jsonObjectAppinfo.put("packageName", packageName)
                        jsonObjectAppinfo.put("intentList", jsonObjectintentList)
//                println("    }  ] ")
//                println("} ]")
                        jsonObjectApplcationList.put(jsonObjectAppinfo)
                    }
                    jsonObjectApplcation.put("applicationList", jsonObjectApplcationList)
//            println(" ------------------------------------------------------  " )
                }

                var result = jsonObjectApplcation.toString()
                //  println("} ]")
                println(result)

                runOnUiThread {
                    Toast.makeText(applicationContext, "[done] Get Intent", Toast.LENGTH_LONG).show()
                    binding.tvIntentInfo.text = result
                    //
                    val intentSize = intentList.size
                    binding.tvIntentSummary.text = "- intentSize:$intentSize"

                }
            }
        }.start()
    }
}