package fci.university.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import fci.university.chatapp.R;

public class SignUpActivity extends AppCompatActivity {

    EditText et_name, et_email, et_password, et_confirmPassword;
    TextView tv_addImage;
    Button btn_signUp;
    ImageView iv_profileImage;
    ProgressBar progressBar;
    boolean imagePicked = false;
    Uri imageUri;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        iv_profileImage = findViewById(R.id.iv_profileImageSignUp);
        tv_addImage = findViewById(R.id.tv_addImage);
        btn_signUp = findViewById(R.id.btn_signUp);
        et_name = findViewById(R.id.et_nameSignUp);
        et_email = findViewById(R.id.et_emailSignUp);
        et_password = findViewById(R.id.et_passwordSignUp);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        iv_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker();
            }
        });

        btn_signUp.setOnClickListener(view -> {
            if (isValidUserDetails()) {
                signUp();
            }
        });
    }

    private void signUp() {
        String userName = et_name.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            databaseReference.child("Users").child(auth.getUid()).child("user_name").setValue(userName);
                            databaseReference.child("Users").child(auth.getUid()).child("email").setValue(email);

                            if (imagePicked) {
                                UUID randomID = UUID.randomUUID();
                                final String imageName = "image/" + randomID + ".jpg";
                                storageReference.child(imageName).putFile(imageUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            StorageReference sr = firebaseStorage.getReference(imageName);
                                            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String filePath = uri.toString();
                                                    databaseReference.child("Users").child(auth.getUid()).child("image").setValue(filePath);
                                                }
                                            });
                                        });
                            }
                            else {
                                databaseReference.child("Users").child(auth.getUid()).child("image").setValue(null);
                            }

                            showToast("success!");
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        }
                        else {
                            showToast("SignUp Error!");
                        }

                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidUserDetails() {
        String name = et_name.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String confirmPassword = et_confirmPassword.getText().toString();

        if (name.trim().isEmpty()) {
            showToast("Enter your name!");
            return false;
        }
        else if (email.trim().isEmpty()) {
            showToast("Enter your email!");
            return false;
        }
        else if (password.trim().isEmpty()) {
            showToast("Enter the password!");
            return false;
        }
        else if (confirmPassword.trim().isEmpty()) {
            showToast("Confirm the password!");
            return false;
        }

        else if (!password.equals(confirmPassword)) {
            showToast("Password and confirm must be the same!");
            return false;
        }
        else {
            return true;
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            btn_signUp.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btn_signUp.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void imagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).rotate(90).into(iv_profileImage);
            imagePicked = true;
            tv_addImage.setVisibility(View.INVISIBLE);
        }
        else {
            imagePicked = false;
        }
    }
}