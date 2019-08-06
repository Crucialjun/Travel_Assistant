package com.crucialtech.travelassistant;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private TextInputEditText mDealName;
    private TextInputEditText mDealPrice;
    private TextInputEditText mDealDescription;
    private TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UtilityClass.openFirebaseReference("traveldeals",this);
        mFirebaseDatabase = UtilityClass.mFirebaseDatabase;
        mDatabaseReference = UtilityClass.mDatabaseReference;


        mDealName = findViewById(R.id.deal_name);
        mDealPrice = findViewById(R.id.deal_price);
        mDealDescription = findViewById(R.id.deal_description);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if(deal == null){
            deal = new TravelDeal();
        }

        this.deal = deal;
        mDealName.setText(deal.getTitle());
        mDealDescription.setText(deal.getDescription());
        mDealPrice.setText(deal.getPrice());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_deal:
                saveDealToDatabase();
                cleanEditTexts();
                backToList();
                return true;
            case R.id.delete_deal:
                deleteDealFromDatabase();
                backToList();
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
        deal.setTitle(mDealName.getText().toString());
        deal.setDescription(mDealDescription.getText().toString());
        deal.setPrice(mDealPrice.getText().toString());

        if(deal.getId() == null){
            mDatabaseReference.push().setValue(deal);
        }else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }

    }

    private void deleteDealFromDatabase(){
        if(deal == null){
            Toast.makeText(this, "You can't delete a non-saved deal", Toast.LENGTH_LONG).show();
        }else{
            mDatabaseReference.child(deal.getId()).removeValue();
        }
    }

    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
}
