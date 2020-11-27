package com.example.androidapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {
    TextView name,email,profession,PhoneNo;
    private static int RESULT_LOAD_IMAGE = 1;
    Button button, buttonLoadImage ;
    ImageView mImageview;
    FirebaseAuth mAuth;
    FirebaseUser user=mAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_main,container,false);
        name=v.findViewById(R.id.name);
        email=v.findViewById(R.id.email);
        profession=v.findViewById(R.id.profession);
        button=v.findViewById(R.id.sign_out);
        buttonLoadImage=v.findViewById(R.id.buttonLoadPicture);
        PhoneNo=v.findViewById(R.id.phone_no);
        mImageview = (ImageView) v.findViewById(R.id.Profile_img);
        v.findViewById(R.id.buttonLoadPicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2000);
                }
                else
                {
                    startGallery();
                }
            }
        });
        return v;
    }
    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("users");
        if(mAuth.getInstance().getCurrentUser()!=null){
            String n=user.getDisplayName();
            name.setText("Username: "+n);
            String e=user.getEmail();
            email.setText("Email Id: "+e);

            DatabaseReference ProfRef=rootRef.child("Profession").child(user.getDisplayName());
            ProfRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ProfessionHelperClass p=snapshot.getValue(ProfessionHelperClass.class);
                    String pi= p.profession;
                    profession.setText("Profession: "+pi);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read Failed for  Profession!!");
                }
            });
            DatabaseReference Mob=rootRef.child("PhoneNo").child(user.getDisplayName());
            Mob.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snap) {
                    MobileHelperClass mo=snap.getValue(MobileHelperClass.class);
                    String no=mo.MobileNo+" ";
                    PhoneNo.setText("Phone Number:"+no);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read Failed for Mobile!!");
                }
            });
        }
       button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getActivity(),Login.class);
                startActivity(intent);
            }
       });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mImageview.setImageBitmap(bitmapImage);
            }
        }
    }
}
