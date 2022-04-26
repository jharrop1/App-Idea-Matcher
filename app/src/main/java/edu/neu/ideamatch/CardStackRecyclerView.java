package edu.neu.ideamatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.Collections;

import edu.neu.ideamatch.LikedIdeas.LikedIdeasRecyclerView;
import edu.neu.ideamatch.Login.MainActivity;
import edu.neu.ideamatch.YourIdeas.YourIdeaRecyclerView;

public class CardStackRecyclerView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "CardStackRecyclerView";

    private CardStackLayoutManager csManager;
    private CardStackRecyclerViewAdapter csAdapter;
    private ArrayList<IdeaDetails> csItems;
    private ArrayList<String> projectIDList = new ArrayList<String>();
    private DatabaseReference csIdeaList;
    private DatabaseReference userNode;
    private FirebaseDatabase root;
    private String userID, projectID, ideaName;
    private TextView outofideas;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FirebaseAuth rvAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_stack_recycler_view);

        //Sets up the toolbar with onback pressed and navitemselected methods
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.im_toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Start of array list
        csItems = new ArrayList<>();
        rvAuth = FirebaseAuth.getInstance();
        FirebaseUser rvUser = FirebaseAuth.getInstance().getCurrentUser();
        csIdeaList = FirebaseDatabase.getInstance().getReference("ProjectIdeas");

        root = FirebaseDatabase.getInstance();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(csItems!=null){
            csItems.clear();
        }

        //Get the user node for the value listener
        userNode = root.getReference().child("Users").child(userID);


        //When a new idea is added it sees the change and notifies the list
        csIdeaList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    IdeaDetails idea = dataSnapshot.getValue(IdeaDetails.class);
                    if(idea.getImageURL() != null) {
                        csItems.add(idea);
                    }
                    Collections.shuffle(csItems);
                }
                csAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CardStackView cardStackView = findViewById(R.id.card_stack_view);
        csManager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                if (direction == Direction.Right){
                    //Toast.makeText(CardStackRecyclerView.this, "Direction Right", Toast.LENGTH_SHORT).show();
                    userNode.child("likedIdeas").child(projectID).setValue(ideaName);

                }
                if (direction == Direction.Left){
                    //Toast.makeText(CardStackRecyclerView.this, "Direction Left", Toast.LENGTH_SHORT).show();
                }

                if (csManager.getTopPosition() == csAdapter.getItemCount()){
                    //If you run out of ideas display the textview
                    outofideas = (TextView) findViewById(R.id.out_of_ideas);
                    outofideas.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView cardTvName = view.findViewById(R.id.idea_name);
                TextView cardTvProjectID = view.findViewById(R.id.idea_project_id);
                ideaName = cardTvName.getText().toString();
                projectID = cardTvProjectID.getText().toString();
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView cardTv = view.findViewById(R.id.idea_name);
            }
        });
        //Sets up the cardstack with setting from the library and the recyclerview portion
        csManager.setStackFrom(StackFrom.None);
        csManager.setVisibleCount(3);
        csManager.setTranslationInterval(8.0f);
        csManager.setScaleInterval(0.95f);
        csManager.setSwipeThreshold(0.3f);
        csManager.setMaxDegree(20.0f);
        csManager.setDirections(Direction.FREEDOM);
        csManager.setCanScrollHorizontal(true);
        csManager.setSwipeableMethod(SwipeableMethod.Manual);
        csManager.setOverlayInterpolator(new LinearInterpolator());
        csAdapter = new CardStackRecyclerViewAdapter(csItems);
        cardStackView.setLayoutManager(csManager);
        cardStackView.setAdapter(csAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
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
                break;
            case R.id.nav_add_idea:
                Intent newIdeaIntent = new Intent(CardStackRecyclerView.this, NewIdeaActivity.class);
                startActivity(newIdeaIntent);
                break;
            case R.id.nav_view_liked_ideas:
                Intent newLikedIdeasIntent = new Intent(CardStackRecyclerView.this, LikedIdeasRecyclerView.class);
                startActivity(newLikedIdeasIntent);
                break;
            case R.id.nav_your_ideas:
                Intent newYourIdeasIntent = new Intent(CardStackRecyclerView.this, YourIdeaRecyclerView.class);
                startActivity(newYourIdeasIntent);
                break;
            case R.id.nav_logout:
                rvAuth.signOut();
                Intent logoutIntent = new Intent(CardStackRecyclerView.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
                break;
        }
        return true;
    }
}