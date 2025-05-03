package com.example.layali.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layali.models.Reservation;
import com.example.layali.models.Client;
import com.example.layali.MainActivity;

import com.example.layali.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String spectacleId, titre, typeBillet, imageUrl;
    private int quantite;
    private double total;

    private TextView summaryView;
    private Button confirmPaymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        summaryView = findViewById(R.id.paymentSummary);
        confirmPaymentBtn = findViewById(R.id.confirmPaymentBtn);

        // Vérifier connexion
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Vous devez vous connecter pour payer", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Récupérer extras
        spectacleId = getIntent().getStringExtra("spectacleId");
        titre = getIntent().getStringExtra("titre");
        typeBillet = getIntent().getStringExtra("typeBillet");
        quantite = getIntent().getIntExtra("quantite", 1);
        total = getIntent().getDoubleExtra("total", 0);
        imageUrl = getIntent().getStringExtra("imageUrl");

        summaryView.setText("Spectacle: " + titre + "\nBillet: " + typeBillet + "\nQuantité: " + quantite + "\nTotal: " + total + " TND");

        confirmPaymentBtn.setOnClickListener(v -> {
            String userId = auth.getCurrentUser().getUid();

            Map<String, Object> reservationData = new HashMap<>();
            reservationData.put("spectacleId", spectacleId);
            reservationData.put("titre", titre);
            reservationData.put("typeBillet", typeBillet);
            reservationData.put("quantite", quantite);
            reservationData.put("total", total);
            reservationData.put("imageUrl", imageUrl);

            // Enregistrer dans /reservations
            db.collection("reservations").document(userId).collection("items")
                    .add(reservationData);

            // Enregistrer dans /history
            db.collection("history").document(userId).collection("items")
                    .add(reservationData)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Paiement réussi !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("openHistory", true);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
