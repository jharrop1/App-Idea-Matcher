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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.ideamatch.CardStackRecyclerView;
import edu.neu.ideamatch.R;
import edu.neu.ideamatch.User;

public class RegistrationActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private Button regRegisterButton;
    private EditText regEmail, regPassword, regUserName;

    private FirebaseAuth regAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        regAuth = FirebaseAuth.getInstance();

//        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null) {
//                    //User is logged in it sends it to mainActivity
//                    Intent intent = new Intent(RegistrationActivity.this, CardStackRecyclerView.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        };

        regRegisterButton = (Button) findViewById(R.id.register_account);

        regEmail = (EditText) findViewById(R.id.registration_email);
        regPassword = (EditText) findViewById(R.id.registration_password);
        regUserName = (EditText) findViewById(R.id.registration_username);

        regUserName.requestFocus();

        regRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        final String email = regEmail.getText().toString();
        final String password = regPassword.getText().toString();
        final String userName = regUserName.getText().toString();

        if(userName.isEmpty()) {
            regUserName.setError("Valid email is required");
            regUserName.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            regEmail.setError("Valid email is required");
            regEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            regPassword.setError("Password is required");
            regPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            regPassword.setError("Password must be 6 characters long");
            regPassword.requestFocus();
            return;
        }

        regAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Should probably make an onsuccess instead of an on failure
                if (!task.isSuccessful()) {
                    //Registration was not successful
                    Toast.makeText(RegistrationActivity.this, "Registration was not successful", Toast.LENGTH_SHORT).show();
                } else {
                    String userId = regAuth.getCurrentUser().getUid();
                    User user = new User(userName, email);
                    DatabaseReference currentUserNode = FirebaseDatabase.getInstance().getReference().child("Users");
                    currentUserNode.child(userId).setValue(user);


                }
            }
        });
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
            Intent intent = new Intent(RegistrationActivity.this, CardStackRecyclerView.class);
            startActivity(intent);
            finish();
        }
    }

}