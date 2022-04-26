package edu.neu.ideamatch.YourIdeas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import edu.neu.ideamatch.IdeaDetails;
import edu.neu.ideamatch.R;

public class YourIdeaDetailsActivity extends AppCompatActivity {

    private String projectID;
    private TextView yidIdeaName, yidCreatorName, yidDescription, yidDesiredSkills,
            yidContactInfoTitle, yidContactInfo;
    private ImageView yidImageLogo;
    private Button yiEdit;
    private IdeaDetails yidIdeaDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_idea_details);

        //Get the project ID from the bundle
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                projectID= null;
            } else {
                projectID= extras.getString("projectID");
            }
        } else {
            projectID = (String) savedInstanceState.getSerializable("projectID");
        }

        //Initialize views
        yidIdeaName = (TextView) findViewById(R.id.your_idea_details_idea_name);
        yidCreatorName = (TextView) findViewById(R.id.your_idea_details_creator_name);
        yidDescription = (TextView) findViewById(R.id.your_idea_details_idea_description);
        yidDesiredSkills = (TextView) findViewById(R.id.your_idea_details_desired_skills);
        yidContactInfoTitle = (TextView) findViewById(R.id.your_idea_details_contact_info_title);
        yidContactInfo = (TextView) findViewById(R.id.your_idea_details_contact_info);
        yidImageLogo = (ImageView) findViewById(R.id.your_idea_details_image);
        yiEdit = (Button) findViewById(R.id.edit_your_idea);

        getProject(projectID);

        //Open up edit activity
        yiEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditYourIdeaActivity.class);
                Bundle ideaBundle = new Bundle();
                ideaBundle.putString("projectID", projectID);
                intent.putExtras(ideaBundle);
                view.getContext().startActivity(intent);

            }
        });
    }

    //Retrives the project information from the db
    private void getProject(String key) {
        DatabaseReference projectDB = FirebaseDatabase.getInstance().getReference().child("ProjectIdeas").child(key);
        projectDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String ideaID = snapshot.getKey();
                    String ideaName = "";
                    String ideaContactInfo= "";
                    String ideaDescription = "";
                    String ideaCreatorName = "";
                    String ideaDesiredSkills = "";
                    String ideaImageURL = "";

                    if(snapshot.child("ideaName").getValue() != null) {
                        ideaName = snapshot.child("ideaName").getValue().toString();
                    }
                    if(snapshot.child("contactInfo").getValue() != null) {
                        ideaContactInfo = snapshot.child("contactInfo").getValue().toString();
                    }
                    if(snapshot.child("ideaDescription").getValue() != null) {
                        ideaDescription = snapshot.child("ideaDescription").getValue().toString();
                    }
                    if(snapshot.child("creatorName").getValue() != null) {
                        ideaCreatorName = snapshot.child("creatorName").getValue().toString();
                    }
                    if(snapshot.child("desiredSkills").getValue() != null) {
                        ideaDesiredSkills = snapshot.child("desiredSkills").getValue().toString();
                    }
                    if(snapshot.child("imageURL").getValue() != null) {
                        ideaImageURL = snapshot.child("imageURL").getValue().toString();
                    }
                    yidIdeaDetails = new IdeaDetails(ideaName,
                            ideaContactInfo,
                            ideaDescription,
                            ideaCreatorName,
                            ideaDesiredSkills,
                            ideaID,
                            ideaImageURL);

                    setYourIdeaDetailsInformation(yidIdeaDetails);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Sets the project information in to the views
    private void setYourIdeaDetailsInformation(IdeaDetails ideaDetails) {
        yidIdeaName.setText(ideaDetails.getIdeaName());
        yidCreatorName.setText(ideaDetails.getCreatorName());
        yidDescription.setText(ideaDetails.getIdeaDescription());
        yidDesiredSkills.setText(ideaDetails.getDesiredSkills());
        yidContactInfoTitle.setText("Reach out to " + ideaDetails.getCreatorName() + " at:");
        yidContactInfo.setText(ideaDetails.getContactInfo());
        Picasso.get()
                .load(ideaDetails.getImageURL())
                .fit()
                .centerCrop()
                .into(yidImageLogo);
    }

    public void onBackPressed() {
        Intent newYourIdeasIntent = new Intent(YourIdeaDetailsActivity.this, YourIdeaRecyclerView.class);
        startActivity(newYourIdeasIntent);
    }
}