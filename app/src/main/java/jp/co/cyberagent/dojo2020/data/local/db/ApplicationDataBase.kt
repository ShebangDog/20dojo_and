package jp.co.cyberagent.dojo2020.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.cyberagent.dojo2020.data.local.db.category.CategoryDao
import jp.co.cyberagent.dojo2020.data.local.db.category.CategoryEntity
import jp.co.cyberagent.dojo2020.data.local.db.converter.CategoryConverter
import jp.co.cyberagent.dojo2020.data.local.db.converter.ColorConverter
import jp.co.cyberagent.dojo2020.data.local.db.converter.ContentConverter
import jp.co.cyberagent.dojo2020.data.local.db.draft.DraftDao
import jp.co.cyberagent.dojo2020.data.local.db.draft.DraftEntity
import jp.co.cyberagent.dojo2020.data.local.db.memo.MemoDao
import jp.co.cyberagent.dojo2020.data.local.db.memo.MemoEntity
import javax.inject.Singleton

@TypeConverters(
    CategoryConverter::class,
    ContentConverter::class,
    ColorConverter::class
)
@Database(
    entities = [MemoEntity::class, DraftEntity::class, CategoryEntity::class],
    version = 1
)
abstract class ApplicationDataBase : RoomDatabase() {
    abstract fun memoDao(): MemoDao

    abstract fun draftDao(): DraftDao

    abstract fun categoryDao(): CategoryDao
}

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationDataBaseModule {

    @Singleton
    @Provides
    fun provideApplicationDataBase(
        @ApplicationContext applicationContext: Context
    ): ApplicationDataBase = Room.databaseBuilder(
        applicationContext,
        ApplicationDataBase::class.java,
        "ca-memo-database"
    ).build()

}