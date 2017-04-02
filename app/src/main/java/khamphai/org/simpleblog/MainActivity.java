package khamphai.org.simpleblog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceUsers;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializes();
    }//End OnCreate

    private void initializes() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReferenceUsers.keepSynced(true);
        mDatabaseReference.keepSynced(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_blog);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
//        checkUserExist();
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter =
        new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.item_blog,
                BlogViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDes(model.getDes());
                viewHolder.setImage(getApplicationContext(), model.getImage());
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkUserExist() {
        final String user_id = mFirebaseAuth.getCurrentUser().getUid().toString();
        mDatabaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user_id)) {
                    Intent setupIntent = new Intent(MainActivity.this, SetUpActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView tvTitle = (TextView) mView.findViewById(R.id.tvTitle);
            tvTitle.setText(title);
        }

        public void setDes(String des) {
            TextView tvDes = (TextView) mView.findViewById(R.id.tvDes);
            tvDes.setText(des);
        }

        public void setImage(Context context, String image) {
            ImageView imvShow = (ImageView) mView.findViewById(R.id.imvShow);
            Picasso.with(context).load(image).into(imvShow);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }

        if (item.getItemId() == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mFirebaseAuth.signOut();
    }

}//End Main Class
