package com.example.iapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
     EditText emailEditText, passwordEditText;
     Button registerButton;
     FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validación de campos vacíos
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validación de formato de correo electrónico
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Correo inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validación de longitud mínima de la contraseña
                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Registro de usuario
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(RegisterActivity.this, "Registro exitoso: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                                // Redirigir a la pantalla principal
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Finalizar esta actividad para que no se pueda volver con el botón "Atrás"
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                                Log.w("RegisterActivity", "Error: " + errorMessage);
                                Toast.makeText(RegisterActivity.this, "Error en el registro: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
