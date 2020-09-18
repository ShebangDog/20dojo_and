package jp.co.cyberagent.dojo2020.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import jp.co.cyberagent.dojo2020.data.model.FirebaseUserInfo
import jp.co.cyberagent.dojo2020.data.remote.auth.FirebaseAuthentication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UserInfoRepository {
    fun fetchUserInfo(): Flow<FirebaseUserInfo?>
}

@ActivityScoped
class DefaultUserInfoRepository @Inject constructor() : UserInfoRepository {

    @ExperimentalCoroutinesApi
    override fun fetchUserInfo(): Flow<FirebaseUserInfo?> {
        return FirebaseAuthentication.currentUser.map { firebaseUser ->
            val user = firebaseUser ?: return@map null

            val name = user.displayName ?: return@map null
            val uid = user.uid
            val uri = user.photoUrl

            FirebaseUserInfo(uid, name, uri)
        }
    }
}

@Module
@InstallIn(ActivityComponent::class)
abstract class UserInfoModule {

    @ActivityScoped
    @Binds
    abstract fun bindUserInfoRepository(defaultUserInfoRepository: DefaultUserInfoRepository): UserInfoRepository
}