package com.group3.glimpse;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth uAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText username = (EditText) findViewById(R.id.user);
        EditText password = (EditText) findViewById(R.id.pass);

        Button newUserBtn = (Button) findViewById(R.id.newUser);
        Button loginBtn = (Button) findViewById(R.id.login);

        newUserBtn.setOnClickListener(v -> newUserBtn.setVisibility(View.INVISIBLE));
        loginBtn.setOnClickListener(v -> {

            String user = username.getText().toString();
            String pw = password.getText().toString();

            // If newUserBtn was NOT clicked, regular login attempt
            if (newUserBtn.getVisibility() != View.INVISIBLE)
            {
                uAuth.signInWithEmailAndPassword(user, pw)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                    System.out.println("User logged in successfully!");
                                else
                                    System.out.println("Login failed!");
                            }
                        });
            }

            // If they selected new user, create new account first
            else
            {
                uAuth.createUserWithEmailAndPassword(user, pw)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                    System.out.println("Account created successfully!");
                                else
                                    System.out.println("Account creation failed!");
                            }
                        });
            }

        });

        username.setOnClickListener(v -> username.setText(""));

    }
}
