package jp.co.cyberagent.dojo2020.data.remote.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

fun FirebaseFirestore.memosRef(uid: String): CollectionReference {
    return this
        .collection(FirestoreConstants.USERS)
        .document(uid)
        .collection(FirestoreConstants.MEMOS)
}

fun FirebaseFirestore.profileRef(uid: String): DocumentReference {
    return this
        .collection(FirestoreConstants.USERS)
        .document(uid)
        .collection(FirestoreConstants.PROFILE)
        .document(FirestoreConstants.YOUR)
}

fun FirebaseFirestore.categoriesRef(uid: String): CollectionReference {
    return this
        .collection(FirestoreConstants.USERS)
        .document(uid)
        .collection(FirestoreConstants.CATEGORY)
}

fun CollectionReference.document(id: Int): DocumentReference {
    return this.document(id.toString())
}