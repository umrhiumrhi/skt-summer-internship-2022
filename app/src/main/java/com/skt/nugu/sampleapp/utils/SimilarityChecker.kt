package com.skt.nugu.sampleapp.utils

import java.lang.Integer.max
import java.lang.Math.min

class SimilarityChecker {

    companion object {
        private const val korBegin = 44032
        private const val korEnd = 55203
        private const val chosungBase = 588
        private const val jungsungBase = 28
        private const val jaumBegin = 12593
        private const val jaumEnd = 12622
        private const val moumBegin = 12623
        private const val moumEnd = 12643

        private val chosungList = listOf('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
            'ㅅ', 'ㅆ', 'ㅇ' , 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')

        private val jungsungList = listOf('ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ',
            'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ')

        private val jongsungList = listOf(' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ',
            'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')
    }

    private fun getLevenshteinDistance(s1 : List<Char>, s2 : List<Char>): Int {
        val m = s1.size
        val n = s2.size

        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 1..m) {
            dp[i][0] = i
        }

        for (j in 1..n) {
            dp[0][j] = j
        }

        var cost : Int
        for (i in 1..m) {
            for (j in 1..n) {
                cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost)
            }
        }
        return dp[m][n]
    }

    private fun getJamoLevenshteinDistance(s1 : String, s2 : String): Double {
        val m = s1.length
        val n = s2.length

        val dp = Array(m + 1) { DoubleArray(n + 1) }

        for (i in 1..m) {
            dp[i][0] = i.toDouble()
        }

        for (j in 1..n) {
            dp[0][j] = j.toDouble()
        }

        var cost : Double
        for (i in 1..m) {
            for (j in 1..n) {
                cost = subCost(s1[i-1], s2[j-1])
                dp[i][j] = min(min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost)
            }
        }
        return dp[m][n]
    }

    private fun subCost(c1 : Char, c2 : Char) : Double {
        if (c1 == c2) {
            return 0.0
        }
        return getLevenshteinDistance(decompose(c1), decompose(c2)) / 3.0
    }

    fun findSimilarity(s1: String?, s2: String?): Double {
        if (s1.isNullOrEmpty() or s2.isNullOrEmpty()) return -1.0
        var result = -1.0
        val maxLength = max(s1!!.length, s2!!.length)
        if (maxLength > 0) {
           result =  (maxLength * 1.0 - getJamoLevenshteinDistance(s1, s2)) / maxLength * 1.0

        } else {
            result = 1.0
        }
        return result
    }

    fun decompose(c : Char) : List<Char> {
        if (!checkKorean(c)) {
            return emptyList()
        }
        var i = c.code
        if (i in jaumBegin..jaumEnd) {
            return listOf(c, ' ', ' ')
        }
        if (i in moumBegin..moumEnd) {
            return listOf(' ', c, ' ')
        }

        i -= korBegin
        val cho : Int = i / chosungBase
        val jung : Int = ( i - cho * chosungBase ) / jungsungBase
        val jong : Int = ( i - cho * chosungBase - jung * jungsungBase)

        return listOf(chosungList[cho], jungsungList[jung], jongsungList[jong])
    }

    fun compose(chosung : Char, jungsung : Char, jongsung : Char) : Char {
        val tmp = korBegin + chosungBase * chosungList.indexOf(chosung) + jungsungBase * jungsungList.indexOf(jungsung) +
                jongsungList.indexOf(jongsung)

        return tmp.toChar()
    }

    fun checkKorean(c : Char) : Boolean {
        val i = c.code
        return (i in korBegin..korEnd) or (i in jaumBegin..jaumEnd) or (i in moumBegin..moumEnd)
    }

}