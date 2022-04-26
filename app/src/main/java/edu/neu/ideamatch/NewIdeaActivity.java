package edu.neu.ideamatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
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
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.neu.ideamatch.YourIdeas.EditYourIdeaActivity;
import edu.neu.ideamatch.YourIdeas.YourIdeaRecyclerView;

public class NewIdeaActivity extends AppCompatActivity {
    private String userID, imageUri, currentPhotoPath;
    private EditText niIdeaName, niDescription, niDesiredSkills;
    private Button niCreateNewIdea, niSelectImage;
    private ImageView niImage;
    private Uri niImageUri;
    private Task<Uri> downloadUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private FirebaseDatabase root;
    private DatabaseReference userNode;
    private StorageReference niStorageRef;
    private boolean imageSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea);

        //Initialize views
        niIdeaName = (EditText) findViewById(R.id.new_idea_name);
        niDescription = (EditText) findViewById(R.id.new_idea_description);
        niDesiredSkills = (EditText) findViewById(R.id.new_idea_desired_skills);
        niCreateNewIdea = (Button) findViewById(R.id.create_new_idea);
        niImage = (ImageView) findViewById(R.id.uploaded_image);

        // Get DB and user
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

        //Set the image from camera or storage
        niImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder imageAlert = new AlertDialog.Builder(NewIdeaActivity.this);
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
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //From images on device
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
            imageSelected = true;
        }
        //From camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
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
                niImageUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, niImageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                imageSelected = true;
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = niImage.getWidth();
        int targetH = niImage.getHeight();

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
        niImage.setImageBitmap(bitmap);
    }


    //Creates the idea and uploads it to the db
    private void createNewIdea(String userID, DatabaseReference userNode, String key) {
        //Get the text from the detail boxes
        String ideaName = niIdeaName.getText().toString();
        String description = niDescription.getText().toString();
        String desiredSkills = niDesiredSkills.getText().toString();
        String image = niImage.toString();

        //Ensure the information is input
        if(ideaName.isEmpty()) {
            niIdeaName.setError("Valid email is required");
            niIdeaName.requestFocus();
            return;
        }

        if(description.isEmpty()) {
            niDescription.setError("Valid email is required");
            niDescription.requestFocus();
            return;
        }

        if(desiredSkills.isEmpty()) {
            niDesiredSkills.setError("Valid email is required");
            niDesiredSkills.requestFocus();
            return;
        }

        if(!imageSelected) {
            niImageUri = Uri.parse("android.resource://edu.neu.ideamatch/" + R.drawable.tbd2);
        }

        //Getting the email and name from the currently logged in user
        userNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue().toString();
                String creatorName = snapshot.child("userName").getValue().toString();
                //String downURIforIdea = uploadFile(key);

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
                                uploadFile(key, ideaName);
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
        goToYourIdeasRV();
    }

    private void uploadFile(String key, String ideaName) {
        if (niImageUri != null) {

            // Defining the storageReference
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("IdeaImages").child(System.currentTimeMillis() + ideaName);
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

    private void goToYourIdeasRV() {
        Intent intent = new Intent(NewIdeaActivity.this, YourIdeaRecyclerView.class);
        startActivity(intent);
    }

}