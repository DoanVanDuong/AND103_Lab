package com.example.asm_ph46500;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    Button btnSignUp, btnLogin;
    private FirebaseAuth mAuth;
    EditText edtEmail, edtPass;
    String email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSignUp = findViewById(R.id.btnSignUp1);
        btnLogin = findViewById(R.id.btnLoginEmail);
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
        edtEmail = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        mAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(v -> {
            email = edtEmail.getText().toString();
            pass = edtPass.getText().toString();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Login thành công", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Login thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}