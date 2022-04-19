package edu.neu.ideamatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class NewIdeaActivity extends AppCompatActivity {
    private String userID;
    private EditText niIdeaName, niDescription, niDesiredSkills;
    private Button niCreateNewIdea;

    private FirebaseDatabase root;
    private DatabaseReference userNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea);

        niIdeaName = (EditText) findViewById(R.id.new_idea_name);
        niDescription = (EditText) findViewById(R.id.new_idea_description);
        niDesiredSkills = (EditText) findViewById(R.id.new_idea_desired_skills);
        niCreateNewIdea = (Button) findViewById(R.id.create_new_idea);


        root = FirebaseDatabase.getInstance();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        niCreateNewIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewIdea(userID);
            }
        });
    }

    private void createNewIdea(String userID) {
        //Get the text from the detail boxes
        String ideaName = niIdeaName.getText().toString();
        String description = niDescription.getText().toString();
        String desiredSkills = niDesiredSkills.getText().toString();

        //Get the user node for the value listener
        userNode = root.getReference().child("Users").child(userID);

        //Getting the email and name from the currently logged in user
        userNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getKey().toString();
                String creatorName = snapshot.child("userName").getKey().toString();

                IdeaDetails newIdea = new IdeaDetails(
                        R.drawable.tbd,
                        ideaName,
                        email,
                        description,
                        creatorName,
                        desiredSkills);
                //Adding the idea to the database and makes a succesful toast if it works
                root.getReference().child("ProjectIdeas").child(ideaName).setValue(newIdea)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NewIdeaActivity.this,
                                "Idea was successfully added",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}