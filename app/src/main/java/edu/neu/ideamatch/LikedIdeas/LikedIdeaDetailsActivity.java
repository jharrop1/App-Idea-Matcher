package edu.neu.ideamatch.LikedIdeas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
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

public class LikedIdeaDetailsActivity extends AppCompatActivity {
    private String projectID, creatorName;
    private TextView lidIdeaName, lidCreatorName, lidDescrption, lidDesiredSkills, lidContactInfoTitle, lidContactInfo;
    private ImageView lidImageLogo;
    private IdeaDetails lidIdeaDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_idea_details);

        //Get the project id given to the intent
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
        lidIdeaName = (TextView) findViewById(R.id.liked_idea_details_idea_name);
        lidCreatorName = (TextView) findViewById(R.id.liked_idea_details_creator_name);
        lidDescrption = (TextView) findViewById(R.id.liked_idea_details_idea_description);
        lidDesiredSkills = (TextView) findViewById(R.id.liked_idea_details_desired_skills);
        lidContactInfoTitle = (TextView) findViewById(R.id.liked_idea_details_contact_info_title);
        lidContactInfo = (TextView) findViewById(R.id.liked_idea_details_contact_info);
        lidImageLogo = (ImageView) findViewById(R.id.liked_idea_details_image);

        getProject(projectID);

    }

    //Gets the project with the string ID and populates views
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
                    lidIdeaDetails = new IdeaDetails(ideaName,
                            ideaContactInfo,
                            ideaDescription,
                            ideaCreatorName,
                            ideaDesiredSkills,
                            ideaID,
                            ideaImageURL);

                    setLikedIdeaDetailsInformation(lidIdeaDetails);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Actuually populates the views
    private void setLikedIdeaDetailsInformation(IdeaDetails ideaDetails) {
        lidIdeaName.setText(ideaDetails.getIdeaName());
        lidCreatorName.setText(ideaDetails.getCreatorName());
        lidDescrption.setText(ideaDetails.getIdeaDescription());
        lidDesiredSkills.setText(ideaDetails.getDesiredSkills());
        lidContactInfoTitle.setText("Reach out to " + ideaDetails.getCreatorName() + " at:");
        lidContactInfo.setText(ideaDetails.getContactInfo());
        Picasso.get()
                .load(ideaDetails.getImageURL())
                .fit()
                .centerCrop()
                .into(lidImageLogo);
    }
}