package jp.co.cyberagent.dojo2020.data.remote.firestore.profile

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import jp.co.cyberagent.dojo2020.data.model.Profile
import jp.co.cyberagent.dojo2020.data.remote.firestore.profileRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

interface FirestoreProfileDataSource {
    suspend fun saveProfile(uid: String, profile: Profile)

    fun fetchProfile(uid: String): Flow<Profile?>
}

@Singleton
class DefaultFirestoreProfileDataSource @Inject constructor() : FirestoreProfileDataSource {

    private val firestore = Firebase.firestore

    override suspend fun saveProfile(uid: String, profile: Profile) {
        firestore.profileRef(uid).set(profile)
    }

    @ExperimentalCoroutinesApi
    override fun fetchProfile(uid: String) = callbackFlow {
        firestore.profileRef(uid).addSnapshotListener { snapshot, exception ->
            exception?.message?.run { return@addSnapshotListener }

            val profileEntity = snapshot?.toObject(ProfileEntity::class.java)

            offer(profileEntity?.toModel())
        }.also { awaitClose { it.remove() } }
    }

}

@Module
@InstallIn(ApplicationComponent::class)
abstract class FirestoreProfileDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindFirestoreProfileDataSource(defaultFirestoreProfileDataSource: DefaultFirestoreProfileDataSource): FirestoreProfileDataSource
}