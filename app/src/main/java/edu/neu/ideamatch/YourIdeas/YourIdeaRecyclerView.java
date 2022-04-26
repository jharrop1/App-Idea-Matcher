package edu.neu.ideamatch.YourIdeas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.ideamatch.CardStackRecyclerView;
import edu.neu.ideamatch.IdeaDetails;
import edu.neu.ideamatch.LikedIdeas.LikedIdeasRecyclerView;
import edu.neu.ideamatch.Login.MainActivity;
import edu.neu.ideamatch.NewIdeaActivity;
import edu.neu.ideamatch.R;

public class YourIdeaRecyclerView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView yiRecyclerView;
    private RecyclerView.Adapter yourIdeasAdapter;
    private RecyclerView.LayoutManager yourIdeasLayoutManager;
    private ArrayList<IdeaDetails> yourIdeasList = new ArrayList<IdeaDetails>();
    private ArrayList<String> projectIDList = new ArrayList<String>();

    private String userID;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseAuth rvAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_idea_recycler_view);

        //Sets up the toolbar with onback pressed and navitemselected methods
        drawerLayout = findViewById(R.id.yi_drawer_layout);
        navigationView = findViewById(R.id.yi_nav_view);
        toolbar = findViewById(R.id.im_toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        rvAuth = FirebaseAuth.getInstance();
        FirebaseUser rvUser = FirebaseAuth.getInstance().getCurrentUser();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Clears the list for a fresh view
        if(yourIdeasList!=null){
            yourIdeasList.clear();
        }

        //Sets up the recycler view
        yiRecyclerView = (RecyclerView) findViewById(R.id.rv_your_ideas);
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

    //Get the project id
    private void getProjectID() {
        DatabaseReference yourIdeasDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("yourIdeas");
        yourIdeasDB.addValueEventListener(new ValueEventListener() {
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

    //Get the project from DB
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
                    IdeaDetails ideaDetailObject = new IdeaDetails(ideaName,
                            ideaContactInfo,
                            ideaDescription,
                            ideaCreatorName,
                            ideaDesiredSkills,
                            ideaID,
                            ideaImageURL);
                    //sets the project into the list for the Recylcerview
                    if (ideaDetailObject.getImageURL() == "" || ideaDetailObject.getImageURL() == null || projectIDList.contains(ideaDetailObject.getProjectID())) {

                    } else {
                        projectIDList.add(ideaDetailObject.getProjectID());
                        yourIdeasList.add(ideaDetailObject);
                        yourIdeasAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Close the menu not the application
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Navigation bar options and routing
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Intent newSwipeHomeIntent = new Intent(YourIdeaRecyclerView.this, CardStackRecyclerView.class);
                startActivity(newSwipeHomeIntent);
                break;
            case R.id.nav_add_idea:
                Intent newIdeaIntent = new Intent(YourIdeaRecyclerView.this, NewIdeaActivity.class);
                startActivity(newIdeaIntent);
                break;
            case R.id.nav_view_liked_ideas:
                Intent newLikedIdeasIntent = new Intent(YourIdeaRecyclerView.this, LikedIdeasRecyclerView.class);
                startActivity(newLikedIdeasIntent);
                break;
            case R.id.nav_your_ideas:
                break;
            case R.id.nav_logout:
                rvAuth.signOut();
                Intent logoutIntent = new Intent(YourIdeaRecyclerView.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
                break;
        }
        return true;
    }
}