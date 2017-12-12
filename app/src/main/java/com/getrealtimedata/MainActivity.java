package com.getrealtimedata;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    TextView getlocation;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DialogBox.setConfirm(c);

    /*    getlocation = (TextView) findViewById(R.id.getlocation);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("cars");



        mFirebaseDatabase.child("driver1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);


                Log.e("MainActivity", "User data is changed!" + customer.lat + ", " + customer.log);

                getlocation.setText("Latitude : " + customer.lat + "\n" +  "Longitude :" +customer.log);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


    }
}
