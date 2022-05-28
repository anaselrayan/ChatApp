package fci.university.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fci.university.chatapp.R;

public class SignInActivity extends AppCompatActivity {

    Button btn_signIn;
    TextView tv_createAccount;
    EditText et_email, et_password;
    ProgressBar progressBar;

    FirebaseAuth auth;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        tv_createAccount = findViewById(R.id.tv_createAccount);
        btn_signIn = findViewById(R.id.btn_signIn);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        tv_createAccount.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidUserDetails()) {
                    signIn();
                }
            }
        });
    }

    private void signIn() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        loading(true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //TODO start the main activity
                            showToast("Sign in successfully");
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        }
                        else {
                            loading(false);
                            showToast("Error !");
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidUserDetails() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        if (email.trim().isEmpty()) {
            showToast("Enter your email!");
            return false;
        }
        else if (password.trim().isEmpty()) {
            showToast("Enter the password!");
            return false;
        }
        else {
            return true;
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            btn_signIn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btn_signIn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


}