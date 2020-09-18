package jp.co.cyberagent.dojo2020

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jp.co.cyberagent.dojo2020.data.*
import jp.co.cyberagent.dojo2020.data.local.*
import jp.co.cyberagent.dojo2020.data.local.db.ApplicationDataBase
import jp.co.cyberagent.dojo2020.data.remote.firestore.category.DefaultFirestoreCategoryDataSource
import jp.co.cyberagent.dojo2020.data.remote.firestore.category.FirestoreCategoryDataSource
import jp.co.cyberagent.dojo2020.data.remote.firestore.memo.DefaultFirestoreMemoDataSource
import jp.co.cyberagent.dojo2020.data.remote.firestore.memo.FirestoreMemoDataSource
import jp.co.cyberagent.dojo2020.data.remote.firestore.profile.DefaultFirestoreProfileDataSource
import jp.co.cyberagent.dojo2020.data.remote.firestore.profile.FirestoreProfileDataSource

object DI {
    private var memoRepository: MemoRepository? = null
    private var draftRepository: DraftRepository? = null
    private var profileRepository: ProfileRepository? = null
    private var userInfoRepository: UserInfoRepository? = null
    private var categoryRepository: CategoryRepository? = null

    private var firestoreProfileDataSource: FirestoreProfileDataSource? = null
    private var profileDataSource: ProfileDataSource? = null

    private var memoDataSource: MemoDataSource? = null
    private var firestoreMemoDataSource: FirestoreMemoDataSource? = null

    private var draftDataSource: DraftDataSource? = null

    private var firebaseFireStore: FirebaseFirestore? = null

    private var categoryDataSource: CategoryDataSource? = null
    private var firestoreCategoryDataSource: FirestoreCategoryDataSource? = null

    private var applicationDataBase: ApplicationDataBase? = null

    fun injectDefaultCategoryRepository(context: Context): CategoryRepository {
        if (categoryRepository != null) return categoryRepository!!

        val localCategoryDataSource = injectCategoryDataSource(context)
        val remoteCategoryDataSource = injectFirestoreCategoryDataSource()

        categoryRepository = DefaultCategoryRepository(
            localCategoryDataSource,
            remoteCategoryDataSource
        )

        return categoryRepository!!
    }

    fun injectDefaultMemoRepository(context: Context): MemoRepository {
        if (memoRepository != null) return memoRepository!!

        val localDataSource = injectDefaultMemoDataSource(context)
        val remoteDataSource = injectDefaultFireStoreMemoDataSource()

        memoRepository = DefaultMemoRepository(localDataSource, remoteDataSource)

        return memoRepository!!
    }

    fun injectDefaultProfileRepository(context: Context): ProfileRepository {
        if (profileRepository != null) return profileRepository!!

        val profileDataSource = injectDefaultProfileDataSource(context)
        val fireStoreProfileDataSource = injectDefaultFireStoreProfileDataSource()

        profileRepository = DefaultProfileRepository(profileDataSource, fireStoreProfileDataSource)

        return profileRepository!!
    }

    fun injectDefaultDraftRepository(context: Context): DraftRepository {
        if (draftRepository != null) return draftRepository!!
        val dataSource = injectDefaultDraftDataSource(context)

        draftRepository = DefaultDraftRepository(dataSource)

        return draftRepository!!
    }

    fun injectDefaultUserInfoRepository(): UserInfoRepository {
        if (userInfoRepository != null) return userInfoRepository!!

        userInfoRepository = DefaultUserInfoRepository()

        return userInfoRepository!!
    }

    private fun injectDefaultFireStoreProfileDataSource(): FirestoreProfileDataSource {
        if (firestoreProfileDataSource != null) return firestoreProfileDataSource!!

        val fireStore = injectFireStore()
        firestoreProfileDataSource = DefaultFirestoreProfileDataSource()

        return firestoreProfileDataSource!!
    }

    private fun injectDefaultProfileDataSource(context: Context): ProfileDataSource {
        if (profileDataSource != null) return profileDataSource!!

        val database = injectDatabase(context)
        profileDataSource = DefaultProfileDataSource(database)

        return profileDataSource!!
    }

    private fun injectDefaultFireStoreMemoDataSource(): FirestoreMemoDataSource {
        if (firestoreMemoDataSource != null) return firestoreMemoDataSource!!

        val firestore = injectFireStore()
        firestoreMemoDataSource = DefaultFirestoreMemoDataSource()

        return firestoreMemoDataSource!!
    }

    private fun injectDefaultMemoDataSource(context: Context): MemoDataSource {
        if (memoDataSource != null) return memoDataSource!!

        val database = injectDatabase(context)
        memoDataSource = DefaultMemoDataSource(database)

        return memoDataSource!!
    }

    private fun injectDefaultDraftDataSource(context: Context): DraftDataSource {
        if (draftDataSource != null) return draftDataSource!!

        val dataBase = injectDatabase(context)
        draftDataSource = DefaultDraftDataSource(dataBase)

        return draftDataSource!!
    }

    private fun injectFireStore(): FirebaseFirestore {
        if (firebaseFireStore != null) return firebaseFireStore!!

        firebaseFireStore = Firebase.firestore

        return firebaseFireStore!!
    }

    private fun injectCategoryDataSource(context: Context): CategoryDataSource {
        if (categoryDataSource != null) return categoryDataSource!!

        val database = injectDatabase(context)
        categoryDataSource = DefaultCategoryDataSource(database)

        return categoryDataSource!!
    }

    private fun injectFirestoreCategoryDataSource(): FirestoreCategoryDataSource {
        if (firestoreCategoryDataSource != null) return firestoreCategoryDataSource!!

        firestoreCategoryDataSource = DefaultFirestoreCategoryDataSource()

        return firestoreCategoryDataSource!!
    }

    private fun injectDatabase(context: Context): ApplicationDataBase {
        if (applicationDataBase != null) return applicationDataBase!!

        applicationDataBase = Room.databaseBuilder(
            context,
            ApplicationDataBase::class.java,
            "ca-memo-database"
        ).build()

        return applicationDataBase!!
    }
}