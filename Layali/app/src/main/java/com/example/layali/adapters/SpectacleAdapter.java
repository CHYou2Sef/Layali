package com.example.layali.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import com.example.layali.R;
import com.example.layali.models.Lieu;
import com.example.layali.models.Spectacle;
import com.example.layali.activities.SpectacleDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SpectacleAdapter extends RecyclerView.Adapter<SpectacleAdapter.SpectacleViewHolder> {

    private Context context;
    private List<Spectacle> spectacleList;
    private static final String TAG = "SpectacleAdapter";


    public SpectacleAdapter(Context context, List<Spectacle> spectacleList) {
        this.context = context;
        this.spectacleList = spectacleList;
    }

    @NonNull
    @Override
    public SpectacleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_spectacle, parent, false);
        return new SpectacleViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SpectacleViewHolder holder, int position) {
        Spectacle spectacle = spectacleList.get(position);

        // Vérification null de base
        if (spectacle == null) {
            Log.e(TAG, "Spectacle at position " + position + " is null");
            return;
        }

        // Log data for debugging
        logSpectacleDetails(spectacle);

        // Chargement de l'image avec Picasso (changé de Glide)
        loadSpectacleImage(spectacle, holder.ivSpectacle);

        // Set basic text information with null checks
        holder.tvTitle.setText(getValueOrDefault(spectacle.getTitre(), "Sans titre"));
        holder.tvDate.setText(getValueOrDefault(spectacle.getDateS(), "Date non spécifiée"));

        // Format and set time
        String formattedTime = spectacle.getH_DebutS() != null ?
                formatTime(spectacle.getH_DebutS()) : "Heure non spécifiée";
        holder.tvTime.setText(formattedTime);

        Log.d(TAG, "Here is Lieu ID: " + spectacle.getIdLieu());
        fetchLieuDetails(spectacle.getIdLieu(), holder.tvLocation);

        // Handle location display with thorough null checks
       // setLocationText(holder.tvLocation, spectacle);

        // Ensure spectacleId is valid
        String spectacleId = ensureValidId(spectacle);

        // Set click listeners with the validated ID
        setClickListeners(holder, spectacle, spectacleId);
    }

    private void logSpectacleDetails(Spectacle spectacle) {
        Log.d(TAG, "Spectacle: " + spectacle);
        Log.d(TAG, "ID: " + spectacle.getIdSpec());
        Log.d(TAG, "Titre: " + spectacle.getTitre());
        Log.d(TAG, "Date: " + spectacle.getDateS());
        Log.d(TAG, "Heure: " + spectacle.getH_DebutS());
        Log.d(TAG, "Prix: " + spectacle.getPrix());
        Log.d(TAG, "Durée: " + spectacle.getDuree());
        Log.d(TAG, "Nombre de Spectateur: " + spectacle.getNbrSpectateur());
        Log.d(TAG, "Description: " + spectacle.getDescription());
        Log.d(TAG, "Site Web: " + spectacle.getSite_web());


        if (spectacle.getLieu() != null) {
            Log.d(TAG, "Lieu: " + spectacle.getLieu());
        } else {
            Log.d(TAG, "Lieu: null");
        }
    }

    private void loadSpectacleImage(Spectacle spectacle, ImageView imageView) {
        if (spectacle.getimageUrl() != null && !spectacle.getimageUrl().isEmpty()) {
            try {
                // Utiliser Picasso au lieu de Glide
                Picasso.get()
                        .load(spectacle.getimageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(imageView);
                Log.d(TAG, "Loading image from URL: " + spectacle.getimageUrl());
            } catch (Exception e) {
                Log.e(TAG, "Error loading image with Picasso", e);
                imageView.setImageResource(R.drawable.error_image);
            }
        } else {
            Log.d(TAG, "Image URL is null or empty, using default image");
            imageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void setLocationText(TextView locationTextView, Spectacle spectacle) {
        if (spectacle.getLieu() != null) {
            Lieu lieu = spectacle.getLieu();
            StringBuilder locationBuilder = new StringBuilder();

            // Add lieu name if available
            if (lieu.getNomLieu() != null && !lieu.getNomLieu().isEmpty()) {
                locationBuilder.append(lieu.getNomLieu());
            } else {
                locationBuilder.append("Lieu non nommé");
            }

            // Add city if available
            if (lieu.getVille() != null && !lieu.getVille().isEmpty()) {
                locationBuilder.append(" - ").append(lieu.getVille());
            }

            // Add address on new line if available
            if (lieu.getAdresse() != null && !lieu.getAdresse().isEmpty()) {
                locationBuilder.append("\n").append(lieu.getAdresse());
            }

            locationTextView.setText(locationBuilder.toString());

            // If we have an ID but missing details, try to fetch from Firestore
            if (lieu.getIdLieu() != null && !lieu.getIdLieu().isEmpty() &&
                    (lieu.getNomLieu() == null || lieu.getVille() == null || lieu.getAdresse() == null)) {
                fetchLieuDetails(lieu.getIdLieu(), locationTextView);
            }
        } else {
            locationTextView.setText("Lieu non spécifié");
        }
    }

    private String ensureValidId(Spectacle spectacle) {
        String spectacleId = spectacle.getIdSpec();
        if (spectacleId == null || spectacleId.isEmpty()) {
            // Generate temporary ID if needed
            spectacleId = "temp_" + spectacle.hashCode();
            spectacle.setIdSpec(spectacleId);
            Log.w(TAG, "Created temporary ID for spectacle: " + spectacleId);
        }
        return spectacleId;
    }

    private void setClickListeners(SpectacleViewHolder holder, Spectacle spectacle, String spectacleId) {
        // Item click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SpectacleDetailActivity.class);
            intent.putExtra("spectacleId", spectacleId);
            context.startActivity(intent);
        });

        // Button click listener
        holder.btnVoirPlus.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SpectacleDetailActivity.class);
            intent.putExtra("spectacleId", spectacleId);
            v.getContext().startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return spectacleList.size();
    }

    private String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    private String formatTime(String rawTime) {
        if (rawTime == null || rawTime.isEmpty()) {
            return "Heure non spécifiée";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH'h'mm", Locale.getDefault());
            Date time = inputFormat.parse(rawTime);
            return time != null ? outputFormat.format(time) : rawTime;
        } catch (Exception e) {
            Log.e(TAG, "Erreur de format d'heure: " + rawTime, e);
            return rawTime;
        }
    }

    private String formatFirestoreDate(Timestamp timestamp) {
        if (timestamp == null) return "Date inconnue";

        try {
            // Conversion correcte pour Firestore Timestamp
            Date date = new Date(timestamp.getSeconds() * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du formatage de la date Firestore", e);
            return "Date inconnue";
        }
    }

    private void fetchLieuDetails(String lieuId, TextView textView) {
        if (lieuId == null || lieuId.isEmpty()) {
            textView.setText("Lieu non spécifié");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("lieux")
                .document(lieuId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Lieu lieu = documentSnapshot.toObject(Lieu.class);
                        if (lieu != null) {
                            StringBuilder locationBuilder = new StringBuilder();

                            if (lieu.getNomLieu() != null && !lieu.getNomLieu().isEmpty()) {
                                locationBuilder.append(lieu.getNomLieu());
                            } else {
                                locationBuilder.append("Lieu non nommé");
                            }

                            if (lieu.getVille() != null && !lieu.getVille().isEmpty()) {
                                locationBuilder.append(" - ").append(lieu.getVille());
                            }

                            if (lieu.getAdresse() != null && !lieu.getAdresse().isEmpty()) {
                                locationBuilder.append("\n").append(lieu.getAdresse());
                            }

                            textView.setText(locationBuilder.toString());
                        } else {
                            textView.setText("Lieu non disponible");
                        }
                    } else {
                        textView.setText("Lieu introuvable");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors de la récupération du lieu", e);
                    textView.setText("Erreur de chargement du lieu");
                });
    }

    public static class SpectacleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvTime, tvLocation;
        ImageView ivSpectacle;
        Button btnVoirPlus;
        TextView tvPrix;
        TextView tvNbrSpectateur;

        public SpectacleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.titleTextView);
            tvDate = itemView.findViewById(R.id.dateTextView);
            tvTime = itemView.findViewById(R.id.timeTextView);
            tvLocation = itemView.findViewById(R.id.locationTextView);
            ivSpectacle = itemView.findViewById(R.id.spectacleImageView);
            btnVoirPlus = itemView.findViewById(R.id.btn_vp);

            btnVoirPlus.setOnClickListener(v -> {
                        Intent intent = new Intent(v.getContext(), SpectacleDetailActivity.class);
                        v.getContext().startActivity(intent);
                    });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), SpectacleDetailActivity.class);
                v.getContext().startActivity(intent);
            });
        }
    }

    // Méthode pour mettre à jour les données
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Spectacle> newSpectacles) {
        spectacleList = newSpectacles;
        notifyDataSetChanged();
    }
}

