package khamphai.org.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetUpActivity extends AppCompatActivity {

    private ImageButton imbSetUpProfile;
    private EditText edtSetUpName;
    private Button btnSetUp;

    private Uri imgUri = null;
    private static final int GALLERY_REQUEST = 1;

    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference mDatabaseReferenceUsers;

    private StorageReference mStorageReference;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        initializes();
    }

    private void initializes() {

        mFirebaseAuth = FirebaseAuth.getInstance();

        mStorageReference = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        mDatabaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgressDialog = new ProgressDialog(this);
        imbSetUpProfile = (ImageButton) findViewById(R.id.imbSetUpProfile);
        edtSetUpName = (EditText) findViewById(R.id.edtSetUpName);
        btnSetUp = (Button) findViewById(R.id.btnSetUp);
        imbSetUpProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        btnSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpProfile();
            }
        });
    }

    private void setUpProfile() {
        final String name = edtSetUpName.getText().toString().trim();
        final String user_id = mFirebaseAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(name) && imgUri != null) {
            mProgressDialog.setMessage("SetUp Profile...");
            mProgressDialog.show();
            StorageReference filePath = mStorageReference.child(imgUri.getLastPathSegment());
            filePath.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downlaodUrl = taskSnapshot.getDownloadUrl();
                    mDatabaseReferenceUsers.child(user_id).child("name").setValue(name);
                    mDatabaseReferenceUsers.child(user_id).child("image").setValue(downlaodUrl.toString());
                    mProgressDialog.dismiss();
                }
            });


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                imbSetUpProfile.setImageURI(imgUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
