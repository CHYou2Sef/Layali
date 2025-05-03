package com.example.layali.fragments.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.layali.R;
import com.example.layali.activities.LoginActivity;
import com.example.layali.models.Client;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private TextView nameTextView, emailTextView, passwordTextView;
    private Button logoutBtn, settingsButton, editProfileButton, languagebtn;
    private FirebaseAuth mAuth;

    private CircleImageView image ;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Référencer les vues
        nameTextView = view.findViewById(R.id.profileName);
        emailTextView = view.findViewById(R.id.profileEmail);
        passwordTextView = view.findViewById(R.id.profile_password);
        settingsButton = view.findViewById(R.id.settingsButton);

        image = view.findViewById(R.id.profileImage);

        editProfileButton = view.findViewById(R.id.editProfileButton);
        languagebtn = view.findViewById(R.id.languageButton);
        logoutBtn = view.findViewById(R.id.logoutButton);


        languagebtn.setOnClickListener(v -> showLanguageDialog());

        // Charger les données de l'utilisateur
        loadUserData();

        // Configurer le bouton de déconnexion
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            // Rediriger vers l'écran de login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
        //return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            if (currentUser != null) {
                nameTextView.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Nom inconnu");
                emailTextView.setText(currentUser.getEmail());
                passwordTextView.setText("********"); // (par sécurité, tu n’as pas accès au mot de passe)

                // Si tu veux afficher la photo
                if (currentUser.getPhotoUrl() != null) {
                    Picasso.get().load(currentUser.getPhotoUrl()).into(image);
                }
            } else {
                Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            }

            // Récupérer les données de l'utilisateur depuis Firestore
            db.collection("clients")
                    .whereEqualTo("email", currentUser.getEmail())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Client user = documentSnapshot.toObject(Client.class);
                            if (user != null) {
                                nameTextView.setText(user.getnomClt());
                                emailTextView.setText(user.getEmail());
                                passwordTextView.setText(user.getPassword());
                            }
                        } else {
                            Toast.makeText(getContext(), "Aucun profil trouvé pour cet utilisateur.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } else {
            // L'utilisateur n'est pas connecté, rediriger vers l'écran de login avec startActivityForResult
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(intent, 1001);

                // Si nous sommes dans un fragment, nous devons le retirer
                // puisque l'utilisateur n'est pas autorisé à le voir
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(this)
                        .commit();
            }
        }
    }

    private void showLanguageDialog() {
        final String[] languages = {"Français", "English", "العربية"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.choose_language));
        builder.setSingleChoiceItems(languages, -1, (dialog, which) -> {
            switch (which) {
                case 0: setLocale("fr"); break;
                case 1: setLocale("en"); break;
                case 2: setLocale("ar"); break;
            }
            dialog.dismiss();
        });
        builder.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());

        // Sauvegarder la préférence
        SharedPreferences.Editor editor = requireContext()
                .getSharedPreferences("Settings", Context.MODE_PRIVATE)
                .edit();
        editor.putString("My_Lang", lang);
        editor.apply();

        // Redémarrer l'activité
        requireActivity().recreate();
    }

    /*
    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Récupérer les données de l'utilisateur depuis Firestore
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Client user = documentSnapshot.toObject(Client.class);
                            if (user != null) {
                                // Afficher les données dans les TextView
                                nameTextView.setText(user.getnomClt());
                                emailTextView.setText(user.getEmail());
                                passwordTextView.setText(user.getPassword());
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erreur lors du chargement des données: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            // L'utilisateur n'est pas connecté, rediriger vers l'écran de login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

     */

    @Override
    public void onResume() {
        super.onResume();
        // Recharger les données à chaque fois que le fragment devient visible
        loadUserData();
    }


    /*

    private void setupProfileData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Set email
            binding.profileEmail.setText(currentUser.getEmail());

            // Load user profile data from Firebase
            mDatabase.child("users").child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue(String.class);
                                String photoUrl = snapshot.child("photoUrl").getValue(String.class);

                                binding.profileName.setText(name != null ? name : "User");

                                // Load profile image using Glide
                                if (photoUrl != null && !photoUrl.isEmpty()) {
                                    Glide.with(requireContext())
                                            .load(photoUrl)
                                            .placeholder(R.drawable.profile)
                                            .error(R.drawable.profile)
                                            .into(binding.profileImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setupClickListeners() {
        binding.editProfileButton.setOnClickListener(v -> {
            // TODO: Implement edit profile functionality
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show();
        });

        binding.settingsButton.setOnClickListener(v -> {
            // TODO: Implement settings functionality
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        binding.logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            // TODO: Navigate to login screen
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }

     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // binding = null;
    }
}