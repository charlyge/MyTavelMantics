package com.charlyge.android.mytavelmantics;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.charlyge.android.mytavelmantics.Model.TravelMantics;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {
    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private InputStream inputStream;
    private Bitmap imageBitmap;
    private static final String TAG = "AdminActivity";
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private Uri imageUri;
    private int PICK_IMAGE  = 87;
    private ImageView upload_image_view;
    private Button btn_upload_image;
    private TextInputEditText editx_dealTitle,editx_dealBody,editx_dealPrice;
    private int READ_EXTERNAL_STORAGE_CODE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        editx_dealTitle = findViewById(R.id.editx_dealTitle);
        editx_dealBody = findViewById(R.id.editx_dealBody);
        editx_dealPrice =  findViewById(R.id.editx_dealPrice);
        btn_upload_image = findViewById(R.id.btn_upload_image);
        btn_upload_image.setOnClickListener(v -> {
           checkPermissionAndUpload(this);
        });
        firebaseStorage = FirebaseStorage.getInstance();
        upload_image_view = findViewById(R.id.upload_image_view);
        storageReference = firebaseStorage.getReference();
    }

    private void uploadImageToFirebase() {
        progressDialog.show();
        progressDialog.setMessage("Please wait ...");
        progressDialog.setCancelable(false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] data = outputStream.toByteArray();
        UploadTask uploadTask = storageReference.child("Deals/" + imageUri.getPath() + ".jpg").putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {

        }).addOnFailureListener(e -> {
            Log.i(TAG,e.getLocalizedMessage());
            Toast.makeText(AdminActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        });

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
            storageReference.child("Deals/" + imageUri.getPath() + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                Toast.makeText(this, "Image Upload Successful", Toast.LENGTH_SHORT).show();
                UploadDealsToFirebaseDb(uri.toString());
            });

        });

    }

    private void UploadDealsToFirebaseDb(String ImageUrl) {
          String dealTitle = editx_dealTitle.getText().toString();
          String dealBody = editx_dealBody.getText().toString();
          String dealPrice = editx_dealPrice.getText().toString();

        TravelMantics travelMantics = new TravelMantics(dealTitle,dealBody,dealPrice,ImageUrl);
        db.collection("travelMantics").document().set(travelMantics).addOnSuccessListener(aVoid ->
                Toast.makeText(AdminActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show());
        progressDialog.dismiss();
        finish();
    }

    private void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                imageUri = data.getData();
                assert imageUri != null;
                inputStream = getContentResolver().openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(inputStream);
                upload_image_view.setImageBitmap(imageBitmap);
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.settings,menu);
       return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            if (!validteInput()){
                return false;
            }
            uploadImageToFirebase();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPermissionAndUpload(Context context){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_CODE);
                }
                else {
                    uploadImage();
                }
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length>0 && requestCode==READ_EXTERNAL_STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            uploadImage();
        }
        else {
            Toast.makeText(this, "Accept Permission to upload file", Toast.LENGTH_SHORT).show();
            checkPermissionAndUpload(this);
        }
    }

    private boolean validteInput(){
        String dealTitle = Objects.requireNonNull(editx_dealTitle.getText()).toString();
        String dealBody = Objects.requireNonNull(editx_dealBody.getText()).toString();
        String dealPrice = Objects.requireNonNull(editx_dealPrice.getText()).toString();
        if(TextUtils.isEmpty(dealBody) || TextUtils.isEmpty(dealTitle)|| TextUtils.isEmpty(dealPrice)){
            Toast.makeText(this, "Input All Fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
