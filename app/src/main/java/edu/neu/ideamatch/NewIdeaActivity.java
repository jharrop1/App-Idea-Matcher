package edu.neu.ideamatch;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseDatabase root;
    private DatabaseReference userNode;
    private StorageReference niStorageRef;

//    ActivityResultLauncher<Intent> startForResultFromGallery = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri uri) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // There are no request codes
//                        Intent data = result.getData();
//                        doSomeOperations();
//                    }
//                }
//            });


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

        niCreateNewIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewIdea(userID);
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
            niImage.setImageURI(niImageUri);
        }
    }


    private void createNewIdea(String userID) {
        //TODO Check if things are null if needed
        //Get the text from the detail boxes
        String ideaName = niIdeaName.getText().toString();
        String description = niDescription.getText().toString();
        String desiredSkills = niDesiredSkills.getText().toString();

        if (niImageUri != null) {
            StorageReference imagepath = FirebaseStorage.getInstance().getReference().child("IdeaImages").child(System.currentTimeMillis() + "ideaName");
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), niImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imagepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageUri = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                }
            });
        } else {
            finish();
        }

        //Get the user node for the value listener
        userNode = root.getReference().child("Users").child(userID);

        //Getting the email and name from the currently logged in user
        userNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getKey().toString();
                String creatorName = snapshot.child("userName").getKey().toString();

                IdeaDetails newIdea = new IdeaDetails(
                        imageUri,
                        ideaName,
                        email,
                        description,
                        creatorName,
                        desiredSkills);
                //Adding the idea to the database and makes a succesful toast if it works
                root.getReference().child("ProjectIdeas").child(ideaName).setValue(newIdea)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NewIdeaActivity.this,
                                "Idea was successfully added",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}