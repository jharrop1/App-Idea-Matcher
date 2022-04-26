package edu.neu.ideamatch.LikedIdeas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.neu.ideamatch.IdeaDetails;
import edu.neu.ideamatch.R;

public class LikedIdeasRecyclerViewAdapter extends RecyclerView.Adapter<LikedIdeasRecyclerViewAdapter.LikedIdeasViewHolder> {
    private Context likedIdeasContext;
    private ArrayList<IdeaDetails> likedIdeasList;

    public LikedIdeasRecyclerViewAdapter(Context likedIdeasContext, ArrayList<IdeaDetails> likedIdeasList) {
        this.likedIdeasContext = likedIdeasContext;
        this.likedIdeasList = likedIdeasList;
    }


    @NonNull
    @Override
    public LikedIdeasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_idea_list_item, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        LikedIdeasViewHolder livh = new LikedIdeasViewHolder(layoutView);
        return livh;
    }

    @Override
    public void onBindViewHolder(@NonNull LikedIdeasViewHolder holder, int position) {
        holder.liProjectID.setText(likedIdeasList.get(position).getProjectID());
        holder.liVhName.setText(likedIdeasList.get(position).getIdeaName());
        holder.liVhDescription.setText(likedIdeasList.get(position).getIdeaDescription());
        Picasso.get()
                .load(likedIdeasList.get(position).getImageURL())
                .fit()
                .centerCrop()
                .into(holder.liVhImage);
    }




    @Override
    public int getItemCount() {
        return likedIdeasList.size();
    }


    class LikedIdeasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView liVhImage;
        public TextView liVhName, liVhDescription, liProjectID;

        LikedIdeasViewHolder(@NonNull View likedIdeaView){
            //Initialize variables
            super(likedIdeaView);
            likedIdeaView.setOnClickListener(this);

            liVhImage = (ImageView) likedIdeaView.findViewById(R.id.liked_idea_logo);
            liVhName = likedIdeaView.findViewById(R.id.liked_idea_name);
            liVhDescription = likedIdeaView.findViewById(R.id.liked_idea_description);
            liProjectID = likedIdeaView.findViewById(R.id.liked_idea_project_id);
        }


        @Override
        public void onClick(View view) {
            //On click goes to details activity
            Intent intent = new Intent(view.getContext(), LikedIdeaDetailsActivity.class);
            Bundle ideaBundle = new Bundle();
            ideaBundle.putString("projectID", liProjectID.getText().toString());
            intent.putExtras(ideaBundle);
            view.getContext().startActivity(intent);
        }
    }
}
