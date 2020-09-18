package jp.co.cyberagent.dojo2020.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import jp.co.cyberagent.dojo2020.data.local.MemoDataSource
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.data.remote.firestore.memo.FireStoreMemoDataSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

interface MemoRepository {
    suspend fun saveMemo(uid: String?, memo: Memo)

    fun fetchAllMemo(uid: String?): Flow<List<Memo>>

    fun fetchFilteredMemoByCategory(uid: String?, category: Category): Flow<List<Memo>?>

    fun fetchMemoById(uid: String?, id: String): Flow<Memo?>

    suspend fun deleteMemoById(uid: String?, id: String)
}

@ActivityScoped
class DefaultMemoRepository @Inject constructor(
    private val localMemoDataSource: MemoDataSource,
    private val remoteMemoDataSource: FireStoreMemoDataSource
) : MemoRepository {

    override suspend fun saveMemo(uid: String?, memo: Memo) {
        localMemoDataSource.saveMemo(memo)
        uid ?: return

        remoteMemoDataSource.saveMemo(uid, memo)
    }

    @FlowPreview
    override fun fetchAllMemo(uid: String?): Flow<List<Memo>> {
        val localMemoListFlow = localMemoDataSource.fetchAllMemo()

        val memoListFlow = localMemoListFlow.flatMapConcat { localMemoList ->
            if (uid != null && localMemoList.isEmpty()) {
                return@flatMapConcat remoteMemoDataSource.fetchAllMemo(uid)
            }

            return@flatMapConcat localMemoListFlow
        }

        return memoListFlow
    }

    @FlowPreview
    override fun fetchFilteredMemoByCategory(
        uid: String?,
        category: Category
    ): Flow<List<Memo>?> {
        val localMemoListFlow = localMemoDataSource.fetchFilteredMemoByCategory(category)

        val filteredMemoListFlow = localMemoListFlow.flatMapConcat { localMemoList ->
            if (uid != null && localMemoList == null) {
                return@flatMapConcat remoteMemoDataSource.fetchAllMemo(uid)
            }

            return@flatMapConcat localMemoListFlow
        }

        return filteredMemoListFlow
    }

    @FlowPreview
    override fun fetchMemoById(uid: String?, id: String): Flow<Memo?> {
        val localMemoFlow = localMemoDataSource.fetchMemoById(id)

        val memoListFlow = localMemoFlow.flatMapConcat { localMemo ->
            if (uid != null && localMemo == null) {
                return@flatMapConcat remoteMemoDataSource.fetchMemoById(uid, id)
            }

            return@flatMapConcat localMemoFlow
        }

        return memoListFlow
    }

    override suspend fun deleteMemoById(uid: String?, id: String) {
        localMemoDataSource.deleteMemoById(id)
        uid ?: return

        remoteMemoDataSource.deleteMemoById(uid, id)
    }
}

@Module
@InstallIn(ActivityComponent::class)
abstract class MemoModule {

    @ActivityScoped
    @Binds
    abstract fun bindMemoRepository(defaultMemoRepository: DefaultMemoRepository): MemoRepository
}