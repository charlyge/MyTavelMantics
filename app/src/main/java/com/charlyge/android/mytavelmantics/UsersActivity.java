package com.charlyge.android.mytavelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.charlyge.android.mytavelmantics.Adapter.TravelManticsAdapter;
import com.charlyge.android.mytavelmantics.Model.TravelMantics;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView recy_view;
    private TravelManticsAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private int RC_SIGN_IN = 898;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private List<DocumentSnapshot> mSnapshots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        recy_view = findViewById(R.id.recy_view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        adapter = new TravelManticsAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recy_view.setLayoutManager(linearLayoutManager);
        recy_view.setHasFixedSize(true);
        recy_view.setAdapter(adapter);
        recy_view.setVisibility(View.VISIBLE);
        if (mFirebaseUser == null) {
            startSignIn();
            return;
        }
        loadDeals();
        FloatingActionButton fab_btn = findViewById(R.id.fab_btn);
        fab_btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        });
    }

    private void startSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            recreate();

        } else {

            Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDeals() {
        firebaseFirestore.collection("travelMantics").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            if (queryDocumentSnapshots != null) {
                List<TravelMantics> travelMantics = queryDocumentSnapshots.toObjects(TravelMantics.class);
                adapter.setTravelManticsList(travelMantics);
                for (DocumentChange change: queryDocumentSnapshots.getDocumentChanges()) {
                    if(change==null){
                        return;
                    }
                     switch (change.getType()){
                         case ADDED: onDocumentAdded(change);break;
                         case REMOVED:onDocumentRemoved(change);break;
                         case MODIFIED: onDocumentModified(change);
                     }
                }
            }
        });

    }



    protected void onDocumentAdded(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        adapter.notifyItemInserted(change.getNewIndex());
    }

    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            adapter.notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            adapter.notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    protected void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        adapter.notifyItemRemoved(change.getOldIndex());
    }
}
