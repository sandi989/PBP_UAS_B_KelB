package com.laundry.laundry.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.laundry.laundry.R;
import com.laundry.laundry.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private  static final String TAG = "ProfileFragment";

    CircleImageView profileImageView;
    private Button btnEditProfile;
    TextView displayNameText, displayAlamatText;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = root.findViewById(R.id.profileImageView);
        displayNameText = root.findViewById(R.id.displayNameText);
        displayAlamatText = root.findViewById(R.id.displayAlamatText);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        if(firebaseUser != null){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    displayNameText.setText(user.getNama());
                    displayAlamatText.setText(user.getAlamat());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(),error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            if(firebaseUser.getPhotoUrl() != null){
                Glide.with(this)
                        .load(firebaseUser.getPhotoUrl())
                        .into(profileImageView);
            }
        }

        btnEditProfile = root.findViewById(R.id.goUpdateProfile);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(),EditProfileActivity.class);
                startActivity(i);
            }
        });

        return root;
    }
}