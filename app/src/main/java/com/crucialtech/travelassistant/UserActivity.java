package com.crucialtech.travelassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;

public class UserActivity extends AppCompatActivity {


    @Override
    protected void onResume() {
        super.onResume();
        UtilityClass.openFirebaseReference("traveldeals",this);
        RecyclerView rvDeals = findViewById(R.id.rv_travel_deals);
        final DealsAdapter adapter = new DealsAdapter();
        rvDeals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvDeals.setAdapter(adapter);
        UtilityClass.attachListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.user_activity_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.new_travel_deal);
        if(UtilityClass.isAdmin){
            menuItem.setVisible(true);
        }else{
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_travel_deal:
                Intent intent = new Intent(this,AdminActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                AuthUI.getInstance().signOut(UserActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        UtilityClass.attachListener();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                UtilityClass.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }


}

