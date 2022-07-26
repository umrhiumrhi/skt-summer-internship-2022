package com.skt.nugu.sampleapp.client

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
    }

    val uiHandler = Handler(Looper.getMainLooper())
    private val similarityChecker = SimilarityChecker()
    var isResolvedIntent = false

    fun onAsrResultComplete(result: String, header: Header) {
        //only action_main
        var resolveInfoArrayList = ArrayList<ArrayList<ResolveInfo>>()

        if (result.contains("실행") && result.indexOf("실행") != 0) {
            Thread {
                isResolvedIntent = false
                val parsedResult = result.substring(0 until result.indexOf("실행")-1)
                // 모든 어플리케이션의 라벨과 사용자 발화 내용의 유사도 비교, 가장 높은 것을 채택하여 실행

                resolveInfoArrayList = getResolveInfoListWithAction(0)

                getMostProperResolveInfo(resolveInfoArrayList, parsedResult)?.let {
                    Log.d("chkk", "[RESULT] appName : "+it.activityInfo.loadLabel(context.packageManager).toString()+", Utterance : "+parsedResult)
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
        }




//        if (result.contains("실행") || result.contains("동작")) {
//            //
//            if (result.contains("카카오")) {
//                if (result.contains("웹툰")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=net.daum.android.webtoon/net.daum.android.WebtoonActivity;end"
//                } else if (result.contains("맵")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=net.daum.android.map/com.kakao.map.main.view.IntroActivity;end"
//                } else if (result.contains("뱅크")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.kakaobank.channel/.presentation.view.activity.StartActivity;end"
//                } else if (result.contains("내비") || result.contains("네비")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.locnall.KimGiSa/.activity.IntroActivity;end"
//                } else {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.kakao.talk/.activity.SplashActivity;end"
//                }
//
//            } else if (result.contains("네이버")) {
//                if (result.contains("웹툰")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.android.webtoon/.splash.SplashActivity;end"
//                } else if (result.contains("지도")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.android.nmap/com.naver.map.LaunchActivity;end"
//                } else if (result.contains("부동산")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.land.android/.activity.LandActivity;end"
//                } else if (result.contains("까페") || result.contains("카페")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.android.navercafe/.feature.LauncherActivity;end"
//                } else {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.android.search/.ui.pages.SearchHomePage;end"
//                }
//            } else if (result.contains("구글")) {
//                if (result.contains("지도")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.apps.maps/com.google.android.maps.MapsActivity;end"
//                } else if (result.contains("뉴스")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.apps.magazines/com.google.apps.dots.android.app.activity.CurrentsStartActivity;end"
//                } else if (result.contains("캘린더")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.calendar/com.android.calendar.AllInOneActivity;end"
//                } else if (result.contains("포토")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.apps.photos/.home.HomeActivity;end"
//                } else if (result.contains("크롬")) {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.android.chrome/com.google.android.apps.chrome.Main;end"
//                } else {
//                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.googlequicksearchbox/.SearchActivity;end"
//                }
//            } else if (result.contains("카메라")) {
//                intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.sec.android.app.camera/.Camera;end"
//            } else if (result.contains("텔레그램")) {
//                intentString = "intent:#Intent;action=android.intent.action.MAIN;component=org.telegram.messenger/.DefaultIcon;end"
//                // [Not working]
//            } else if (result.contains("유투브") || result.contains("유튜브")) {
//                intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.youtube/.app.honeycomb.Shell%24HomeActivity;end"
//            }
//
//            intentString?.let {
//                uiHandler.post {
//                    try {
//                        var intent = Intent.parseUri(it, Intent.URI_INTENT_SCHEME)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        context.startActivity(intent)
//                        isResolvedIntent = true
//                    } catch (e: Exception) {
//                        isResolvedIntent = false
//                        e.printStackTrace()
//                    }
//                }
//            }
//        }
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
            } else if (similarity > 0.9999) {
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