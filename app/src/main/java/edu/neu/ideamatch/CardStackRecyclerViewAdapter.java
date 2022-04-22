package edu.neu.ideamatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CardStackRecyclerViewAdapter extends RecyclerView.Adapter<CardStackRecyclerViewAdapter.CardViewHolder>{

    ArrayList<IdeaDetails> ideas;

    public CardStackRecyclerViewAdapter(ArrayList<IdeaDetails> adapterIdeas) {
        this.ideas = adapterIdeas;
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.idea_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.setIdea(ideas.get(position));
    }

    @Override
    public int getItemCount() {
        return ideas.size();
    }

    public ArrayList<IdeaDetails> getIdeas() {
        return ideas;
    }

    public void setIdeas(ArrayList<IdeaDetails> ideas) {
        this.ideas = ideas;
    }

    class CardViewHolder extends RecyclerView.ViewHolder{

        ImageView vhImage;
        TextView vhName, vhDescription, vhDesiredSkills, vhProjectId;

        CardViewHolder(@NonNull View itemView){
            super(itemView);
            vhImage = itemView.findViewById(R.id.idea_image);
            vhName = itemView.findViewById(R.id.idea_name);
            vhDescription = itemView.findViewById(R.id.idea_description);
            vhDesiredSkills = itemView.findViewById(R.id.idea_desired_skills_cv);
            vhProjectId = itemView.findViewById(R.id.idea_project_id);
        }

        void setIdea(IdeaDetails ideaData) {
            String test = ideaData.getImageURL();
            Picasso.get()
                    .load(test)
                    .fit()
                    .centerCrop()
                    .into(vhImage);
            vhName.setText(ideaData.getIdeaName());
            vhDescription.setText(ideaData.getIdeaDescription());
            vhDesiredSkills.setText(ideaData.getDesiredSkills());
            vhProjectId.setText(ideaData.getProjectID());
        }
    }
}
