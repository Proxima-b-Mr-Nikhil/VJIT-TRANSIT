package com.vjit.vjittransit.Driver;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vjit.vjittransit.Driver.DriverActivity;
import com.vjit.vjittransit.R;

public class TrackerService extends Service {
    DatabaseReference re;
    FirebaseUser fuser;
    FirebaseAuth auth;
    String user;
    private FirebaseAuth.AuthStateListener authListener;
    public static final String CHANNEL_ID = "VJIT Transit Location";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, DriverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_bus)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentText("Location sharing is active")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_bus,"Navigate to app",
                        pendingIntent)
                .build();

        startForeground(1, notification);
        LocationRequest request = new LocationRequest();
        request.setInterval(1000);
        request.setFastestInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        final String path = intent.getStringExtra("inputExtra");
        final String fid = intent.getStringExtra("inputId");


        int permission = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    DatabaseReference ref;
                    ref = FirebaseDatabase.getInstance().getReference(path+"/"+path);
                    auth = FirebaseAuth.getInstance();
                    fuser = auth.getCurrentUser();
                    assert fuser != null;
                    re = FirebaseDatabase.getInstance().getReference(path+"/"+"id");
                    Location location = locationResult.getLastLocation();
                    if (location != null) {

                        re.setValue(fid);
                        ref.setValue(location);
                    }
                }
            }, null);
        }

        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "VJIT Transit Location",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            serviceChannel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }

}

