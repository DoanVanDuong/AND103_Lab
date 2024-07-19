package com.example.asm_ph46500;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    ImageButton btnBack;
    Button btnSignUp;
    private FirebaseAuth mAuth;
    EditText edtEmail, edtPass;
    String email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnBack = findViewById(R.id.imgBtnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        edtEmail = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        mAuth = FirebaseAuth.getInstance();
        btnSignUp=findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v -> {
            email = edtEmail.getText().toString();
            pass = edtPass.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(getApplicationContext(), user.getEmail(), Toast.LENGTH_LONG).show();
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}