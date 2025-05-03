package com.example.layali.activities;

import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.layali.MainActivity;
import com.example.layali.R;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.layali.fragments.cart.CartFragment;
import com.example.layali.models.Lieu;
import com.example.layali.models.Spectacle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.UserData;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SpectacleDetailActivity extends AppCompatActivity {
    private static final String TAG = "SpectacleAdapter";

    private TextView title, description, date, heure, lieu, artistes, nbspec ,duree,prix, totalPrice;
    private ImageView image;
    private Spinner typeBilletSpinner;
    private Button addCartBtn , openlink , mapBtn ;
    private String spectacleId;
    private Spectacle currentSpectacle;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextInputEditText etQuantity ;
    private  ProgressDialog progressDialog;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectacle_detail);

        progressDialog = new ProgressDialog(this);
        title = findViewById(R.id.tv_show_title);
        description = findViewById(R.id.tv_show_description);
        date = findViewById(R.id.detail_date);
        heure = findViewById(R.id.detail_hdebut);
        duree = findViewById(R.id.detail_duree);
        lieu = findViewById(R.id.tv_show_location);
        nbspec = findViewById(R.id.detail_nbrSpectateur);
        artistes = findViewById(R.id.tv_show_artists);
        image = findViewById(R.id.iv_show);
        typeBilletSpinner = findViewById(R.id.spinner_type_billet);
      //  quantityInput = findViewById(R.id.tv_quantite);
        addCartBtn = findViewById(R.id.btn_reserve);
        openlink = findViewById(R.id.btn_open_website);
        artistes = findViewById(R.id.tv_show_artists);
        prix = findViewById(R.id.detail_prix);
        totalPrice = findViewById(R.id.tv_total_price);
       // rubrique = findViewById(R.id.tv_quantite);
        Button btnDecrement = findViewById(R.id.btn_decrement);
        Button btnIncrement = findViewById(R.id.btn_increment);
        etQuantity = findViewById(R.id.tv_quantite);
        mapBtn = findViewById(R.id.btn_map);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Récupérer l'ID envoyé
        spectacleId = getIntent().getStringExtra("spectacleId");

        if (spectacleId == null) {
            Toast.makeText(this, "Erreur: ID spectacle introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSpectacleDetails(spectacleId);

        loadRubriquesEtArtistes(spectacleId);

        btnDecrement.setOnClickListener(v -> adjustQuantity(-1));
        btnIncrement.setOnClickListener(v -> adjustQuantity(1));

        addCartBtn.setOnClickListener(v -> addToCart());
        openlink.setOnClickListener(v -> {
            if (currentSpectacle != null && currentSpectacle.getSite_web() != null) {
                String url = currentSpectacle.getSite_web();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Site web introuvable", Toast.LENGTH_SHORT).show();
            }
        });

        mapBtn.setOnClickListener(v->
        {
            // 1. Récupérer les coordonnées du lieu depuis Firestore
            FirebaseFirestore.getInstance()
                    .collection("lieux")
                    .document(currentSpectacle.getIdLieu())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Lieu lieu = documentSnapshot.toObject(Lieu.class);
                            if (lieu != null && lieu.getLocation() != null) {
                                // 2. Préparer l'URI pour Google Maps
                                double latitude = lieu.getLocation().getLatitude();
                                double longitude = lieu.getLocation().getLongitude();

                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)",
                                        latitude,
                                        longitude,
                                        latitude,
                                        longitude,
                                        Uri.encode(currentSpectacle.getTitre()));

                                // 3. Créer l'intention
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                mapIntent.setPackage("com.google.android.apps.maps");

                                // 4. Vérifier si Google Maps est installé
                                    // Fallback : ouvrir dans navigateur web
                                    String webUrl = String.format(Locale.ENGLISH,
                                            "https://www.google.com/maps/search/?api=1&query=%f,%f",
                                            latitude, longitude);
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));

                            } else {
                                Toast.makeText(this, "Localisation non disponible", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur de chargement de la localisation", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void loadSpectacleDetails(String spectacleId) {
        FirebaseFirestore.getInstance().collection("spectacles").document(spectacleId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentSpectacle = documentSnapshot.toObject(Spectacle.class);

                        if (currentSpectacle != null) {
                            Log.d(TAG, "Spectacle: " + currentSpectacle);
                            Log.d(TAG, "ID: " + currentSpectacle.getIdSpec());
                            Log.d(TAG, "Titre: " + currentSpectacle.getTitre());
                            Log.d(TAG, "Date: " + currentSpectacle.getDateS());
                            Log.d(TAG, "Heure: " + currentSpectacle.getH_DebutS());
                            Log.d(TAG, "Prix: " + currentSpectacle.getPrix());
                            Log.d(TAG, "Durée: " + currentSpectacle.getDuree());
                            Log.d(TAG, "Nombre de Spectateur: " + currentSpectacle.getNbrSpectateur());
                            Log.d(TAG, "Description: " + currentSpectacle.getDescription());
                            Log.d(TAG, "Site Web: " + currentSpectacle.getSite_web());

                            title.setText(currentSpectacle.getTitre());
                            description.setText(currentSpectacle.getDescription());
                            date.setText(currentSpectacle.getDateS());
                            heure.setText(currentSpectacle.getH_DebutS());
                            nbspec.setText(currentSpectacle.getNbrSpectateur());
                            duree.setText(currentSpectacle.getDuree());
                            prix.setText(valueOf(currentSpectacle.getPrix()) + " TND");
                            //artistes.setText(currentSpectacle.getArtiste());

                            if (currentSpectacle.getimageUrl() != null && !currentSpectacle.getimageUrl().isEmpty()) {
                                Glide.with(this)
                                        .load(currentSpectacle.getimageUrl())
                                        .placeholder(R.drawable.placeholder_image)
                                        .into(image);
                            }

                            String ll = currentSpectacle.getIdLieu();
                            loadLieuDetails(ll);



                           // lieu.setText(ll.getNomLieu() + " - " + ll.getVille());

                           // Glide.with(this).load(currentSpectacle.getImage()).into(image);
                            /*
                            // Chargement d'image depuis URL
                                Glide.with(context)
                                    .load("https://example.com/image.jpg")
                                    .into(imageView);

                                // Chargement depuis ressources locales
                                Glide.with(context)
                                    .load(R.drawable.local_image)
                                    .into(imageView);
                             */

                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                    R.array.billet_types, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            typeBilletSpinner.setAdapter(adapter);
                        }
                        else {
                            Toast.makeText(this, "Spectacle introuvable", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(this, "Spectacle introuvable", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadLieuDetails(String lieuId) {
        if (lieuId == null || lieuId.isEmpty()) return;

        db.collection("lieux")
                .document(lieuId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Lieu lieu2 = doc.toObject(Lieu.class);
                        if (lieu2 != null) {
                            String full = lieu2.getNomLieu() + ", " + lieu2.getVille();
                            lieu.setText(full);
                        }
                    }
                });
    }

    private void loadRubriquesEtArtistes(String spectacleId) {
        db.collection("spectacles").document(spectacleId).collection("rubriques")
                .get()
                .addOnSuccessListener(query -> {
                    StringBuilder details = new StringBuilder();
                    for (DocumentSnapshot rub : query) {
                        String type = rub.getString("typeR");
                        String heure = rub.getString("H_DebutR");
                        String duree = rub.getString("dureeRub");
                        String idArt = rub.getString("idArt");

                        details.append("🎭 ").append(type).append(" à ").append(heure).append(" (").append(duree).append(")\n");

                        // 👤 Chercher l'artiste correspondant
                        db.collection("artistes").document(idArt).get()
                                .addOnSuccessListener(art -> {
                                    if (art.exists()) {
                                        String nom = art.getString("prenomArt") + " " + art.getString("nomArt");
                                        String specialite = art.getString("specialite");
                                        details.append("  👤 ").append(nom).append(" [").append(specialite).append("]\n");
                                    }
                                    artistes.setText(details.toString());
                                });
                    }
                });
    }


    private void addToCart() {
        String typeSelected = typeBilletSpinner.getSelectedItem().toString();
        String quantityTxt = Objects.requireNonNull(etQuantity.getText()).toString();

        if (quantityTxt.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir la quantité", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityTxt);
        if (quantity <= 0) {
            Toast.makeText(this, "Quantité invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 récupérer prix
        Double prixUnitaire = (double) currentSpectacle.getPrixParType(typeSelected);
        if (prixUnitaire == null) {
            Toast.makeText(this, "Prix non disponible pour ce type", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = prixUnitaire * quantity;
        totalPrice.setText(total + " TND");

        // ✅ récupérer anonymousId depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("anonymousId", null);
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            prefs.edit().putString("anonymousId", userId).apply();
        }

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("spectacleId", spectacleId);
        cartData.put("titre", currentSpectacle.getTitre());
        cartData.put("typeBillet", typeSelected);
        cartData.put("quantite", quantity);
        cartData.put("prixUnitaire", prixUnitaire);
        cartData.put("total", total);
        cartData.put("imageUrl", currentSpectacle.getimageUrl());
        cartData.put("dateS", currentSpectacle.getDateS());

        // ✅ afficher loading
        progressDialog.setMessage("Ajout au panier...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("carts").document(userId).collection("items") // ✅ fix here
                .add(cartData)
                .addOnSuccessListener(docRef -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Ajouté au panier", Toast.LENGTH_SHORT).show();

                    // Retourner vers MainActivity pour afficher CartFragment
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("openCart", true);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void adjustQuantity(int delta) {
        int current = getCurrentQuantity();
        int newValue = Math.max(1, Math.min(10, current + delta)); // Limite 1-10
        etQuantity.setText(valueOf(newValue));
    }

    private int getCurrentQuantity() {
        try {
            return Integer.parseInt(etQuantity.getText().toString());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

}
