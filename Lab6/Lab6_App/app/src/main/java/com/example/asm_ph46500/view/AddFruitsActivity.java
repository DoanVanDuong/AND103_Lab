package com.example.asm_ph46500.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.asm_ph46500.MainActivity;
import com.example.asm_ph46500.R;
import com.example.asm_ph46500.model.Distributor;
import com.example.asm_ph46500.model.Fruit;
import com.example.asm_ph46500.services.ApiServices;
import com.google.android.material.textfield.TextInputEditText;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFruitsActivity extends AppCompatActivity {

    private ApiServices apiService;
    private TextInputEditText edName, edQuantity, edPrice, edStatus, edDescription;
    private Spinner spDistributor;
    private String id_Distributor;
    private ArrayList<Distributor> distributorArrayList;
    private Button btnSubmit;
    ImageView btnBack;
    private Uri avatarUri;
    private ImageView avatarImageView;
    private ArrayList<File> ds_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fruit);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServices.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiServices.class);
        initViews();
        distributorArrayList = new ArrayList<>();
        ds_image = new ArrayList<>();
        configSpinner();
        userListener();
    }

    private void initViews() {
        edName = findViewById(R.id.ed_name);
        edQuantity = findViewById(R.id.ed_quantity);
        edPrice = findViewById(R.id.ed_price);
        edStatus = findViewById(R.id.ed_status);
        edDescription = findViewById(R.id.ed_description);
        spDistributor = findViewById(R.id.sp_distributor);
        avatarImageView = findViewById(R.id.avatar);
        btnSubmit = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);
    }


    private void userListener() {
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status=validateInputs();
                if (status==true){
                Map<String, RequestBody> mapRequestBody = new HashMap<>();
                String _name = edName.getText().toString().trim();
                String _quantity = edQuantity.getText().toString().trim();
                String _price = edPrice.getText().toString().trim();
                String _status = edStatus.getText().toString().trim();
                String _description = edDescription.getText().toString().trim();
                mapRequestBody.put("name", getRequestBody(_name));
                mapRequestBody.put("quantity", getRequestBody(_quantity));
                mapRequestBody.put("price", getRequestBody(_price));
                mapRequestBody.put("status", getRequestBody(_status));
                mapRequestBody.put("description", getRequestBody(_description));
                mapRequestBody.put("id_distributor", getRequestBody(id_Distributor));
                ArrayList<MultipartBody.Part> _ds_image = new ArrayList<>();
                ds_image.forEach(file1 -> {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file1);
                    MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
                    _ds_image.add(multipartBodyPart);
                });
                Call<Fruit> call = apiService.addFruitWithFileImage(mapRequestBody, _ds_image);
                call.enqueue(new Callback<Fruit>() {
                    @Override
                    public void onResponse(Call<Fruit> call, Response<Fruit> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            Toast.makeText(AddFruitsActivity.this, "Đã thêm nhà phân phối thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddFruitsActivity.this, HomeActivity.class));


                        } else {
                            Toast.makeText(AddFruitsActivity.this, "Không thêm được nhà phân phối: " + response.message(), Toast.LENGTH_SHORT).show();
                            Log.e("AddDistributor", "Lỗi phản hồi: " + response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<Fruit> call, Throwable t) {
                        Toast.makeText(AddFruitsActivity.this, "Lỗi mạng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                        Log.e("AddDistributor", "Lỗi: ", t);
                    }
                });}else {
                    Toast.makeText(AddFruitsActivity.this, "Vui lòng nhập đầy đ dữ liệu", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }


    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    private void chooseImage() {
//        if (ContextCompat.checkSelfPermission(RegisterActivity.this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        Log.d("123123", "chooseAvatar: " + 123123);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        getImage.launch(intent);
//        }else {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//
//        }
    }
    private boolean validateInputs() {
        String name = edName.getText().toString().trim();
        String quantity = edQuantity.getText().toString().trim();
        String price = edPrice.getText().toString().trim();
        String status = edStatus.getText().toString().trim();
        String description = edDescription.getText().toString().trim();

        if (name.isEmpty()) {
            edName.setError("Name is required");
            return false;
        }
        if (quantity.isEmpty()) {
            edQuantity.setError("Quantity is required");
            return false;
        }
        if (price.isEmpty()) {
            edPrice.setError("Price is required");
            return false;
        }
        if (status.isEmpty()) {
            edStatus.setError("Status is required");
            return false;
        }
        if (description.isEmpty()) {
            edDescription.setError("Description is required");
            return false;
        }
        if (id_Distributor == null || id_Distributor.isEmpty()) {
            Toast.makeText(this, "Please select a distributor", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ds_image.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {

                        Uri tempUri = null;

                        ds_image.clear();
                        Intent data = o.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                tempUri = imageUri;

                                File file = createFileFormUri(imageUri, "image" + i);
                                ds_image.add(file);
                            }


                        } else if (data.getData() != null) {
                            // Trường hợp chỉ chọn một hình ảnh
                            Uri imageUri = data.getData();

                            tempUri = imageUri;
                            // Thực hiện các xử lý với imageUri
                            File file = createFileFormUri(imageUri, "image");
                            ds_image.add(file);

                        }

                        if (tempUri != null) {
                            Glide.with(AddFruitsActivity.this)
                                    .load(tempUri)
                                    .thumbnail(Glide.with(AddFruitsActivity.this).load(R.drawable.image))
                                    .centerCrop()
                                    .circleCrop()
                                    .skipMemoryCache(true)
//                                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                .skipMemoryCache(true)
                                    .into(avatarImageView);
                        }

                    }
                }
            });

    private File createFileFormUri(Uri path, String name) {
        File _file = new File(AddFruitsActivity.this.getCacheDir(), name + ".png");
        try {
            InputStream in = AddFruitsActivity.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            Log.d("123123", "createFileFormUri: " + _file);
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void configSpinner() {
        // Gọi API để lấy danh sách nhà phân phối
        Call<ArrayList<Distributor>> call = apiService.getListDistributor();
        call.enqueue(new Callback<ArrayList<Distributor>>() {
            @Override
            public void onResponse(Call<ArrayList<Distributor>> call, Response<ArrayList<Distributor>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    distributorArrayList.addAll(response.body());

                    String[] items = new String[distributorArrayList.size()];

                    for (int i = 0; i < distributorArrayList.size(); i++) {
                        items[i] = distributorArrayList.get(i).getName();
                    }
                    ArrayAdapter<String> adapterSpin = new ArrayAdapter<>(AddFruitsActivity.this, android.R.layout.simple_spinner_item, items);
                    adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDistributor.setAdapter(adapterSpin);

                    // Đặt sự kiện chọn item cho Spinner
                    spDistributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // Lấy id của nhà phân phối được chọn
                            id_Distributor = distributorArrayList.get(position).getId();
                            Log.d("123123", "onItemSelected: " + id_Distributor);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Xử lý trường hợp không chọn item nào
                        }
                    });

                    // Đặt lựa chọn mặc định
                    spDistributor.setSelection(0);
                } else {
                    Log.e("Main", "Lỗi phản hồi: " + response.message());
                    Toast.makeText(AddFruitsActivity.this, "Không tải được nhà phân phối", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Distributor>> call, Throwable t) {
                Log.e("Main", "Lỗi: ", t);
                Toast.makeText(AddFruitsActivity.this, "Lỗi mạng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}