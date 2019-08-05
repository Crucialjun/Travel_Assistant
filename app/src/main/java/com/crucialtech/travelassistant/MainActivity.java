package com.crucialtech.travelassistant;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private TextInputEditText mDealName;
    private TextInputEditText mDealPrice;
    private TextInputEditText mDealDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("traveldeals");
        mDealName = findViewById(R.id.deal_name);
        mDealPrice = findViewById(R.id.deal_price);
        mDealDescription = findViewById(R.id.deal_description);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_deal:
                saveDealToDatabase();
                cleanEditTexts();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    private void cleanEditTexts() {
        mDealName.setText("");
        mDealPrice.setText("");
        mDealDescription.setText("");
        mDealName.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_activity_menu,menu);
        return true;
    }

    private void saveDealToDatabase() {
        String dealName = mDealName.getText().toString();
        String dealPrice = mDealPrice.getText().toString();
        String dealDescription = mDealDescription.getText().toString();

        TravelDeal travelDeal = new TravelDeal("",dealName,dealDescription,dealPrice,"");
        mDatabaseReference.push().setValue(travelDeal);

    }
}
