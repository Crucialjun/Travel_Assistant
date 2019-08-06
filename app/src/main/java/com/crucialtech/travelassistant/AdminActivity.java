package com.crucialtech.travelassistant;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AdminActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private TextInputEditText mDealName;
    private TextInputEditText mDealPrice;
    private TextInputEditText mDealDescription;
    private TravelDeal deal;
    private Button mBtnImageUpload;
    private ImageView mDealImage;
    private static final int REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //UtilityClass.openFirebaseReference("traveldeals",this);
        mFirebaseDatabase = UtilityClass.mFirebaseDatabase;
        mDatabaseReference = UtilityClass.mDatabaseReference;


        mDealName = findViewById(R.id.deal_name);
        mDealPrice = findViewById(R.id.deal_price);
        mDealDescription = findViewById(R.id.deal_description);
        mDealImage = findViewById(R.id.deal_image);
        mBtnImageUpload = findViewById(R.id.btn_image_upload);
        mBtnImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"Select Image"), REQUEST_CODE);
            }
        });


        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }

        this.deal = deal;
        mDealName.setText(deal.getTitle());
        mDealDescription.setText(deal.getDescription());
        mDealPrice.setText(deal.getPrice());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        menuInflater.inflate(R.menu.admin_activity_menu, menu);
        return true;
    }

    private void saveDealToDatabase() {
        deal.setTitle(mDealName.getText().toString());
        deal.setDescription(mDealDescription.getText().toString());
        deal.setPrice(mDealPrice.getText().toString());

        if (deal.getId() == null) {
            mDatabaseReference.push().setValue(deal);
        } else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }

    }

    private void deleteDealFromDatabase() {
        if (deal == null) {
            Toast.makeText(this, "You can't delete a non-saved deal", Toast.LENGTH_LONG).show();
        } else {
            mDatabaseReference.child(deal.getId()).removeValue();
        }
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            assert imageUri != null;
            final StorageReference ref = UtilityClass.mStorageReference.child(Objects.requireNonNull(imageUri.getLastPathSegment()));
            ref.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return ref.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String uri = Objects.requireNonNull(task.getResult()).toString();
                        String finalUri = uri.replace("\"","");
                        String pictureName = ref.getName();
                        deal.setImageName(pictureName);
                        deal.setImageUrl(finalUri);
                        showImage(uri);
                    } else {
                        Toast.makeText(AdminActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showImage(String uri) {
        if(uri != null && !uri.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get().load(Uri.parse(uri)).resize(width,width * 2/3).centerCrop().into(mDealImage);
        }
    }
}
