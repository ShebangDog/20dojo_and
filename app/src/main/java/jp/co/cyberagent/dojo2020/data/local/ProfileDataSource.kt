package jp.co.cyberagent.dojo2020.data.local

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import jp.co.cyberagent.dojo2020.data.local.db.ApplicationDataBase
import jp.co.cyberagent.dojo2020.data.local.db.profile.ProfileEntity
import jp.co.cyberagent.dojo2020.data.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface ProfileDataSource {
    suspend fun saveProfile(profile: Profile)

    fun fetchProfile(): Flow<Profile?>
}

@Singleton
class DefaultProfileDataSource @Inject constructor(
    private val database: ApplicationDataBase
) : ProfileDataSource {

    override suspend fun saveProfile(profile: Profile) {
        database.profileDao().insert(profile.toEntity())
    }

    override fun fetchProfile(): Flow<Profile?> {
        return database.profileDao().fetch().map { it?.toModel() }
    }

    private fun Profile.toEntity(): ProfileEntity {
        return ProfileEntity.createForInsert(name, iconUrl, accountList)
    }

}

@Module
@InstallIn(ApplicationComponent::class)
abstract class ProfileDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindProfileDataSource(defaultProfileDataSource: DefaultProfileDataSource): ProfileDataSource
}