package fci.university.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fci.university.chatapp.R;
import fci.university.chatapp.adapters.MessagesAdapter;
import fci.university.chatapp.model.Message;

public class ChatActivity extends AppCompatActivity {

    ImageView iv_backArrow;
    TextView tv_username;
    EditText et_message;
    FloatingActionButton btn_sendMessage;
    RecyclerView rv_chatHistory;

    String userName, otherName;

    FirebaseDatabase database;
    DatabaseReference reference;

    MessagesAdapter adapter;
    List<Message> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        iv_backArrow = findViewById(R.id.iv_backArrow);
        tv_username = findViewById(R.id.tv_userName);
        et_message = findViewById(R.id.et_message);
        btn_sendMessage = findViewById(R.id.btn_sendMessage);
        rv_chatHistory = findViewById(R.id.rv_chatHistory);

        rv_chatHistory.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        userName = getIntent().getStringExtra("userName");
        otherName = getIntent().getStringExtra("otherName");

        tv_username.setText(otherName);

        iv_backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
                finish();
            }
        });

        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String messageContent = et_message.getText().toString();
                if (!messageContent.equals("")) {
                    sendMessage(messageContent);
                    et_message.setText("");
                }
            }
        });
        // show the message
        getMessage();
    }
    private void sendMessage(String message) {
        String key = reference.child("Messages").child(userName).child(otherName).push().getKey();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("from", userName);

        reference.child("Messages").child(userName).child(otherName).child(key)
                .setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference.child("Messages").child(otherName).child(userName)
                                    .child(key).setValue(messageMap);
                        }
                    }
                });
    }

    public void getMessage() {
        reference.child("Messages").child(userName).child(otherName)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        Message message = snapshot.getValue(Message.class);
                        list.add(message);
                        adapter.notifyDataSetChanged();
                        rv_chatHistory.scrollToPosition(list.size() - 1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        adapter = new MessagesAdapter(list, userName);
        rv_chatHistory.setAdapter(adapter);
    }
}