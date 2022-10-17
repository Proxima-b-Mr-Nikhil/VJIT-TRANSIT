package com.vjit.vjittransit.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vjit.vjittransit.LoginActivity;
import com.vjit.vjittransit.R;

import java.util.HashMap;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    EditText email,password,name,phone;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    Button submit;
    DatabaseReference reference;
    FirebaseUser fuser;
    String userid;
    private FirebaseAuth.AuthStateListener authListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent=new Intent(requireActivity(),LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        email=root.findViewById(R.id.aemail);
        password=root.findViewById(R.id.apassword);
        name=root.findViewById(R.id.aname);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar2);
        phone=root.findViewById(R.id.aphone);
        submit=root.findViewById(R.id.asubmit);
        auth = FirebaseAuth.getInstance();

        String[] arraySpinn = new String[] {
                "--select--","Driver","Admin"
        };
        final Spinner s = (Spinner)root.findViewById(R.id.aposition);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(),
                android.R.layout.simple_spinner_item, arraySpinn);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String semail=email.getText().toString();
                final String spassword=password.getText().toString();
                final   String ph=phone.getText().toString();
                final   String sname=name.getText().toString();


                if (semail.isEmpty()) {
                    email.setError("Invalid");
                    return;
                }
                if (spassword.length()<6) {
                    password.setError("Minimum 6 digits or characters");
                    return;
                }

                if (sname.isEmpty()){
                    name.setError("Invalid");
                    return;
                }
                final String typ = s.getSelectedItem().toString();
                if (typ.equals("--select--")) {
                    s.requestFocus();
                    Toast.makeText(requireActivity(), "Please choose one option", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(semail, spassword)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(requireActivity(), "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    fuser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (fuser!=null){
                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                        assert firebaseUser != null;
                                        userid = firebaseUser.getUid();

                                        reference = FirebaseDatabase.getInstance().getReference("User").child(userid);


                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("id", userid);
                                        hashMap.put("job",typ);
                                        hashMap.put("name",sname);
                                        hashMap.put("phone",ph);
                                        hashMap.put("password",spassword);
                                        hashMap.put("email", semail);
                                        hashMap.put("status", "Active");
                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(requireActivity(), "Authentication completed", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    FirebaseAuth.getInstance().signOut();
                                                    auth.signOut();
                                                    email.setText("");
                                                    password.setText("");
                                                    phone.setText("");
                                                    name.setText("");
                                                }else {
                                                    Toast.makeText(requireActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }}
                        });
            }});


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
