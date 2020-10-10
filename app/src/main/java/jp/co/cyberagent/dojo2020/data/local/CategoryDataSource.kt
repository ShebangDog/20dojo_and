package jp.co.cyberagent.dojo2020.data.local

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import jp.co.cyberagent.dojo2020.data.local.db.ApplicationDataBase
import jp.co.cyberagent.dojo2020.data.local.db.category.CategoryEntity
import jp.co.cyberagent.dojo2020.data.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface CategoryDataSource {
    suspend fun addCategory(category: Category)

    fun fetchCategory(): Flow<List<Category>>

    suspend fun deleteCategory(category: Category)
}

@Singleton
class DefaultCategoryDataSource @Inject constructor(
    private val database: ApplicationDataBase
) : CategoryDataSource {

    override suspend fun addCategory(category: Category) {
        database.categoryDao().insert(category.toEntity())
    }

    override fun fetchCategory(): Flow<List<Category>> {
        return database.categoryDao().fetchAll().map { categoryList ->
            categoryList.map { it.toModel() }
        }
    }

    override suspend fun deleteCategory(category: Category) {
        database.categoryDao().delete(category.name)
    }

    private fun Category.toEntity(): CategoryEntity {
        return CategoryEntity(name, color)
    }
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class CategoryDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindCategoryDataSource(defaultCategoryDataSource: DefaultCategoryDataSource): CategoryDataSource
}