package com.example.layali.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import com.example.layali.R;
import com.google.firebase.auth.FirebaseAuth;
import com.example.layali.MainActivity;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class RegisterActivity extends AppCompatActivity {
        private EditText email, password, name, tel, passwordConf,  prenom ;
        private Button registerBtn, loginRedirect;
        private FirebaseAuth mAuth;
        private ProgressBar progressBar;

        @Override
        public void onStart() {
            super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            // Initialisation Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            email = findViewById(R.id.email);
            password = findViewById(R.id.password);
            name = findViewById(R.id.nom);
            tel = findViewById(R.id.tel);
            passwordConf = findViewById(R.id.conf_password);
            prenom = findViewById(R.id.prenom);
            registerBtn = findViewById(R.id.registerBtn);
            loginRedirect = findViewById(R.id.loginBtn);
            progressBar = findViewById(R.id.FprogressBar);

            mAuth = FirebaseAuth.getInstance();

            registerBtn.setOnClickListener(v -> registerUser());
            loginRedirect.setOnClickListener(v -> {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            });
        }

        private void registerUser() {
            String emailTxt = email.getText().toString();
            String passTxt = password.getText().toString();
            String nameTxt = name.getText().toString();
            String telTxt = tel.getText().toString();
            String passwordConfTxt = passwordConf.getText().toString();
            String prenomTxt = prenom.getText().toString();


            if (TextUtils.isEmpty(emailTxt)) {
                email.setError("Email requis !");
                return;
            }
            if (TextUtils.isEmpty(passTxt)) {
                password.setError("Mot de passe requis !");
                return;
            }
            if (passTxt.length() < 6) {
                password.setError("Le mot de passe doit contenir au moins 6 caractères !");
                return;
            }
            if (!passTxt.equals(passwordConfTxt)) {
                password.setError("Les mots de passe ne correspondent pas !");
                return;
            }
            if (TextUtils.isEmpty(nameTxt)) {
                name.setError("Nom requis !");
                return;
            }
            if (TextUtils.isEmpty(telTxt)) {
                tel.setError("Tel requis !");
                return;
            }
            if (TextUtils.isEmpty(prenomTxt)) {
                prenom.setError("Prenom requis !");
                return;
            }
            if (TextUtils.isEmpty(passwordConfTxt)) {
                passwordConf.setError("Confirmation du mot de passe requis !");
                return;
            }

          //  progressBar.setVisibility(View.VISIBLE);

            if (emailTxt.isEmpty() || passTxt.isEmpty() || nameTxt.isEmpty() || telTxt.isEmpty() || passwordConfTxt.isEmpty() || prenomTxt.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(emailTxt, passTxt)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(RegisterActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Log.e("FirebaseAuth", "Erreur d'inscription", task.getException());
                                Toast.makeText(this, "Échec de l'inscription", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
