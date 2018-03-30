package app.swift;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client_viewCouriersProfileFeedbacks extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference ratings_feedbacksRef, accountRef,postRef;

    private RecyclerView mFeedbacks;

    String firstName_db,lastName_db,amount_db,image_profile_db = null;
    String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_view_couriers_profile_feedbacks);

        Intent client_request = getIntent();

        final String courier_id = client_request.getStringExtra("courier_id");


        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mFeedbacks = (RecyclerView) findViewById(R.id.client_feedbacks_recycleView);
        mFeedbacks.setHasFixedSize(true);
        mFeedbacks.setLayoutManager(layoutManager);

        getFeedbacks(courier_id);
    }

    public void getFeedbacks(String courier_id){
        MaterialDialog.Builder builder  = new MaterialDialog.Builder(Client_viewCouriersProfileFeedbacks.this)
                .content("")
                .contentGravity(GravityEnum.START)
                .cancelable(false)
                .progress(true,0);
        final MaterialDialog dialog = builder.build();
        dialog.show();

        ratings_feedbacksRef = database.getReference("ratings_feedbacks");
        final Query ratingsFeedback_query = ratings_feedbacksRef.orderByChild("rate_by_user_id").equalTo(courier_id);

        ratingsFeedback_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dialog.dismiss();
                    final FirebaseRecyclerAdapter<Feedbacks, Courier_Feedbacks.FeedbacksViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Feedbacks, Courier_Feedbacks.FeedbacksViewHolder>
                            (Feedbacks.class, R.layout.feedbacks_cardview, Courier_Feedbacks.FeedbacksViewHolder.class, ratingsFeedback_query) {
                        @Override
                        protected void populateViewHolder(final Courier_Feedbacks.FeedbacksViewHolder viewHolder, final Feedbacks model, int position) {


                            viewHolder.setMessage(model.feedback);
                            if(model.getCreated_at() == null){

                            }else{
                                DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy  hh:mm aaa");
                                Date netDate = (new Date(Long.parseLong(String.valueOf(model.getCreated_at()))));
                                viewHolder.date.setText(dateFormat.format(netDate));
                            }

                            accountRef = database.getReference("accounts");
                            Query accounts_query = accountRef.orderByChild("user_id").equalTo(model.getRespondent_user_id());
                            accounts_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                            firstName_db = snapshot.child("first_name").getValue().toString();
                                            image_profile_db = snapshot.child("image_profile").getValue().toString();
                                            lastName_db = snapshot.child("last_name").getValue().toString();
                                        }

                                        fullname = firstName_db + " " + lastName_db;

                                        viewHolder.setName(fullname);
                                        Transformation transformation = new RoundedTransformationBuilder()
                                                //.borderColor(Color.WHITE)
                                                //.borderWidthDp(1)
                                                .cornerRadiusDp(100)
                                                .oval(false)
                                                .build();
                                        Picasso.with(Client_viewCouriersProfileFeedbacks.this)
                                                .load(image_profile_db)
                                                .fit()
                                                .transform(transformation)
                                                .into(viewHolder.profileImage);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    };
                    mFeedbacks.setAdapter(firebaseRecyclerAdapter);
                }else{
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class FeedbacksViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView date;
        ImageView profileImage;
        public FeedbacksViewHolder(View itemView){
            super(itemView);
            mView=itemView;
            profileImage =(ImageView) mView.findViewById(R.id.feedback_profile_icon);
            date = (TextView) mView.findViewById(R.id.feedback_time_txtVw);
        }
        public void setName(String name){
            TextView name_txt = (TextView) mView.findViewById(R.id.feedback_name_txtVw);
            name_txt.setText(name);
        }
        public void setMessage(String message){
            TextView message_txt = (TextView) mView.findViewById(R.id.feedback_msg_txtVw);
            message_txt.setText(message);
        }
    }

    public void backToClientRequestCourierProfile(View view){
        Intent client_request = getIntent();
        final String courier_id = client_request.getStringExtra("courier_id");

        Intent intent = new Intent(Client_viewCouriersProfileFeedbacks.this, Client_Request_viewProfile.class);
        intent.putExtra("courier_id",courier_id);
        startActivity(intent);
    }
}
