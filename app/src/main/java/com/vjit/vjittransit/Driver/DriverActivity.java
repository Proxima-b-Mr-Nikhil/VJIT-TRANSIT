package com.vjit.vjittransit.Driver;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vjit.vjittransit.LoginActivity;
import com.vjit.vjittransit.MapsActivity;
import com.vjit.vjittransit.Notifications.APIService;
import com.vjit.vjittransit.Notifications.Client;
import com.vjit.vjittransit.Notifications.Data;
import com.vjit.vjittransit.Notifications.MyResponse;
import com.vjit.vjittransit.Notifications.Sender;
import com.vjit.vjittransit.Notifications.Token;
import com.vjit.vjittransit.R;
import com.vjit.vjittransit.User;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DriverActivity extends FragmentActivity implements OnMapReadyCallback {
    Button btnStartService, btnStopService;
    DatabaseReference reference,referenc;
    FirebaseUser fuser;
    Context context;
    ImageButton nav;
    String userid;
    APIService apiService;

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 200;
    String bus;
    Button sos,help;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);


        sos=findViewById(R.id.sos);
        help=findViewById(R.id.help);
        auth = FirebaseAuth.getInstance();
        int Permission_All = 1;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bus = preferences.getString("Bus","");
        nav=findViewById(R.id.nav);
        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DriverActivity.this, ProfileActivity.class));
                Animatoo.animateSlideLeft(DriverActivity.this);
                DriverActivity.this.finish();
            }
        });
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                fuser = auth.getCurrentUser();
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                assert fuser != null;

                DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                tokens.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Token token = snapshot.getValue(Token.class);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            String no = preferences.getString("Bus","");
                            if (!no.isEmpty()){
                                Data data = new Data(fuser.getUid(), R.drawable.ic_bus,no, "VJIT TRANSIT"+" "+"SOS", userid);
                                Sender sender = new Sender(data, Objects.requireNonNull(token).getToken());
                                apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

                                apiService.sendNotification(sender)
                                        .enqueue(new Callback<MyResponse>() {
                                            @Override
                                            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                                if (response.code() == 200){
                                                    if (Objects.requireNonNull(response.body()).success != 1){
                                                        Toast.makeText(DriverActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(DriverActivity.this, "SOS request sent !", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {

                                            }
                                        });

                            }
                        }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                fuser = auth.getCurrentUser();
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                assert fuser != null;


                DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                tokens.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Token token = snapshot.getValue(Token.class);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            String no = preferences.getString("Bus","");
                            if (!no.isEmpty()){
                                Data data = new Data(fuser.getUid(), R.drawable.ic_bus,no, "VJIT TRANSIT"+" "+"Help", userid);                            Sender sender = new Sender(data, Objects.requireNonNull(token).getToken());
                            apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (Objects.requireNonNull(response.body()).success != 1) {
                                                    Toast.makeText(DriverActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(DriverActivity.this, "Help request sent !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {

                                        }
                                      });
                        }
                        }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity ( new Intent(DriverActivity.this, LoginActivity.class));
                    Animatoo.animateSlideLeft(DriverActivity.this);
                    finish();
                }
            }
        };

        final FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null){
        userid = firebaseUser.getUid();
            SharedPreferences preferen = PreferenceManager.getDefaultSharedPreferences(DriverActivity.this);
            SharedPreferences.Editor editor = preferen.edit();
            editor.putString("uid", userid);
            editor.apply();

            referenc = FirebaseDatabase.getInstance().getReference("User").child(userid);
            referenc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final User user = snapshot.getValue(User.class);
                    assert user != null;
                    String a=user.getStatus();
                    if (!a.equals("Active")){
                        FirebaseAuth.getInstance().signOut();
                        auth.signOut();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }

        btnStartService = findViewById(R.id.buttonStartService);

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bus.isEmpty()){

                                if (firebaseUser != null) {
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    final String no = preferences.getString("Bus","");
                                    reference = FirebaseDatabase.getInstance().getReference("User").child(userid);
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                           reference.child("driving").setValue("online");
                                            if (!no.isEmpty()){
                                                reference.child("busno").setValue(no);
                                            }
                                            String requiredPermission = Manifest.permission.ACCESS_FINE_LOCATION;
                                            int checkVal = getApplicationContext().checkCallingOrSelfPermission(requiredPermission);
                                            if (checkVal==PackageManager.PERMISSION_GRANTED){
                                                Toast.makeText(DriverActivity.this, "Online", Toast.LENGTH_SHORT).show();
                                                startService();
                                            }else {
                                                Toast.makeText(DriverActivity.this, "Turn on location !", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }
            }else {
                    Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                    startActivity(intent);
                }
            }
        });
        btnStopService = findViewById(R.id.buttonStopService);

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    final String no = preferences.getString("Bus","");
                    reference = FirebaseDatabase.getInstance().getReference("User").child(userid);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            reference.child("driving").setValue("offline");
                            if (snapshot.exists()){
                            if (!no.isEmpty()){
                                reference.child("busno").setValue(no);
                            }
                            Toast.makeText(DriverActivity.this, "Offline", Toast.LENGTH_SHORT).show();
                            stopService();
                        }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

            }
        });

        String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!hasPermissions(context, Permissions)) {
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;


    }

    public void startService() {

        if (!bus.isEmpty()){
        Intent serviceIntent = new Intent(this, TrackerService.class);
        serviceIntent.putExtra("inputExtra", bus);
            serviceIntent.putExtra("inputId", userid);
        ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, TrackerService.class);
        stopService(serviceIntent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap= googleMap;
        mMap.clear();

        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(DriverActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(DriverActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                try {
                    mMap.setMyLocationEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getDeviceLocation();
            }

            private void getDeviceLocation() {
                fusedLocationProviderClient.getLastLocation()
                        .addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()) {
                                    mLastKnownLocation = task.getResult();
                                    if (mLastKnownLocation != null) {

                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 15));
                                    } else {
                                        final LocationRequest locationRequest = LocationRequest.create();
                                        locationRequest.setInterval(10000);
                                        locationRequest.setFastestInterval(5000);
                                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                        locationCallback = new LocationCallback() {
                                            @Override
                                            public void onLocationResult(LocationResult locationResult) {
                                                super.onLocationResult(locationResult);
                                                if (locationResult == null) {
                                                    return;
                                                }
                                                mLastKnownLocation = locationResult.getLastLocation();
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 15));
                                                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                            }
                                        };
                                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                                    }
                                } else {
                                    Toast.makeText(DriverActivity.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });

        task.addOnFailureListener(DriverActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(DriverActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Authenticate with Firebase when the Google map is loaded
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setMaxZoomPreference(30);
        LatLngBounds INDIA = new LatLngBounds(
                new LatLng(17.3850, 78.4867), new LatLng(17.3850, 78.4867));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INDIA.getCenter(), 15));
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
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