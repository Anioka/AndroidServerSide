package com.lmb_europa.campscoutserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmb_europa.campscoutserver.Common.Common;
import com.lmb_europa.campscoutserver.Model.User;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn;
    EditText edtPhone, edtPassword;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (FButton)findViewById(R.id.btnSignIn);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtPhone = (EditText)findViewById(R.id.edtPhone);

        //Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(edtPhone.getText().toString(), edtPassword.getText().toString());
            }
        });

        //Check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd != null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
                login(user, pwd);
        }
    }

    /*private void signInUser(String phone, String password) {
        final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists())
                {
                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsStaff()))
                    {
                        if (user.getPassword().equals(localPassword))
                        {
                            Intent login = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(login);
                            finish();
                        }
                        else Toast.makeText(MainActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(MainActivity.this, "Wrong account", Toast.LENGTH_SHORT).show();
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

    private void login(final String phone, final String pwd) {

        if (Common.isConnectingToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please wait...");
            mDialog.show();

            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists()) {
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)) {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                            Common.currentUser = user;
                            Intent i1 = new Intent(MainActivity.this, Home.class);
                            startActivity(i1);
                            finish();
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
