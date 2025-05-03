package com.example.layali.utils;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static FirebaseFirestore getFirestore() {
        return db;
    }

    public static FirebaseStorage getStorage() {
        return storage;
    }

    public static CollectionReference getSpectaclesCollection() {
        return db.collection("spectacles");
    }

    public static CollectionReference getClientsCollection() {
        return db.collection("clients");
    }

    public static CollectionReference getArtistesCollection() {
        return db.collection("artistes");
    }

    public static CollectionReference getReservationsCollection() {
        return db.collection("reservations");
    }

    public static void log(String message) {
        Log.d(TAG, message);
    }
}

