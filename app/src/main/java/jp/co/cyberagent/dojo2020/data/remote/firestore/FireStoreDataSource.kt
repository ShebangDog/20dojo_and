package jp.co.cyberagent.dojo2020.data.remote.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jp.co.cyberagent.dojo2020.data.model.Memo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface FireStoreDataSource {
    suspend fun saveMemo(uid: String, memo: Memo)

    suspend fun fetchAllMemo(uid: String): Flow<List<Memo>>
}

class DefaultFireStoreDataSource : FireStoreDataSource {
    private val firestore = Firebase.firestore

    override suspend fun saveMemo(uid: String, memo: Memo) {
        val entity = memo.toEntityForRemote()

        firestore.memosRef(uid).document().set(entity)
    }

    override suspend fun fetchAllMemo(uid: String) = flow<List<Memo>> {
        try {
            val snapshot = firestore.memosRef(uid).get().await()
            val memoEntityList = snapshot.toObjects(MemoEntity::class.java)
            val nullableMemoList = memoEntityList.map { entity -> entity.modelOrNull() } // if use state, throw exception

            val notNullMemoList = nullableMemoList.map { it ?: return@flow }

            emit(notNullMemoList)
        } catch (e: Exception) {
            //Todo(emit failed)
        }
    }

    private fun Memo.toEntityForRemote(): MemoEntity {
        return MemoEntity(title, contents, time)
    }

    private fun MemoEntity.modelOrNull(): Memo? {
        val containsNull = listOf(title, contents, time).contains(null)
        if (containsNull) {
            return null
        }

        return Memo(title!!, contents!!, time!!)
    }

}