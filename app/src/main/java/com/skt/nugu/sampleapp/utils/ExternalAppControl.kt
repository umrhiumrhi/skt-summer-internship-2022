package com.skt.eaa.debug.capability

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class ExternalAppControl(val context: Context) {

    val TAG = "ExternalAppControl"

    var appContext: String? = null

    init {
        getUpdatedContextIfNeed()
    }

    @Synchronized
    fun getUpdatedContextIfNeed(): String? {
        if (appContext == null) {
            getIntentResolveInfo()
        }
        //return appContext!!
        if (appContext == null) {
            return null//JsonHelper.toJsonObject("{\"param\":\"this is test for ext.app.control\"}").toJson()
        } else {
            return appContext!!
        }
    }

    private fun getIntentResolveInfo() {
        Log.d(TAG, "getIntentResolveInfo() start")
        Thread {
            var packageManager = context.packageManager
            val installedPackage = packageManager.getInstalledPackages(0)
            // TODO: 외부에서 설정값 유입 가능 후보(1)
            val actionList: List<String> = listOf(Intent.ACTION_MAIN,Intent.ACTION_SEARCH,Intent.ACTION_VIEW,Intent.ACTION_WEB_SEARCH)
            //  println("\"applicationList\" : [ {")
            val jsonObjectApplcation = JSONObject()
            val jsonObjectApplcationList = JSONArray()

            for (packageItem in installedPackage) {
                val pi: PackageInfo = packageItem
                val packageName = pi.packageName
                val versionName = pi.versionName
                val appLabel = pi.applicationInfo.loadLabel(packageManager)

                /*val browser = "com.polestar.vivaldi.production.android"

                if (browser.equals(packageName)) {
                    TLog.w(TAG, "found!")
                }
    */          val jsonObjectAppinfo = JSONObject()
                val jsonObjectintentList = JSONArray()
                for (action in actionList) {
                    val intent = Intent(action)
                    // TODO: 외부에서 설정값 유입 가능 후보(2)
                    if (action == Intent.ACTION_MAIN) intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intent.setPackage(packageName)
                    val ris: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

                    if (ris.isEmpty()) {
                        continue
                    }
//                println("\"appInfo\" : [{")
//                println("\"appName\" =  \" $appLabel\" \n \"packageName\" :\" $packageName\"")
//                println("\"intentList\" : [{")

                    for (ri in ris) {
                        val packageName = ri.activityInfo.packageName
                        val className = ri.activityInfo.name
                        val label = ri.loadLabel(packageManager).toString()
                        val icon = ri.loadIcon(packageManager)
                        val intent2 = Intent(action)

                        if (ri.filter != null) {
                            var schemes = ri.filter.schemesIterator()
                            Log.d(TAG, "schemes:$schemes")
                            for (s in schemes) {
                                Log.w(TAG, "s.length:${s.length}")
                                Log.w(TAG, "s:${s}")
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
                    }
                }
                if (jsonObjectintentList.isNull(0)) continue
                jsonObjectAppinfo.put("appName", appLabel)
                jsonObjectAppinfo.put("packageName", packageName)
                jsonObjectAppinfo.put("intentList", jsonObjectintentList)
//                println("    }  ] ")
//                println("} ]")
                jsonObjectApplcationList.put(jsonObjectAppinfo)
                jsonObjectApplcation.put("applicationList", jsonObjectApplcationList)
//            println(" ------------------------------------------------------  " )
            }

            appContext = jsonObjectApplcation.toString()
            Log.d(TAG, "getIntentResolveInfo() end: $appContext")
        }.start()
    }
}