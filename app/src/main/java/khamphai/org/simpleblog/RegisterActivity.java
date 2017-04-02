package khamphai.org.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnRegister;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializes();
    }

    private void initializes() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgressDialog = new ProgressDialog(this);

        edtFullName = (EditText) findViewById(R.id.edtFullName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    private void startRegister() {

        final String name = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            mProgressDialog.setMessage("Signing Up...");
            mProgressDialog.show();
            mFirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = mFirebaseAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabaseReference.child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("Default");
                        mProgressDialog.dismiss();
                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }
            });
        }
    }
}
