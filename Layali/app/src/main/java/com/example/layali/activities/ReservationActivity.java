package com.example.layali.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layali.MainActivity;
import com.example.layali.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

    public class ReservationActivity extends AppCompatActivity {

        private TextView titreView, typeBilletView, quantiteView, totalView;
        private ImageView spectacleImage;
        private Button payerButton;
        private String spectacleId, titre, typeBillet, imageUrl;
        private int quantite;
        private double total;
        private FirebaseAuth auth;
        private FirebaseFirestore db;
        private String userId;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reservation);

            titreView = findViewById(R.id.reservationTitre);
            typeBilletView = findViewById(R.id.reservationTypeBillet);
            quantiteView = findViewById(R.id.reservationQuantite);
            totalView = findViewById(R.id.reservationTotal);
            spectacleImage = findViewById(R.id.reservationImage);
            payerButton = findViewById(R.id.payerButton);

            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            // 🔥 Récupérer les extras
            spectacleId = getIntent().getStringExtra("spectacleId");
            titre = getIntent().getStringExtra("titre");
            typeBillet = getIntent().getStringExtra("typeBillet");
            quantite = getIntent().getIntExtra("quantite", 1);
            total = getIntent().getDoubleExtra("total", 0);
            imageUrl = getIntent().getStringExtra("imageUrl");

            titreView.setText(titre);
            typeBilletView.setText("Billet: " + typeBillet);
            quantiteView.setText("Quantité: " + quantite);
            totalView.setText(String.format("Total: %.2f TND", total));

            Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder_image).into(spectacleImage);

            payerButton.setOnClickListener(v -> {
                if (auth.getCurrentUser() == null) {
                    // 🔐 Exiger login AVANT paiement
                    Toast.makeText(this, "Veuillez vous connecter avant de payer.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    return;
                }

                userId = auth.getCurrentUser().getUid();

                Map<String, Object> reservationData = new HashMap<>();
                reservationData.put("spectacleId", spectacleId);
                reservationData.put("titre", titre);
                reservationData.put("typeBillet", typeBillet);
                reservationData.put("quantite", quantite);
                reservationData.put("imageUrl", imageUrl);
                reservationData.put("total", total);

                // ✅ Sauvegarder dans /reservations/{userId}
                db.collection("reservations").document(userId)
                        .collection("items")
                        .add(reservationData);

                // ✅ Aussi dans /history/{userId}
                db.collection("history").document(userId)
                        .collection("items")
                        .add(reservationData)
                        .addOnSuccessListener(docRef -> {
                            Toast.makeText(this, "Réservation confirmée !", Toast.LENGTH_SHORT).show();
                            // 🔥 Redirige vers historique
                            Intent i = new Intent(this, MainActivity.class);
                            i.putExtra("openHistory", true);
                            startActivity(i);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }

