package app.swift;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Courier_TransactionHistory extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference transactionsRef, accountRef,postRef;

    private RecyclerView mTransactions;

    String firstName_db,lastName_db,amount_db = null;
    String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier__transaction_history);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mTransactions = (RecyclerView) findViewById(R.id.transaction_history_recycleView);
        mTransactions.setHasFixedSize(true);
        mTransactions.setLayoutManager(layoutManager);

        getTransactions();
    }


    public void getTransactions(){

        transactionsRef = database.getReference("transactions");
        final Query transaction_query = transactionsRef.orderByChild("courier_id").equalTo(user.getUid());

        transaction_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    final FirebaseRecyclerAdapter<TransactionHistory,TransactionHistoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TransactionHistory, TransactionHistoryViewHolder>
                            (TransactionHistory.class, R.layout.transaction_history_cardview, TransactionHistoryViewHolder.class, transaction_query) {
                        @Override
                        protected void populateViewHolder(final TransactionHistoryViewHolder viewHolder,final TransactionHistory model, int position) {

                            if(model.getTransaction_status() == null || model.getTransaction_status().equals("pending") || model.getTransaction_status().equals("processing") ){
                                viewHolder.setLayout_GONE();
                            }else if(model.getTransaction_status().equals("completed")){
                                viewHolder.setLayout_VISIBLE();
                            }
                            if(model.getCompleted_at() == null){

                            }else{
                                DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
                                Date netDate = (new Date(Long.parseLong(String.valueOf(model.getCompleted_at()))));
                                viewHolder.date.setText(dateFormat.format(netDate));
                            }


                            accountRef = database.getReference("accounts");
                            Query accounts_query = accountRef.orderByChild("user_id").equalTo(model.getClient_id());
                            accounts_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                            firstName_db = snapshot.child("first_name").getValue().toString();
                                            lastName_db = snapshot.child("last_name").getValue().toString();
                                        }

                                        fullname = firstName_db + " " + lastName_db;

                                        viewHolder.setName(fullname);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            postRef = database.getReference("delivery_posts");
                            Query posts_query = postRef.orderByChild("post_id").equalTo(model.getPost_id());
                            posts_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                            amount_db = snapshot.child("amount").getValue().toString();
                                        }

                                        viewHolder.setAmount("₱"+ amount_db);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            viewHolder.viewFulldetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Courier_TransactionHistory.this, Courier_TransactionHistory_fullDetails.class);
                                    intent.putExtra("post_id", model.getPost_id());
                                    startActivity(intent);

                                }
                            });



                        }
                    };
                    mTransactions.setAdapter(firebaseRecyclerAdapter);


                }else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static class TransactionHistoryViewHolder extends  RecyclerView.ViewHolder{
        View mView;
        TextView date;
        Button viewFulldetails;
        public TransactionHistoryViewHolder(View itemView){
            super(itemView);
            mView=itemView;
            date = (TextView) mView.findViewById(R.id.courier_transaction_history_date_txtVw);
            viewFulldetails = (Button) mView.findViewById(R.id.courier_transaction_history_view_btn);
        }
        public void setAmount(String amount){
            TextView amount_txt = (TextView) mView.findViewById(R.id.courier_transaction_history_amount_txtVw);
            amount_txt.setText(amount);
        }
        public void setName(String name){
            TextView name_txt = (TextView) mView.findViewById(R.id.courier_transaction_history_name_txtVw);
            name_txt.setText(name);
        }
        public void setLayout_GONE(){
            ConstraintLayout transaction_layout = (ConstraintLayout) mView.findViewById(R.id.transaction_cardLayout);
            transaction_layout.setVisibility(View.GONE);
        }
        public void setLayout_VISIBLE(){
            ConstraintLayout transaction_layout = (ConstraintLayout) mView.findViewById(R.id.transaction_cardLayout);
            transaction_layout.setVisibility(View.VISIBLE);
        }
    }

    public void backToCourierNavMenu (View view){
        Intent intent = new Intent(this,Courier_NavMenu.class);
        startActivity(intent);
    }

}
