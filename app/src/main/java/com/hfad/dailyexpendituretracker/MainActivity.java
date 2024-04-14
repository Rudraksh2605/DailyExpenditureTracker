package com.hfad.dailyexpendituretracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnLogin;
    private TextView mForgetPassword;
    private TextView mSignUpHere;
    private FirebaseAuth mAuth;
    private ProgressDialog mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginDetails();
        mBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    private void loginDetails(){
        mEmail = findViewById(R.id.login);
        mPass = findViewById(R.id.password);
        btnLogin = findViewById(R.id.LogInButton);
        mSignUpHere = findViewById(R.id.sign_up);
        mForgetPassword = findViewById(R.id.forget_password);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email Required!");
                    return;
                }

                if (TextUtils.isEmpty(pass)){
                    mPass.setError("Password is Required!");
                    return;
                }
                mBar.setMessage("Processing");
                mBar.show();

                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mBar.dismiss();
                            Toast.makeText(getApplicationContext(),"LogIn Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class ));
                        } else {
                            mBar.dismiss();
                            Toast.makeText(getApplicationContext(), "LogIn Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
                mSignUpHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                    }
                });

                mForgetPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), ResetActivity.class));
                    }
                });


    }
}