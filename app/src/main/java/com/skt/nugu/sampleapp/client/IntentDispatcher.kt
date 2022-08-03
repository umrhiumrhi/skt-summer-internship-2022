package com.skt.nugu.sampleapp.client

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.skt.nugu.sampleapp.utils.SimilarityChecker
import com.skt.nugu.sdk.core.interfaces.message.Header


class IntentDispatcher(val context: Context) {

    companion object {
        private const val MATCH_THRESHOLD_VALUE : Double = 0.5
        private const val COMPLETE_MATCH_VALUE : Double = 0.9999
    }

    val uiHandler = Handler(Looper.getMainLooper())
    private val similarityChecker = SimilarityChecker()
    var isResolvedIntent = false

    fun onAsrResultComplete(result: String, header: Header) {
        //only action_main

        if (true) return // local 처리 비활성화
        var resolveInfoArrayList = ArrayList<ArrayList<ResolveInfo>>()
        if (result.contains("실행") && result.indexOf("실행") != 0) {
            Thread {
                isResolvedIntent = false
                val parsedResult = result.substring(0 until result.indexOf("실행") - 1)
                // 모든 어플리케이션의 라벨과 사용자 발화 내용의 유사도 비교, 가장 높은 것을 채택하여 실행

                resolveInfoArrayList = getResolveInfoListWithAction(0)

                getMostProperResolveInfo(resolveInfoArrayList, parsedResult)?.let {
                    Log.d("chkk",
                        "[RESULT] appName : " + it.activityInfo.loadLabel(context.packageManager)
                            .toString() + ", Utterance : " + parsedResult
                    )
                    uiHandler.post {
                        try {
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.setClassName(it.activityInfo.packageName, it.activityInfo.name)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            isResolvedIntent = true
                        } catch (e: Exception) {
                            isResolvedIntent = false
                            e.printStackTrace()
                        }
                    }
                }
            }.start()
        } else if (result.contains("검색") && result.indexOf("검색") != 0) {
            Thread {
                isResolvedIntent = false
                val parsedResult = result.substring(0 until result.indexOf("검색") - 1)

                resolveInfoArrayList = getResolveInfoListWithAction(3)

                resolveInfoArrayList[0][0]?.let {
                    Log.d("chkk",
                        "[RESULT] appName : " + it.activityInfo.loadLabel(context.packageManager)
                            .toString() + ", Utterance : " + parsedResult
                    )
                    uiHandler.post {
                        try {
                            val intent = Intent(Intent.ACTION_WEB_SEARCH)
                            intent.setClassName(it.activityInfo.packageName, it.activityInfo.name)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra(SearchManager.QUERY, parsedResult)
                            context.startActivity(intent)
                            isResolvedIntent = true
                        } catch (e: Exception) {
                            isResolvedIntent = false
                            e.printStackTrace()
                        }
                    }
                }
            }.start()
        }
    }

    private fun getResolveInfoListWithAction(index : Int) : ArrayList<ArrayList<ResolveInfo>> {
        var tmpResolveInfoArrayList = ArrayList<ArrayList<ResolveInfo>>()

        context.packageManager?.let {
            var packageManager = it
            val installedPackage = packageManager.getInstalledPackages(0)
            val actionList: List<String> = listOf(
                Intent.ACTION_MAIN,
                Intent.ACTION_SEARCH,
                Intent.ACTION_VIEW,
                Intent.ACTION_WEB_SEARCH
            )

            for (packageItem in installedPackage) {
                val pi: PackageInfo = packageItem
                val packageName = pi.packageName
                val versionName = pi.versionName
                val appLabel = pi.applicationInfo.loadLabel(packageManager)

                for (action in listOf(actionList[index])) {
                    val intent = Intent(action)
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
                }
            }
        }
        return tmpResolveInfoArrayList
    }

    private fun getMostProperResolveInfo(resolveInfoArrayList : ArrayList<ArrayList<ResolveInfo>>, parsedResult : String): ResolveInfo? {
        var maxSimilarity : Double = 0.0
        var mostSimilarResolveInfo : ResolveInfo? = null

        for (data in resolveInfoArrayList) {
            val appName = data[0].activityInfo.loadLabel(context.packageManager).toString()
            val similarity = similarityChecker.findSimilarity(appName, parsedResult)

            Log.d("chkk", "appName : "+data[0].activityInfo.loadLabel(context.packageManager).toString()+", Utterance : "+parsedResult+", Similarity : $similarity")

            if (similarity < MATCH_THRESHOLD_VALUE) {
                continue
            } else if (similarity > COMPLETE_MATCH_VALUE) {
                mostSimilarResolveInfo = data[0]
                maxSimilarity = similarity
                break
            } else if (similarity > maxSimilarity) {
                mostSimilarResolveInfo = data[0]
                maxSimilarity = similarity
            }
        }


        return mostSimilarResolveInfo
    }

}