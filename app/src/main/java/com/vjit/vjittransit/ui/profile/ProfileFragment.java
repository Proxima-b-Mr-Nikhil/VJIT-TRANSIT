package com.vjit.vjittransit.ui.profile;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vjit.vjittransit.Driver.ProfileActivity;
import com.vjit.vjittransit.R;
import com.vjit.vjittransit.User;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    TextView name,phone,email,status,job,logout,id;
    private FirebaseAuth auth;
    FirebaseUser fuser;
    Spinner busno;
    String bus;
    DatabaseReference reference;
    private FirebaseAuth.AuthStateListener authListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);



        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                   Intent intent=new Intent(requireActivity(),ProfileActivity.class);
                   startActivity(intent);
                }
            }
        };
        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);
        job = root.findViewById(R.id.job);
        status = root.findViewById(R.id.status);
        phone = root.findViewById(R.id.mobile);
        logout=root.findViewById(R.id.logout);
        id = root.findViewById(R.id.id);
        busno=root.findViewById(R.id.busno);



        auth = FirebaseAuth.getInstance();
        fuser = auth.getCurrentUser();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                auth.signOut();
            }
        });


        if (fuser != null) {
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            assert fuser != null;
            reference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final User user = dataSnapshot.getValue(User.class);

                        name.setText(Objects.requireNonNull(user).getName());
                        phone.setText(Objects.requireNonNull(user).getPhone());
                        email.setText(Objects.requireNonNull(user).getEmail());
                        job.setText(Objects.requireNonNull(user).getJob());
                        id.setText(Objects.requireNonNull(user).getId());
                        status.setText(Objects.requireNonNull(user).getStatus());
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Name", user.getName().toUpperCase());
                        editor.putString("Phone", user.getPhone());
                        editor.putString("Email", user.getEmail());
                        editor.putString("Job", user.getJob());
                        editor.putString("userid", fuser.getUid());
                        editor.putString("status", user.getStatus());
                        editor.apply();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        name.setText(preference.getString("Name", ""));
        phone.setText(preference.getString("Phone", ""));
        email.setText(preference.getString("Email", ""));
        job.setText(preference.getString("Job", ""));

        status.setText(preference.getString("status", ""));
        id.setText(preference.getString("Userid", ""));


        id.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                assert cm != null;
                Objects.requireNonNull(cm).setText(id.getText().toString());
                Toast.makeText(requireActivity(), "Copied :)", Toast.LENGTH_SHORT).show();
                return true;
            }
        });



        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
