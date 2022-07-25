package com.skt.nugu.sampleapp.client

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.skt.nugu.sdk.core.interfaces.message.Header

class IntentDispatcher(val context: Context) {

    val uiHandler = Handler(Looper.getMainLooper())

    var isResolvedIntent = false

    fun onAsrResultComplete(result: String, header: Header) {

        var intentString: String? = null

        isResolvedIntent = false

        if (result.contains("실행") || result.contains("동작")) {
            //
            if (result.contains("카카오")) {
                if (result.contains("웹툰")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=net.daum.android.webtoon/net.daum.android.WebtoonActivity;end"
                } else if (result.contains("맵")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=net.daum.android.map/com.kakao.map.main.view.IntroActivity;end"
                } else if (result.contains("뱅크")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.kakaobank.channel/.presentation.view.activity.StartActivity;end"
                } else if (result.contains("내비") || result.contains("네비")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.locnall.KimGiSa/.activity.IntroActivity;end"
                } else {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.kakao.talk/.activity.SplashActivity;end"
                }

            } else if (result.contains("네이버")) {
                if (result.contains("웹툰")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.android.webtoon/.splash.SplashActivity;end"
                } else if (result.contains("지도")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.android.nmap/com.naver.map.LaunchActivity;end"
                } else if (result.contains("부동산")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.land.android/.activity.LandActivity;end"
                } else if (result.contains("까페") || result.contains("카페")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.android.navercafe/.feature.LauncherActivity;end"
                } else {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.nhn.android.search/.ui.pages.SearchHomePage;end"
                }
            } else if (result.contains("구글")) {
                if (result.contains("지도")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.apps.maps/com.google.android.maps.MapsActivity;end"
                } else if (result.contains("뉴스")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.apps.magazines/com.google.apps.dots.android.app.activity.CurrentsStartActivity;end"
                } else if (result.contains("캘린더")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.calendar/com.android.calendar.AllInOneActivity;end"
                } else if (result.contains("포토")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.apps.photos/.home.HomeActivity;end"
                } else if (result.contains("크롬")) {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.android.chrome/com.google.android.apps.chrome.Main;end"
                } else {
                    intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.googlequicksearchbox/.SearchActivity;end"
                }
            } else if (result.contains("카메라")) {
                intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.sec.android.app.camera/.Camera;end"
            } else if (result.contains("텔레그램")) {
                intentString = "intent:#Intent;action=android.intent.action.MAIN;component=org.telegram.messenger/.DefaultIcon;end"
                // [Not working]
            } else if (result.contains("유투브") || result.contains("유튜브")) {
                intentString = "intent:#Intent;action=android.intent.action.MAIN;component=com.google.android.youtube/.app.honeycomb.Shell%24HomeActivity;end"
            }

            intentString?.let {
                uiHandler.post {
                    try {
                        var intent = Intent.parseUri(it, Intent.URI_INTENT_SCHEME)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        isResolvedIntent = true
                    } catch (e: Exception) {
                        isResolvedIntent = false
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}