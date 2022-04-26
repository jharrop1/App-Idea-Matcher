package edu.neu.ideamatch.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.neu.ideamatch.CardStackRecyclerView;
import edu.neu.ideamatch.R;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private Button logInButton;
    private EditText mainEmail, mainPassword;

    private FirebaseAuth logAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logAuth = FirebaseAuth.getInstance();

        logInButton = (Button) findViewById(R.id.main_login_button);
        mainEmail = (EditText) findViewById(R.id.main_login_email);
        mainPassword = (EditText) findViewById(R.id.main_login_password);


        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mainEmail.getText().toString();
                final String password = mainPassword.getText().toString();

                if(email.isEmpty()) {
                    mainEmail.setError("Valid email is required");
                    mainEmail.requestFocus();
                    return;
                }

                if(password.isEmpty()) {
                    mainPassword.setError("Password is required");
                    mainPassword.requestFocus();
                    return;
                }

                logAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            //Login was not successful
                            Toast.makeText(MainActivity.this, "No user was found with those credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public void openRegistrationActivity(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //User is logged in it sends it to mainActivity
            Intent intent = new Intent(MainActivity.this, CardStackRecyclerView.class);
            startActivity(intent);
            finish();
        }
    }
}