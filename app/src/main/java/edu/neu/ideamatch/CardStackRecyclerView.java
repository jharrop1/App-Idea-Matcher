package edu.neu.ideamatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;

public class CardStackRecyclerView extends AppCompatActivity {

    private CardStackLayoutManager csManager;
    private CardStackRecyclerViewAdapter csAdapter;

    private FirebaseAuth rvAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_stack_recycler_view);

        rvAuth = FirebaseAuth.getInstance();
        FirebaseUser rvUser = FirebaseAuth.getInstance().getCurrentUser();

        CardStackView cardStackView = findViewById(R.id.card_stack_view);
        csManager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                if (direction == Direction.Right){
                    Toast.makeText(CardStackRecyclerView.this, "Direction Right", Toast.LENGTH_SHORT).show();
                    //Make it take the user to an ideaDetails page about the idea information
                    //Add it to a list of ideas liked by the user
                }
                if (direction == Direction.Left){
                    Toast.makeText(CardStackRecyclerView.this, "Direction Left", Toast.LENGTH_SHORT).show();
                }

                //Can add in code here to check if you are running out of ideas
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView cardTv = view.findViewById(R.id.idea_name);
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView cardTv = view.findViewById(R.id.idea_name);
            }
        });
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
        csAdapter = new CardStackRecyclerViewAdapter(addList());
        cardStackView.setLayoutManager(csManager);
        cardStackView.setAdapter(csAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private ArrayList<IdeaDetails> addList() {
        ArrayList<IdeaDetails> items = new ArrayList<>();
        items.add(new IdeaDetails(R.drawable.pinder, "Pinder", "email", "Tinder for your pets", "Spot", "skills"));
        items.add(new IdeaDetails(R.drawable.room_designer, "Room Designer", "email", "Uses augmented reailty to help redesign your room", "John", "skills"));
        items.add(new IdeaDetails(R.drawable.pickup_sports, "Pickup Sports", "email", "Find athletes in your area to play pickup games with", "Ron", "skills here"));
        items.add(new IdeaDetails(R.drawable.digital_grafiti, "Digital Graffiti", "email", "Uses augmented reailty to tag locations around the world and see other's tags by holding your phone up to it", "Micbac", "skills here"));
        items.add(new IdeaDetails(R.drawable.battery_alarm, "Battery Alarm", "email", "Causes your phone's alarm to go off when it is at a given battery percentage", "Seveer Haon", "skills here"));

        return items;
    }

    public void logoutUser(View view)  {
        rvAuth.signOut();
        Intent intent = new Intent(CardStackRecyclerView.this, MainActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void openAddIdeaActivity(View view)  {
        Intent intent = new Intent(CardStackRecyclerView.this, NewIdeaActivity.class);
        startActivity(intent);
        return;
    }
}