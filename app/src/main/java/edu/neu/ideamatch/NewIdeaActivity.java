package edu.neu.ideamatch;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NewIdeaActivity extends AppCompatActivity {
    private String userID, imageUri;
    private EditText niIdeaName, niDescription, niDesiredSkills;
    private Button niCreateNewIdea, niSelectImage;
    private ImageView niImage;
    private Uri niImageUri;
    private Task<Uri> downloadUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseDatabase root;
    private DatabaseReference userNode;
    private StorageReference niStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea);

        niIdeaName = (EditText) findViewById(R.id.new_idea_name);
        niDescription = (EditText) findViewById(R.id.new_idea_description);
        niDesiredSkills = (EditText) findViewById(R.id.new_idea_desired_skills);
        niCreateNewIdea = (Button) findViewById(R.id.create_new_idea);
        niImage = (ImageView) findViewById(R.id.uploaded_image);


        root = FirebaseDatabase.getInstance();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Get the user node for the value listener
        userNode = root.getReference().child("Users").child(userID);

        String key = root.getReference().child("ProjectIdeas").push().getKey();

        niCreateNewIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewIdea(userID, userNode, key);
            }
        });

        niImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            niImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                niImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            niImage.setImageURI(niImageUri);
        }
    }


    private void createNewIdea(String userID, DatabaseReference userNode, String key) {
        //TODO Check if things are null if needed
        //Get the text from the detail boxes
        String ideaName = niIdeaName.getText().toString();
        String description = niDescription.getText().toString();
        String desiredSkills = niDesiredSkills.getText().toString();

        //Uploads the file and sets the imageURL in the database to the URI


        //Getting the email and name from the currently logged in user
        userNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue().toString();
                String creatorName = snapshot.child("userName").getValue().toString();


                IdeaDetails newIdea = new IdeaDetails(
                        ideaName,
                        email,
                        description,
                        creatorName,
                        desiredSkills,
                        key);
                //Adding the idea to the database and makes a succesful toast if it works
                userNode.child("yourIdeas").child(key).setValue(ideaName);
                root.getReference().child("ProjectIdeas").child(key).setValue(newIdea)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                uploadFile(key);
                                Toast.makeText(NewIdeaActivity.this,
                                        "Idea was successfully added",
                                        Toast.LENGTH_SHORT).show();

                                return;
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Instead of returning home could return to app details page
        returnHome();
    }

    private void uploadFile(String key) {
        if (niImageUri != null) {
            // Defining the storageReference
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("IdeaImages").child(System.currentTimeMillis() + "ideaName");
            // adding listeners on upload
            ref.putFile(niImageUri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            root.getReference().child("ProjectIdeas").child(key).child("imageURL").setValue(uri.toString());
                                        }
                                    });
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            Toast.makeText(NewIdeaActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void returnHome() {
        Intent intent = new Intent(NewIdeaActivity.this, CardStackRecyclerView.class);
        startActivity(intent);
    }
}