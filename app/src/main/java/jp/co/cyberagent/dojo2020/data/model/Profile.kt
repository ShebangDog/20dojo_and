package jp.co.cyberagent.dojo2020.data.model

import jp.co.cyberagent.dojo2020.R

data class Account(val serviceName: String, val id: String, val url: String) {
    companion object {
        val iconHashMap = mapOf(
            "Twitter" to R.mipmap.twitter_logo,
            "Github" to R.mipmap.github_logo
        )

        val defaultIcon = R.mipmap.github_logo
    }
}
data class Profile(val name: String?, val iconUrl: String?, val accountList: List<Account>?)

sealed class UserItem(val viewType: ViewType) {
    data class PrimaryAccountItem(val value: FirebaseUserInfo?) : UserItem(ViewType.PrimaryAccount)
    data class SecondaryAccountItem(val value: Account?) : UserItem(ViewType.SecondaryAccount)
    data class AnalyticItem(val value: List<TimeEachCategory>?) : UserItem(ViewType.Analytic)
}

sealed class ViewType(val value: Int) {
    object PrimaryAccount : ViewType(1)
    object SecondaryAccount : ViewType(2)
    object Analytic : ViewType(3)
}
