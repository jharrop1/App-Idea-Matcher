package edu.neu.ideamatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class YourIdeaRecyclerView extends AppCompatActivity {

    private RecyclerView yiRecyclerView;
    private RecyclerView.Adapter yourIdeasAdapter;
    private RecyclerView.LayoutManager yourIdeasLayoutManager;
    private ArrayList<IdeaDetails> yourIdeasList = new ArrayList<IdeaDetails>();

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_idea_recycler_view);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        yiRecyclerView = (RecyclerView) findViewById(R.id.rv_your_ideas);
        //If scrolling doesn't work add scrolling on the layout resource file
        yiRecyclerView.setNestedScrollingEnabled(false);
        yiRecyclerView.setHasFixedSize(true);
        yourIdeasLayoutManager = new LinearLayoutManager(YourIdeaRecyclerView.this);
        yiRecyclerView.setLayoutManager( yourIdeasLayoutManager);
        yourIdeasAdapter = new YourIdeaRecyclerViewAdapter(YourIdeaRecyclerView.this, getDataYourProjects());
        yiRecyclerView.setAdapter(yourIdeasAdapter);

        getProjectID();

    }


    private ArrayList<IdeaDetails> getDataYourProjects () {
        return yourIdeasList;
    }

    private void getProjectID() {
        DatabaseReference yourIdeasDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("yourIdeas");
        yourIdeasDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot yourIdea : snapshot.getChildren()) {
                        getProject(yourIdea.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProject(String key) {
        DatabaseReference projectDB = FirebaseDatabase.getInstance().getReference().child("ProjectIdeas").child(key);
        projectDB.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    IdeaDetails ideaDetailObject = new IdeaDetails(ideaName,
                            ideaContactInfo,
                            ideaDescription,
                            ideaCreatorName,
                            ideaDesiredSkills,
                            ideaID,
                            ideaImageURL);
                    yourIdeasList.add(ideaDetailObject);
                    yourIdeasAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}