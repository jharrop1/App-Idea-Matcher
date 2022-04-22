package edu.neu.ideamatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EditYourIdeaActivity extends AppCompatActivity {
    private String projectID;
    private TextView eyidIdeaName, eyidCreatorName, eyidDescription, eyidDesiredSkills,
            yidContactInfoTitle, yidContactInfo;
    private ImageView eyidImageLogo;
    private Button eyiSave;
    private IdeaDetails eyidIdeaDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_your_idea);

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
    }
}