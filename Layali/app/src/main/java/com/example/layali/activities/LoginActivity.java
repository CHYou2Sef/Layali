package com.example.layali.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layali.R;
import com.example.layali.models.Client;
import com.google.firebase.auth.FirebaseAuth;
import com.example.layali.MainActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


    public class LoginActivity extends AppCompatActivity {
        private EditText email, password;
        private Button loginBtn, registerRedirect;
        private FirebaseAuth mAuth;
        private FirebaseFirestore db;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            email = findViewById(R.id.email);
            password = findViewById(R.id.password);
            loginBtn = findViewById(R.id.loginBtn);
            registerRedirect = findViewById(R.id.registerBtn);
            mAuth = FirebaseAuth.getInstance();

            loginBtn.setOnClickListener(v -> loginUser());
            registerRedirect.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            });
        }

        private void loginUser() {
            String emailTxt = email.getText().toString();
            String passTxt = password.getText().toString();

            if (emailTxt.isEmpty() || passTxt.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(emailTxt, passTxt)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        // Dans LoginActivity.java, modifiez la partie de redirection après login réussi
        private void saveUserToFirestore(String name, String email, String password) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                // Créer un nouvel objet utilisateur - sans stocker le mot de passe
                Client user = new Client( name, email);

                // Sauvegarder dans Firestore
                db.collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener(aVoid -> {
                            // Vérifier si nous avons été lancés par MainActivity
                            if (getCallingActivity() != null) {
                                // Retourner un résultat à MainActivity
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                // Lancement normal, rediriger vers l'activité principale
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(LoginActivity.this, "Erreur lors de l'enregistrement des données: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        }


}