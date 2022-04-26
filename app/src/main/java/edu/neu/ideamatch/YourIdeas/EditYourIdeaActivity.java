package edu.neu.ideamatch.YourIdeas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.neu.ideamatch.CardStackRecyclerView;
import edu.neu.ideamatch.IdeaDetails;
import edu.neu.ideamatch.NewIdeaActivity;
import edu.neu.ideamatch.R;

public class EditYourIdeaActivity extends AppCompatActivity {
    private String projectID, userID, currentPhotoPath;
    private TextView eyidContactInfoTitle, eyidImageURL;
    private EditText eyidIdeaName, eyidCreatorName, eyidDescription, eyidDesiredSkills, eyidContactInfo;
    private ImageView eyidImageLogo;
    private Button eyiSave;
    private ImageButton eyiDelete;
    private IdeaDetails eyidIdeaDetails;
    private Uri eiImageUri;
    private boolean imageSelected = false;

    private FirebaseDatabase root;
    private DatabaseReference userNode;

    private static final int PICK_IMAGE_REQUEST = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_your_idea);

        //Get project id from bundles
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

        //Sets upd atabase references
        root = FirebaseDatabase.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userNode = root.getReference().child("Users").child(userID);
        DatabaseReference projectsList = FirebaseDatabase.getInstance().getReference().child("ProjectIdeas");

        //Initilize views
        eyidIdeaName = (EditText) findViewById(R.id.edit_your_idea_details_idea_name);
        eyidCreatorName = (EditText) findViewById(R.id.edit_your_idea_details_creator_name);
        eyidDescription = (EditText) findViewById(R.id.edit_your_idea_details_idea_description);
        eyidDesiredSkills = (EditText) findViewById(R.id.edit_your_idea_details_desired_skills);
        eyidContactInfoTitle = (TextView) findViewById(R.id.edit_your_idea_details_contact_info_title);
        eyidContactInfo = (EditText) findViewById(R.id.edit_your_idea_details_contact_info);
        eyidImageLogo = (ImageView) findViewById(R.id.edit_your_idea_details_image);
        eyiSave = (Button) findViewById(R.id.save_your_idea);
        eyiDelete = (ImageButton) findViewById(R.id.delete_your_idea);
        eyidImageURL = (TextView) findViewById(R.id.edit_your_idea_details_image_url);

        //Loads the project dat into fields
        getProject(projectID);



        //Button to select new image with an alert given for the
        eyidImageLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder imageAlert = new AlertDialog.Builder(EditYourIdeaActivity.this);
                imageAlert.setTitle("Select Image");
                imageAlert.setMessage("How would you like to select your image?");
                imageAlert.setPositiveButton("From Images", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent selectPhotoIntent = new Intent(Intent.ACTION_PICK);
                        selectPhotoIntent.setType("image/*");
                        startActivityForResult(selectPhotoIntent, PICK_IMAGE_REQUEST);
                        dialogInterface.dismiss();
                    }
                });
                imageAlert.setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dispatchTakePictureIntent();
                    }
                });
                imageAlert.show();
            }
        });

        //Save button
        eyiSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateIdea(userID, userNode, projectID);
                goToYourIdeasRV(projectID);
            }
        });

        //Delete Button
        eyiDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Deletes the child
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(EditYourIdeaActivity.this);
                deleteAlert.setTitle("Delete Idea Confirmation");
                deleteAlert.setMessage("Are you sure you want to delete this idea?");
                deleteAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            removeFromUsers(projectID);
                            projectsList.child(projectID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    goToHomeScreen();
                                }
                            });
                            dialogInterface.dismiss();
                        }
                    }

                );
                deleteAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                deleteAlert.show();
            }
        });

    }

    //Where the startactivityfor result is handleded
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //pick image from gallery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            eiImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                eiImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            eyidImageLogo.setImageURI(eiImageUri);
            imageSelected = true;
        }
        //take photo from camera and save it
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }


    //get the project and the idea details
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
                        eyidImageURL.setText(ideaImageURL);
                    }
                    eyidIdeaDetails = new IdeaDetails(ideaName,
                            ideaContactInfo,
                            ideaDescription,
                            ideaCreatorName,
                            ideaDesiredSkills,
                            ideaID,
                            ideaImageURL);

                    setYourIdeaDetailsInformation(eyidIdeaDetails);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Sets the details to the views
    private void setYourIdeaDetailsInformation(IdeaDetails ideaDetails) {
        eyidIdeaName.setText(ideaDetails.getIdeaName());
        eyidCreatorName.setText(ideaDetails.getCreatorName());
        eyidDescription.setText(ideaDetails.getIdeaDescription());
        eyidDesiredSkills.setText(ideaDetails.getDesiredSkills());
        eyidContactInfoTitle.setText("Reach out to " + ideaDetails.getCreatorName() + " at:");
        eyidContactInfo.setText(ideaDetails.getContactInfo());
        Picasso.get()
                .load(ideaDetails.getImageURL())
                .fit()
                .centerCrop()
                .into(eyidImageLogo);
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(EditYourIdeaActivity.this, CardStackRecyclerView.class);
        startActivity(intent);
    }

    private void gotoRV() {
        Intent intent = new Intent(EditYourIdeaActivity.this, YourIdeaRecyclerView.class);
        startActivity(intent);
    }

    //Go to ideas and puts the id into a budnle
    private void goToYourIdeasRV(String projectID) {
        Intent resfreshIntent = new Intent(EditYourIdeaActivity.this, YourIdeaDetailsActivity.class);
        Bundle ideaBundle = new Bundle();
        ideaBundle.putString("projectID", projectID);
        resfreshIntent.putExtras(ideaBundle);
        startActivity(resfreshIntent);
    }

    //Remove ideas from the database if you made it or someone liked it.
    private void removeFromUsers(String keyToRemove) {
        DatabaseReference userList = FirebaseDatabase.getInstance().getReference().child("Users");
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot User: snapshot.getChildren()) {
                    if (User.child("likedIdeas").hasChild(keyToRemove)) {
                        User.child("likedIdeas").child(keyToRemove).getRef().setValue(null);
                    }
                    if (User.child("yourIdeas").hasChild(keyToRemove)) {
                        User.child("yourIdeas").child(keyToRemove).getRef().setValue(null);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //Update the idea based on what is given in editactivity
    private void updateIdea(String userID, DatabaseReference userNode, String key) {
        //Get the text from the detail boxes
        String ideaName = eyidIdeaName.getText().toString();
        String description = eyidDescription.getText().toString();
        String desiredSkills = eyidDesiredSkills.getText().toString();
        String contactInfo = eyidContactInfo.getText().toString();
        String creatorName = eyidCreatorName.getText().toString();
        String image = eyidImageURL.getText().toString();

        if(ideaName.isEmpty()) {
            eyidIdeaName.setError("Idea name is required");
            eyidIdeaName.requestFocus();
            return;
        }
        if(description.isEmpty()) {
            eyidDescription.setError("Idea description is required");
            eyidDescription.requestFocus();
            return;
        }
        if(desiredSkills.isEmpty()) {
            eyidDesiredSkills.setError("Desired skills is required");
            eyidDesiredSkills.requestFocus();
            return;
        }
        if(contactInfo.isEmpty()) {
            eyidContactInfo.setError("Contact information is required");
            eyidContactInfo.requestFocus();
            return;
        }
        if(creatorName.isEmpty()) {
            eyidCreatorName.setError("Your name is required");
            eyidCreatorName.requestFocus();
            return;
        }

        userNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                IdeaDetails newIdea = new IdeaDetails(
                        ideaName,
                        contactInfo,
                        description,
                        creatorName,
                        desiredSkills,
                        key,
                        image);
                //Adding the idea to the database and makes a succesful toast if it works
                userNode.child("yourIdeas").child(key).setValue(ideaName);
                root.getReference().child("ProjectIdeas").child(key).setValue(newIdea)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                uploadFile(key, ideaName);
                                return;
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Uploads file to android storage
    private void uploadFile(String key, String ideaname) {
        if (eiImageUri != null) {
            // Defining the storageReference
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("IdeaImages").child(System.currentTimeMillis() + ideaname);
            // adding listeners on upload
            ref.putFile(eiImageUri)
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
                            Toast.makeText(EditYourIdeaActivity.this,
                                    "Failed " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                eiImageUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, eiImageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                imageSelected = true;
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = eyidImageLogo.getWidth();
        int targetH = eyidImageLogo.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        eyidImageLogo.setImageBitmap(bitmap);
    }
}