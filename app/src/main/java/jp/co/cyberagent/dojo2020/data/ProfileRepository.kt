package jp.co.cyberagent.dojo2020.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import jp.co.cyberagent.dojo2020.data.local.ProfileDataSource
import jp.co.cyberagent.dojo2020.data.model.Profile
import jp.co.cyberagent.dojo2020.data.remote.firestore.profile.FireStoreProfileDataSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

interface ProfileRepository {
    suspend fun saveProfile(uid: String?, profile: Profile)

    fun fetchProfile(uid: String?): Flow<Profile?>
}

@ActivityScoped
class DefaultProfileRepository @Inject constructor(
    private val localProfileDataSource: ProfileDataSource,
    private val remoteProfileDataSource: FireStoreProfileDataSource
) : ProfileRepository {

    override suspend fun saveProfile(uid: String?, profile: Profile) {
        localProfileDataSource.saveProfile(profile)
        uid ?: return

        remoteProfileDataSource.saveProfile(uid, profile)
    }

    @FlowPreview
    override fun fetchProfile(uid: String?): Flow<Profile?> {
        val localProfileFlow = localProfileDataSource.fetchProfile()

        val profileFlow = localProfileFlow.flatMapConcat { profile ->
            if (uid != null && profile == null) {
                return@flatMapConcat remoteProfileDataSource.fetchProfile(uid)
            }

            return@flatMapConcat localProfileFlow
        }

        return profileFlow
    }
}

@Module
@InstallIn(ActivityComponent::class)
abstract class ProfileRepositoryModule {

    @ActivityScoped
    @Binds
    abstract fun bindProfileRepository(defaultProfileRepository: DefaultProfileRepository): ProfileRepository
}
