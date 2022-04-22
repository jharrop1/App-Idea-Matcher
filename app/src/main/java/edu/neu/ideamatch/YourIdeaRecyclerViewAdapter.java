package edu.neu.ideamatch;

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

public class YourIdeaRecyclerViewAdapter extends RecyclerView.Adapter<YourIdeaRecyclerViewAdapter.YourIdeasViewHolder> {
    private Context yourIdeasContext;
    private ArrayList<IdeaDetails> yourIdeasList;

    public YourIdeaRecyclerViewAdapter(Context yourIdeasContext, ArrayList<IdeaDetails> yourIdeasList) {
        this.yourIdeasContext = yourIdeasContext;
        this.yourIdeasList = yourIdeasList;
    }


    @NonNull
    @Override
    public YourIdeasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_idea_list_item, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        YourIdeaRecyclerViewAdapter.YourIdeasViewHolder yivh = new YourIdeaRecyclerViewAdapter.YourIdeasViewHolder(layoutView);
        return yivh;
    }

    @Override
    public void onBindViewHolder(@NonNull YourIdeaRecyclerViewAdapter.YourIdeasViewHolder holder, int position) {
        holder.yiProjectID.setText(yourIdeasList.get(position).getProjectID());
        holder.yiVhName.setText(yourIdeasList.get(position).getIdeaName());
        holder.yiVhDescription.setText(yourIdeasList.get(position).getIdeaDescription());
        Picasso.get()
                .load(yourIdeasList.get(position).getImageURL())
                .fit()
                .centerCrop()
                .into(holder.yiVhImage);
    }




    @Override
    public int getItemCount() {
        return yourIdeasList.size();
    }


    class YourIdeasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView yiVhImage;
        public TextView yiVhName, yiVhDescription, yiProjectID;

        YourIdeasViewHolder(@NonNull View yourIdeaView){
            super(yourIdeaView);
            yourIdeaView.setOnClickListener(this);

            yiVhImage = (ImageView) yourIdeaView.findViewById(R.id.your_idea_logo);
            yiVhName = yourIdeaView.findViewById(R.id.your_idea_name);
            yiVhDescription = yourIdeaView.findViewById(R.id.your_idea_description);
            yiProjectID = yourIdeaView.findViewById(R.id.your_idea_project_id);
        }


        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), YourIdeaDetailsActivity.class);
            Bundle ideaBundle = new Bundle();
            ideaBundle.putString("projectID", yiProjectID.getText().toString());
            intent.putExtras(ideaBundle);
            view.getContext().startActivity(intent);
        }
    }
}
