package com.vjit.vjittransit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class AllUsersAdapter extends FirebaseRecyclerAdapter<User,AllUsersAdapter.myviewholder> {


    private Context context;
    public AllUsersAdapter(FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder myviewholder, int i, @NonNull final User User) {

        myviewholder.name.setText(User.getName());
        myviewholder.phone.setText(User.getPhone());
        myviewholder.id.setText(User.getId());
        myviewholder.email.setText(User.getEmail());
        myviewholder.status.setText(User.getStatus());
        myviewholder.job.setText(User.getJob());
        myviewholder.busno.setText(User.getBusno());
        myviewholder.driving.setText(User.getDriving());
        if (User.getJob().equals("Admin")){
          myviewholder.lbs.setVisibility(View.GONE);
          myviewholder.ldrv.setVisibility(View.GONE);
        }
        myviewholder.progressBar.setVisibility(View.GONE);

                myviewholder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(context, manageusers.class);
                        intent.putExtra("id",User.getId());
                        intent.putExtra("name",User.getName());
                        intent.putExtra("phone",User.getPhone());
                        intent.putExtra("password",User.getPassword());
                        intent.putExtra("email",User.getEmail());
                        intent.putExtra("status",User.getStatus());
                        intent.putExtra("job",User.getJob());
                        context.startActivity(intent);
                    }
                });


    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singleuser,parent,false);
        context = parent.getContext();
        return new myviewholder(view);

    }


    class myviewholder extends RecyclerView.ViewHolder{
        TextView name,email,id,status,phone,job,busno,driving;
        ProgressBar progressBar;
        LinearLayout linearLayout,lbs,ldrv;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.uname);
            phone=(TextView)itemView.findViewById(R.id.uphone);
            email=(TextView)itemView.findViewById(R.id.uemail);
            id=(TextView)itemView.findViewById(R.id.uid);
            status=(TextView)itemView.findViewById(R.id.ustatus);
            job=(TextView)itemView.findViewById(R.id.ujob);
            busno=(TextView)itemView.findViewById(R.id.busno);
            driving=(TextView)itemView.findViewById(R.id.driving);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.linerauser);
            lbs=(LinearLayout)itemView.findViewById(R.id.lbs);
            ldrv=(LinearLayout)itemView.findViewById(R.id.ldrv);
            progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar3);
        }
    }
}
