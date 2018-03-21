package com.lmb_europa.campscoutserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.ULocale;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lmb_europa.campscoutserver.Common.Common;
import com.lmb_europa.campscoutserver.Interface.ItemClickListener;
import com.lmb_europa.campscoutserver.Model.Reservation;
import com.lmb_europa.campscoutserver.Model.Spots;
import com.lmb_europa.campscoutserver.Service.ListenOrder;
import com.lmb_europa.campscoutserver.ViewHolder.SpotsViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullName;
    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference spots;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Spots,SpotsViewHolder> adapter;

    //View
    RecyclerView recycler_spots;
    RecyclerView.LayoutManager layoutManager;

    //Add new spot
    MaterialEditText edtName, edtDescription, edtPrice,edtDiscount;
    FButton btnUpload, btnSelect, btnOK;

    Spots newSpot;

    Uri saveUri;


    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Camp Management");
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        spots = database.getReference("Spots");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        //Init View
        recycler_spots = (RecyclerView)findViewById(R.id.recycler_menu);
        recycler_spots.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_spots.setLayoutManager(layoutManager);

        loadSpots();

        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add new spot");
        alertDialog.setMessage("Please fill all information");
        alertDialog.setCancelable(true);

        LayoutInflater inflater = this.getLayoutInflater();
        View add_spots_layout = inflater.inflate(R.layout.add_new_spot, null);

        edtName = (MaterialEditText) add_spots_layout.findViewById(R.id.edtName);
        edtDescription = (MaterialEditText) add_spots_layout.findViewById(R.id.edtDescription);
        edtPrice = (MaterialEditText) add_spots_layout.findViewById(R.id.edtPrice);
        edtDiscount = (MaterialEditText) add_spots_layout.findViewById(R.id.edtDiscount);

        btnSelect = (FButton) add_spots_layout.findViewById(R.id.btnSelect);
        btnUpload = (FButton) add_spots_layout.findViewById(R.id.btnUpload);
        btnOK = (FButton) add_spots_layout.findViewById(R.id.btnOK);

        alertDialog.setView(add_spots_layout);
        alertDialog.setIcon(R.drawable.ic_cart);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(); //for user to select image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newSpot != null)
                {
                    spots.push().setValue(newSpot);
                    //Snackbar.make(drawer, "New spot " + newSpot.getNumber() + " was added", Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(Home.this, "New spot " + newSpot.getNumber() + " was added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (newSpot != null)
                {
                    spots.push().setValue(newSpot);
                    Snackbar.make(drawer, "New spot " + newSpot.getNumber() + " was added", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });*/
        alertDialog.show();
    }

    private void uploadImage() {
        if (saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newSpot = new Spots();
                                    newSpot.setNumber(edtName.getText().toString());
                                    newSpot.setDetails(edtDescription.getText().toString());
                                    newSpot.setPrice(edtPrice.getText().toString());
                                    newSpot.setDiscount(edtDiscount.getText().toString());
                                    newSpot.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Selected!");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select your picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void loadSpots() {
        adapter = new FirebaseRecyclerAdapter<Spots, SpotsViewHolder>(Spots.class, R.layout.spot, SpotsViewHolder.class, spots) {
            @Override
            protected void populateViewHolder(SpotsViewHolder viewHolder, Spots model, int position) {
                viewHolder.txtSpotName.setText(model.getNumber());
                Picasso.with(Home.this).load(model.getImage()).into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recycler_spots.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            Intent orders = new Intent(Home.this, ReservationStatus.class);
            startActivity(orders);
        }/* else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Update / Delete

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteSpot(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteSpot(String key) {
        spots.child(key).removeValue();
        Snackbar.make(drawer, "Spot was deleted", Snackbar.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(final String key, final Spots item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update spot");
        alertDialog.setMessage("Please fill all information");
        alertDialog.setCancelable(true);

        LayoutInflater inflater = this.getLayoutInflater();
        View add_spots_layout = inflater.inflate(R.layout.add_new_spot, null);

        edtName = (MaterialEditText) add_spots_layout.findViewById(R.id.edtName);
        edtDescription = (MaterialEditText) add_spots_layout.findViewById(R.id.edtDescription);
        edtPrice = (MaterialEditText) add_spots_layout.findViewById(R.id.edtPrice);
        edtDiscount = (MaterialEditText) add_spots_layout.findViewById(R.id.edtDiscount);

        edtName.setText(item.getNumber());
        edtDescription.setText(item.getDetails());
        edtPrice.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());

        btnSelect = (FButton) add_spots_layout.findViewById(R.id.btnSelect);
        btnUpload = (FButton) add_spots_layout.findViewById(R.id.btnUpload);
        btnOK = (FButton) add_spots_layout.findViewById(R.id.btnOK);

        alertDialog.setView(add_spots_layout);
        alertDialog.setIcon(R.drawable.ic_cart);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(); //for user to select image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setNumber(edtName.getText().toString());
                item.setDetails(edtDescription.getText().toString());
                item.setPrice(edtPrice.getText().toString());
                item.setDiscount(edtDiscount.getText().toString());
                spots.child(key).setValue(item);
            }
        });


        alertDialog.show();
    }

    private void changeImage(final Spots item) {
        if (saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }
    }
}
