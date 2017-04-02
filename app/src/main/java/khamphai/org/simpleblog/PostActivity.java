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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

public class PostActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 1000;
    private ImageButton imbPic;
    private EditText edtTitle, edtDes;
    private Button btnPost;

    private Uri imgUri = null;
    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initializes();
    }

    private void initializes() {

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

        imbPic = (ImageButton) findViewById(R.id.imb_pic);
        edtTitle = (EditText) findViewById(R.id.edt_title);
        edtDes = (EditText) findViewById(R.id.edt_des);
        btnPost = (Button) findViewById(R.id.btn_post);
        mProgressDialog = new ProgressDialog(this);
        imbPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {

        final String title = edtTitle.getText().toString().trim();
        final String des = edtDes.getText().toString().trim();
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(des) && imgUri != null) {
            mProgressDialog.setMessage("Posting to Blog...");
            mProgressDialog.show();
            StorageReference storageReference = mStorageReference.child("Blog_Images").child(imgUri.getLastPathSegment());
            storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference mPost = mDatabaseReference.push();
                    mPost.child("title").setValue(title);
                    mPost.child("des").setValue(des);
                    mPost.child("image").setValue(downloadUrl.toString());
                    mProgressDialog.dismiss();
                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                }
            });
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                imbPic.setImageURI(imgUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
