package jp.co.cyberagent.dojo2020.data.remote.auth

import jp.co.cyberagent.dojo2020.data.model.FirebaseUserInfo
import kotlinx.coroutines.flow.Flow

interface FirebaseUserInfoDataSource {
    fun fetchFirebaseUserInfo(): Flow<FirebaseUserInfo>
}