package com.vjit.vjittransit.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vjit.vjittransit.MainActivity;
import com.vjit.vjittransit.MapsActivity;
import com.vjit.vjittransit.R;
import com.vjit.vjittransit.User;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ListView myListView;
    ProgressBar progressBar;
    FirebaseUser fuser;
    DatabaseReference reference;

    Query query= FirebaseDatabase.getInstance().getReference("DriverRoute").limitToLast(300);
    ArrayList<String> myArrayList=new ArrayList<>();
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        myListView=(ListView)root.findViewById(R.id.listView);
        final ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(requireActivity(),android.R.layout.simple_list_item_1,myArrayList);
        myListView.setAdapter(myArrayAdapter);



        progressBar=(ProgressBar)root.findViewById(R.id.progressBar) ;


        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key=dataSnapshot.getKey();
                String Value=dataSnapshot.getValue(String.class) ;
                myArrayList.add("\n"+key+"    :    "+Value+"\n");
                myArrayAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final int a=position+1;
                        String b=String.valueOf(a);
                        if (a>0){
                            Intent intent = new Intent(requireActivity(), MapsActivity.class);
                            intent.putExtra("id",String.valueOf(a));
                            startActivity(intent);
                            reference = FirebaseDatabase.getInstance().getReference(b);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final User user = dataSnapshot.getValue(User.class);
                                        String c= Objects.requireNonNull(user).getId();
                                        if (c!=null) {
                                            reference = FirebaseDatabase.getInstance().getReference("User").child(c.trim());
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        User user1=dataSnapshot.getValue(User.class);
                                                        String z= Objects.requireNonNull(user1).getDriving();
                                                        if (z.equals("offline")){
                                                            Toast.makeText(requireActivity(),"Driver is offline",Toast.LENGTH_LONG).show();
                                                        }
                                                        if (z.equals("online")) {
                                                            Toast.makeText(requireActivity(),"Driver is online",Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }else {
                                        Toast.makeText(requireActivity(),"No data !",Toast.LENGTH_LONG).show();

                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                myArrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }
}
