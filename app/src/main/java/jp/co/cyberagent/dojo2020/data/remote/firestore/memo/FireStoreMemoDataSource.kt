package jp.co.cyberagent.dojo2020.data.remote.firestore.memo

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.data.remote.firestore.FireStoreConstants
import jp.co.cyberagent.dojo2020.data.remote.firestore.memosRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface FireStoreMemoDataSource {
    suspend fun saveMemo(uid: String, memo: Memo)

    fun fetchAllMemo(uid: String): Flow<List<Memo>>

    fun fetchFilteredMemoByCategory(uid: String, category: String): Flow<List<Memo>?>

    fun fetchMemoById(uid: String, id: String): Flow<Memo?>

    suspend fun deleteMemoById(uid: String, id: String)
}

@Singleton
class DefaultFireStoreMemoDataSource @Inject constructor() : FireStoreMemoDataSource {

    private val firestore = Firebase.firestore

    override suspend fun saveMemo(uid: String, memo: Memo) {
        val entity = memo.toEntity()
        val id = entity.id ?: return

        firestore.memosRef(uid).document(id).set(entity)
    }

    @ExperimentalCoroutinesApi
    override fun fetchAllMemo(uid: String) = callbackFlow {
        firestore.memosRef(uid).addSnapshotListener { snapshot, exception ->
            exception?.message?.run { return@addSnapshotListener } // if use state, emit error

            val memoEntityList = snapshot?.toObjects(MemoEntity::class.java)
            memoEntityList ?: return@addSnapshotListener

            val memoList = memoEntityList.mapNotNull { it.modelOrNull() }

            offer(memoList)
        }.also { awaitClose { it.remove() } }
    }

    @ExperimentalCoroutinesApi
    override fun fetchFilteredMemoByCategory(
        uid: String,
        category: String
    ) = callbackFlow {

        firestore.memosRef(uid)
            .whereEqualTo(FireStoreConstants.CATEGORY, category)
            .addSnapshotListener { snapshot, exception ->
                exception?.message?.run { return@addSnapshotListener }

                val memoEntityList = snapshot?.toObjects(MemoEntity::class.java)
                memoEntityList ?: return@addSnapshotListener

                val memoList = memoEntityList.mapNotNull { it.modelOrNull() }

                offer(memoList)
            }.also { awaitClose { it.remove() } }
    }

    @ExperimentalCoroutinesApi
    override fun fetchMemoById(uid: String, id: String) = callbackFlow {
        firestore.memosRef(uid).document(id).addSnapshotListener { snapshot, exception ->
            exception?.message?.run { return@addSnapshotListener }

            val memoEntity = snapshot?.toObject(MemoEntity::class.java)
            memoEntity ?: return@addSnapshotListener

            offer(memoEntity.modelOrNull())
        }.also { awaitClose { it.remove() } }

    }

    override suspend fun deleteMemoById(uid: String, id: String) {
        firestore.memosRef(uid).document(id).delete().await()
    }

    private fun Memo.toEntity(): MemoEntity {
        return MemoEntity(id, title, contents, time, category.name)
    }

}

@Module
@InstallIn(ApplicationComponent::class)
abstract class FirestoreMemoDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindFirestoreMemoDataSource(defaultFireStoreMemoDataSource: DefaultFireStoreMemoDataSource): FireStoreMemoDataSource
}