package jp.co.cyberagent.dojo2020

import android.content.Context
import androidx.room.Room
import jp.co.cyberagent.dojo2020.data.MemoDataSource
import jp.co.cyberagent.dojo2020.data.DefaultMemoRepository
import jp.co.cyberagent.dojo2020.data.MemoRepository
import jp.co.cyberagent.dojo2020.data.local.LocalMemoDataSource
import jp.co.cyberagent.dojo2020.data.local.db.memo.MemoDataBase
import jp.co.cyberagent.dojo2020.data.remote.firestore.DefaultFireStoreDataSource
import jp.co.cyberagent.dojo2020.data.remote.firestore.FireStoreDataSource

object DI {
    fun injectRepository(context: Context): MemoRepository {
        val localDataSource = injectLocalDataSource(context)
        val remoteDataSource = injectRemoteDataSource()

        return DefaultMemoRepository(localDataSource, remoteDataSource)
    }

    private fun injectLocalDataSource(context: Context): MemoDataSource {
        val database = injectDatabase(context)

        return LocalMemoDataSource(database)
    }

    private fun injectRemoteDataSource(): FireStoreDataSource {
        return DefaultFireStoreDataSource()
    }

    private fun injectDatabase(context: Context): MemoDataBase {
        val database = Room.databaseBuilder(
            context,
            MemoDataBase::class.java,
            "ca-memo-database"
        ).build()

        return database
    }
}