package fci.university.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fci.university.chatapp.R;
import fci.university.chatapp.activities.ChatActivity;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    List<String> usersList;
    String userName;
    String otherName;
    Context context;

    FirebaseDatabase database;
    DatabaseReference reference;

    public UsersAdapter(List<String> usersList, String userName, Context context) {
        this.usersList = usersList;
        this.userName = userName;
        this.context = context;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_card, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        reference.child("Users").child(usersList.get(position))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        otherName = (String) snapshot.child("user_name").getValue();
                        String imageURL = (String) snapshot.child("image").getValue();

                        holder.tv_nameCard.setText(otherName);
                        if (imageURL != null) {
                            Picasso.get().load(imageURL).into(holder.iv_userImageCard);
                        }

                        //TODO Card view On Click

                        holder.cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra("userName", userName);
                                intent.putExtra("otherName", otherName);

//                                Toast.makeText(context, userName + " " + otherName, Toast.LENGTH_SHORT).show();
                                context.startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_nameCard;
        private CircleImageView iv_userImageCard;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_nameCard = itemView.findViewById(R.id.tv_nameCard);
            iv_userImageCard = itemView.findViewById(R.id.iv_userImage);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
