package jp.co.cyberagent.dojo2020.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import jp.co.cyberagent.dojo2020.data.local.DraftDataSource
import jp.co.cyberagent.dojo2020.data.model.Draft
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DraftRepository {
    suspend fun saveDraft(draft: Draft)

    fun fetchAllDraft(): Flow<List<Draft>>

    fun fetchFilteredDraftsByCategory(category: String): Flow<List<Draft>?>

    fun fetchDraftById(id: String): Flow<Draft?>

    suspend fun deleteDraftById(id: String)
}

@ActivityScoped
class DefaultDraftRepository @Inject constructor(
    private val draftDataSource: DraftDataSource
) : DraftRepository {

    override suspend fun saveDraft(draft: Draft) {
        draftDataSource.saveDraft(draft)
    }

    override fun fetchAllDraft(): Flow<List<Draft>> {
        return draftDataSource.fetchAllDraft()
    }

    override fun fetchFilteredDraftsByCategory(category: String): Flow<List<Draft>?> {
        return draftDataSource.fetchFilteredDraftsByCategory(category)
    }

    override fun fetchDraftById(id: String): Flow<Draft?> {
        return draftDataSource.fetchDraftById(id)
    }

    override suspend fun deleteDraftById(id: String) {
        return draftDataSource.deleteDraftById(id)
    }
}

@Module
@InstallIn(ActivityComponent::class)
abstract class DraftRepositoryModule {

    @ActivityScoped
    @Binds
    abstract fun bindDraftRepository(defaultDraftRepository: DefaultDraftRepository): DraftRepository
}