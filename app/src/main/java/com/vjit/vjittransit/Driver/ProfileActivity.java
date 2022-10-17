package com.vjit.vjittransit.Driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vjit.vjittransit.LoginActivity;
import com.vjit.vjittransit.R;
import com.vjit.vjittransit.User;

import java.util.Arrays;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    TextView name,phone,email,status,job,logout,id;
    private FirebaseAuth auth;
    FirebaseUser fuser;
    Spinner busno;
    String bus;

    DatabaseReference reference,ref;
    private FirebaseAuth.AuthStateListener authListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity ( new Intent(ProfileActivity.this, LoginActivity.class));
                    Animatoo.animateSlideLeft(ProfileActivity.this);
                    finish();
                }
            }
        };
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        job = findViewById(R.id.job);
        status = findViewById(R.id.status);
        phone = findViewById(R.id.mobile);
        logout=findViewById(R.id.logout);
        id = findViewById(R.id.id);
        busno=findViewById(R.id.busno);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        auth = FirebaseAuth.getInstance();
        fuser = auth.getCurrentUser();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                auth.signOut();
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("Bus");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user=snapshot.getValue(User.class);
                if (user!=null){
                     int no= Integer.parseInt(user.getCount());
                    System.out.println(no);

                    int[] array = new int[no];
                    for (int i = 1; i <= array.length; i++){
                        array[i-1] = i;
                    }
                    String[] strArray = new String[array.length];

                    for (int i = 0; i < array.length; i++){
                        strArray[i] = String.valueOf(array[i]);
                        System.out.println(Arrays.toString(strArray));
                    }


                    ArrayAdapter<String> yea = new ArrayAdapter<String>(ProfileActivity.this,
                            android.R.layout.simple_spinner_item, strArray);
                    yea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    busno.setAdapter(yea);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    int n = preferences.getInt("position",0);
                    busno.setSelection(n);
                    busno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            bus=busno.getSelectedItem().toString();
                            if (!bus.isEmpty()){
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("Bus", bus);
                                editor.putInt("position", position);
                                editor.apply();
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        if (fuser != null) {
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            assert fuser != null;
            reference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
            reference.keepSynced(true);
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
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

                SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                name.setText(preference.getString("Name", ""));
                phone.setText(preference.getString("Phone", ""));
                email.setText(preference.getString("Email", ""));
                job.setText(preference.getString("Job", ""));

                status.setText(preference.getString("status", ""));
                id.setText(preference.getString("Userid", ""));


        id.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                assert cm != null;
                Objects.requireNonNull(cm).setText(id.getText().toString());
                Toast.makeText(getApplicationContext(), "Copied :)", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home) {
            startActivity(new Intent(ProfileActivity.this, DriverActivity.class));
            Animatoo.animateSlideRight(ProfileActivity.this);
            ProfileActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, DriverActivity.class));
        Animatoo.animateSlideRight(ProfileActivity.this);
        ProfileActivity.this.finish();
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


