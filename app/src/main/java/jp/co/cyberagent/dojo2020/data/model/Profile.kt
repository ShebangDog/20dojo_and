package jp.co.cyberagent.dojo2020.data.model

data class Account(val serviceName: String, val id: String, val url: String)
data class Profile(val name: String?, val iconUrl: String?, val accountList: List<Account>?)

sealed class UserData {
    data class PrimaryAccountData(val value: FirebaseUserInfo) : UserData()
    data class SecondaryAccountData(val value: Account) : UserData()
    data class Analytic(val value: List<TimeEachCategory>)
}
