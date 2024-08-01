package com.example.asm_ph46500.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.asm_ph46500.R;
import com.example.asm_ph46500.model.User;
import com.example.asm_ph46500.services.ApiServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private ImageView avatarImageView;
    private Uri avatarUri;
    TextView tvLogin;
    private TextInputEditText usernameEditText, passwordEditText, emailEditText, nameEditText;
    private Button registerButton;
    private ApiServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        avatarImageView = findViewById(R.id.avatar);
        usernameEditText = findViewById(R.id.ed_username);
        passwordEditText = findViewById(R.id.ed_password);
        emailEditText = findViewById(R.id.ed_email);
        nameEditText = findViewById(R.id.ed_name);
        registerButton = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServices.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiServices = retrofit.create(ApiServices.class);

        // Check and request permissions
        checkStoragePermission();

        avatarImageView.setOnClickListener(v -> selectImage());
        registerButton.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v->{
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private final ActivityResultLauncher<Intent> selectImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    avatarUri = result.getData().getData();
                    Glide.with(this).load(avatarUri).into(avatarImageView);
                }
            });

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageResultLauncher.launch(intent);
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();

        if (avatarUri == null || username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select an avatar image", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(getRealPathFromURI(avatarUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(avatarUri)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avartar", file.getName(), requestFile);

        RequestBody usernameBody = RequestBody.create(MultipartBody.FORM, username);
        RequestBody passwordBody = RequestBody.create(MultipartBody.FORM, password);
        RequestBody emailBody = RequestBody.create(MultipartBody.FORM, email);
        RequestBody nameBody = RequestBody.create(MultipartBody.FORM, name);

        Call<User> call = apiServices.register(usernameBody, passwordBody, emailBody, nameBody, body);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration  successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Registration  successful" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        }
    }
}
