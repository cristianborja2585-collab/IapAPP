package com.example.iapapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView registerTextView;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        mAuth = FirebaseAuth.getInstance();

        // Verificar si el TextView de registro se inicializa correctamente
        if (registerTextView != null) {
            Log.d("LoginActivity", "registerTextView está inicializado");
        } else {
            Log.d("LoginActivity", "registerTextView es nulo");
        }

        // Botón para iniciar sesión
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null /*&& user.isEmailVerified()*/) {
                                Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, DatosActivity.class); // Redirige a DatosActivity
                                startActivity(intent);
                                finish(); // Cierra la pantalla de login
                            }
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                            Log.w("LoginActivity", "Error: " + errorMessage);
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesión: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Botón para redirigir al registro
        registerTextView.setOnClickListener(v -> {
            Log.d("LoginActivity", "Texto de registro clickeado");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
