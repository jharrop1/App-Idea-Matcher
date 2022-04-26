package edu.neu.ideamatch.LikedIdeas;

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
import edu.neu.ideamatch.Login.MainActivity;
import edu.neu.ideamatch.NewIdeaActivity;
import edu.neu.ideamatch.R;
import edu.neu.ideamatch.YourIdeas.YourIdeaRecyclerView;

public class LikedIdeasRecyclerView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView liRecyclerView;
    private RecyclerView.Adapter likedIdeasAdapter;
    private RecyclerView.LayoutManager likedIdeasLayoutManager;
    private ArrayList<IdeaDetails> likedIdeasList = new ArrayList<IdeaDetails>();
    private ArrayList<String> projectIDList = new ArrayList<String>();

    private String userID;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseAuth rvAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_ideas);

        //Sets up the toolbar with onback pressed and navitemselected methods
        drawerLayout = findViewById(R.id.likedRV_drawer_layout);
        navigationView = findViewById(R.id.li_nav_view);
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

        if(likedIdeasList!=null){
            likedIdeasList.clear();
        }

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
        likedIdeasDB.addValueEventListener(new ValueEventListener() {
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

                    if (ideaDetailObject.getImageURL() == "" || ideaDetailObject.getImageURL() == null || projectIDList.contains(ideaDetailObject.getProjectID())) {

                    } else {
                        projectIDList.add(ideaDetailObject.getProjectID());
                        likedIdeasList.add(ideaDetailObject);
                        likedIdeasAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Close the menu not the applciuation
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Intent newSwipeHomeIntent = new Intent(LikedIdeasRecyclerView.this, CardStackRecyclerView.class);
                startActivity(newSwipeHomeIntent);
                break;
            case R.id.nav_add_idea:
                Intent newIdeaIntent = new Intent(LikedIdeasRecyclerView.this, NewIdeaActivity.class);
                startActivity(newIdeaIntent);
                break;
            case R.id.nav_view_liked_ideas:
                break;
            case R.id.nav_your_ideas:
                Intent newYourIdeasIntent = new Intent(LikedIdeasRecyclerView.this, YourIdeaRecyclerView.class);
                startActivity(newYourIdeasIntent);
                break;
            case R.id.nav_logout:
                rvAuth.signOut();
                Intent logoutIntent = new Intent(LikedIdeasRecyclerView.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
                break;
        }
        return true;
    }
}