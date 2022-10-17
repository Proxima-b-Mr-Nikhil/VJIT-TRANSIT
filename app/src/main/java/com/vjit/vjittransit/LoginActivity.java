package com.vjit.vjittransit;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vjit.vjittransit.Driver.DriverActivity;
import com.vjit.vjittransit.Notifications.Token;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

public class LoginActivity extends AppCompatActivity {
    String TAG="hi";
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    EditText email,password;
    Button loginbtn,stulogin;
    FirebaseUser fuser;
    RelativeLayout Relativelayout;
    DatabaseReference reference;
    int Permission_All = 1;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("you","");
        String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        if (!hasPermissions(LoginActivity.this, Permissions)) {
            ActivityCompat.requestPermissions(LoginActivity.this, Permissions, Permission_All);
        }

        String requiredPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
        int checkVal = getApplicationContext().checkCallingOrSelfPermission(requiredPermission);
        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    System.out.println("done"+"lll");
                }else{
                    System.out.println("no"+"lll");
                }
            }
        });
        if (checkVal==PackageManager.PERMISSION_GRANTED){

        if (!name.isEmpty()) {
            final FirebaseUser firebaseUser = auth.getCurrentUser();
            final String userid;
            if (firebaseUser != null) {
                userid = firebaseUser.getUid();


            if (name.equals("Student")) {

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                Animatoo.animateSlideLeft(LoginActivity.this);
                finish();
            }



                if (name.equals("Admin")) {
                    if (auth.getCurrentUser() != null) {

                        startActivity(new Intent(LoginActivity.this, SuperAdmin.class));
                        Animatoo.animateSlideLeft(LoginActivity.this);
                        finish();
                    }
                }
            if (name.equals("Driver")) {

                if (auth.getCurrentUser() != null) {

                    startActivity(new Intent(LoginActivity.this, DriverActivity.class));
                    Animatoo.animateSlideLeft(LoginActivity.this);
                    finish();
                }
            }

        }
        }
        }
        setContentView(R.layout.activity_login);

        changeStatusBarColor();
        email=findViewById(R.id.email);
        password=findViewById(R.id.Password);
        loginbtn=findViewById(R.id.loginbut);
        progressBar=findViewById(R.id.progressBar);
        Relativelayout=findViewById(R.id.relativelayout);
        stulogin=findViewById(R.id.btn_student);
        stulogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("you", "Student");
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    Animatoo.animateSlideLeft(LoginActivity.this);
            }
        });
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return false;
            }});

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        final String e=email.getText().toString();
        final String p=password.getText().toString();

        if (e.isEmpty()){
            email.setError("Enter Email");
            return;
        }
        if (p.isEmpty()){
            password.setError("Enter Password");
            return;
        }
        if (password.length()<6){
            Snackbar.make(Relativelayout, "Invalid Password", Snackbar.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(e, p).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),String.valueOf(e),Toast.LENGTH_LONG).show();
            }
        }).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(Relativelayout, "Invalid credentials", Snackbar.LENGTH_LONG).show();

                        } else {

                            final FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                final String userid = firebaseUser.getUid();

                                fuser = FirebaseAuth.getInstance().getCurrentUser();

                                reference = FirebaseDatabase.getInstance().getReference("User").child(userid);

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            User user = snapshot.getValue(User.class);
                                            assert user != null;
                                            reference = FirebaseDatabase.getInstance().getReference("User").child(userid);
                                            updateToken(FirebaseInstanceId.getInstance().getToken());

                                            String pos = user.getJob();
                                            String s = user.getStatus();


                                            if (!pos.isEmpty()&&s.equals("Active")) {
                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                                SharedPreferences.Editor editor = preferences.edit();


                                                if (pos.equals("Admin")) {
                                                    progressBar.setVisibility(View.GONE);
                                                    editor.putString("you", "Admin");
                                                    editor.apply();

                                                    startActivity(new Intent(LoginActivity.this, SuperAdmin.class));
                                                    Animatoo.animateSlideLeft(LoginActivity.this);
                                                }
                                                if (pos.equals("Driver")) {
                                                    progressBar.setVisibility(View.GONE);
                                                    editor.putString("you", "Driver");
                                                    editor.apply();
                                                    startActivity(new Intent(LoginActivity.this, DriverActivity.class));
                                                    Animatoo.animateSlideLeft(LoginActivity.this);

                                                }
                                            }else {

                                                Snackbar.make(Relativelayout,"inactive", Snackbar.LENGTH_LONG).show();
                                            }


                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressBar.setVisibility(View.GONE);
                                        Snackbar.make(Relativelayout, (CharSequence) error, Snackbar.LENGTH_LONG).show();
                                    }
                                });

                            }

                        }
                    }
                });
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
    public static boolean hasPermissions(FragmentActivity context, String... permissions){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
                    return  false;
                }
            }
        }
        return true;
    }

}

