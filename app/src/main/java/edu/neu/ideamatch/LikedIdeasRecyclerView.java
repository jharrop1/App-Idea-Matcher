package edu.neu.ideamatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LikedIdeasRecyclerView extends AppCompatActivity {

    private RecyclerView liRecyclerView;
    private RecyclerView.Adapter likedIdeasAdapter;
    private RecyclerView.LayoutManager likedIdeasLayoutManager;
    private ArrayList<IdeaDetails> likedIdeasList = new ArrayList<IdeaDetails>();

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_ideas);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        liRecyclerView = (RecyclerView) findViewById(R.id.rv_liked_ideas);
        //If scrolling doesn't work add scrolling on the layout resource file
        liRecyclerView.setNestedScrollingEnabled(false);
        liRecyclerView.setHasFixedSize(true);
        likedIdeasLayoutManager = new LinearLayoutManager(LikedIdeasRecyclerView.this);
        liRecyclerView.setLayoutManager( likedIdeasLayoutManager);
        likedIdeasAdapter = new LikedIdeasRecyclerViewAdapter(LikedIdeasRecyclerView.this, getDataLikedProjects());
        liRecyclerView.setAdapter(likedIdeasAdapter);

        getProjectID();

    }


    private ArrayList<IdeaDetails> getDataLikedProjects () {
        return likedIdeasList;
    }

    private void getProjectID() {
        DatabaseReference likedIdeasDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("likedIdeas");
        likedIdeasDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot likedIdea : snapshot.getChildren()) {
                        getProject(likedIdea.getKey());
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
                    likedIdeasList.add(ideaDetailObject);
                    likedIdeasAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}