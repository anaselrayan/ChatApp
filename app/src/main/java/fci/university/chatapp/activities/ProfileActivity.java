package fci.university.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import fci.university.chatapp.R;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView iv_profileImage;
    EditText et_name, et_email;
    Button btn_saveChanges;

    boolean imagePicked = false;
    Uri imageUri;
    String image;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        iv_profileImage = findViewById(R.id.iv_ProfileImage);
        et_name = findViewById(R.id.et_nameUpdate);
        et_email = findViewById(R.id.et_emailUpdate);
        btn_saveChanges = findViewById(R.id.btn_save);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        getUserInfo();

        iv_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker();
            }
        });
        btn_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });


    }

    private void getUserInfo() {
        databaseReference.child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = (String) snapshot.child("user_name").getValue();
                        String email = (String) snapshot.child("email").getValue();

                        et_name.setText(userName);
                        et_email.setText(email);
                        image = (String) snapshot.child("image").getValue();
                        if (image != null) {
                            Picasso.get().load(image).into(iv_profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void imagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void updateProfile() {
        String user_name = et_name.getText().toString();
        databaseReference.child("Users").child(firebaseUser.getUid())
                .child("user_name").setValue(user_name);

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
            databaseReference.child("Users").child(auth.getUid()).child("image").setValue(image);
        }

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(iv_profileImage);
            imagePicked = true;
        }
        else {
            imagePicked = false;
        }
    }
}