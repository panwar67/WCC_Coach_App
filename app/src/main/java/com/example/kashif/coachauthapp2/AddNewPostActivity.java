package com.example.kashif.coachauthapp2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewPostActivity extends AppCompatActivity {


    private EditText blog_title_et;
    private EditText blog_description_et;
    private ImageView blog_image_iv;
    private Button blog_submit_btn;

    private Uri uri_image_final;

    private static final int GALLERY_REQUEST = 1;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;


    private Uri imageUri;
    Uri imageURI;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    private Map<String, Object> posts_detail_hash_map;

    File myAppFolder;
    String filePath;
    private String stringUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);


        //show the progress dialog on opening of activity
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading New Post...");


        // setting the title of action bar
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // creating the app folder
        createAppFolder();


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        blog_title_et = (EditText) findViewById(R.id.blog_title_tv);
        blog_description_et = (EditText) findViewById(R.id.blog_description_tv);

        blog_image_iv = (ImageView) findViewById(R.id.blog_image_view);

        blog_submit_btn = (Button) findViewById(R.id.submit_post_btn);

        blog_image_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                selectImage();


            }
        });


        blog_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posting();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case com.example.kashif.coachauthapp2.Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createAppFolder();
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    public void posting() {


        final String post_title = blog_title_et.getText().toString().trim();
        final String post_description = blog_description_et.getText().toString().trim();

        if (!TextUtils.isEmpty(post_title) && !TextUtils.isEmpty(post_description) && uri_image_final != null) {

            showProgressDialog();
            StorageReference filePathNew = storageReference.child("PostImage").child(uri_image_final.getLastPathSegment());

            filePathNew.putFile(Uri.fromFile(new File(filePath))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    String[] metadata = formattedDate.split(" ");
                    String date = metadata[0];
                    String time = metadata[1];
                    long timestamp = taskSnapshot.getMetadata().getCreationTimeMillis();

                    long newTime = -1 * timestamp;
                    String string_timestamp = String.valueOf(newTime);


                    posts_detail_hash_map = new HashMap<>();
                    posts_detail_hash_map.put("Post_Title", post_title);
                    posts_detail_hash_map.put("Post_Description", post_description);
                    posts_detail_hash_map.put("Post_Image", downloadUri.toString());
                    posts_detail_hash_map.put("Post_Time", time);
                    posts_detail_hash_map.put("Post_date", date);
                    posts_detail_hash_map.put("Post_timestamp", newTime);
                    databaseReference.child(string_timestamp).setValue(posts_detail_hash_map);

                    hideProgressDialog();
                    Toast.makeText(AddNewPostActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                    deleteDir(myAppFolder);
                    finish();


                }
            });
        }

            else if (!TextUtils.isEmpty(post_title) && !TextUtils.isEmpty(post_description) && uri_image_final == null){

            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            String[] metadata = formattedDate.split(" ");
            String date = metadata[0];
            String time = metadata[1];
            long timestamp = System.currentTimeMillis();

            long newTime = -1 * timestamp;
            String string_timestamp = String.valueOf(newTime);


            posts_detail_hash_map = new HashMap<>();
            posts_detail_hash_map.put("Post_Title", post_title);
            posts_detail_hash_map.put("Post_Description", post_description);
            posts_detail_hash_map.put("Post_Image", "NULL");
            posts_detail_hash_map.put("Post_Time", time);
            posts_detail_hash_map.put("Post_date", date);
            posts_detail_hash_map.put("Post_timestamp", newTime);
            databaseReference.child(string_timestamp).setValue(posts_detail_hash_map);

            Toast.makeText(AddNewPostActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
            deleteDir(myAppFolder);
            finish();
        }

            else{
                Toast.makeText(AddNewPostActivity.this, "Please Fill Title & Description both", Toast.LENGTH_SHORT).show();
            }
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imageUri = data.getData();

            stringUri = imageUri.toString();

            filePath = SiliCompressor.with(getApplicationContext()).compress(stringUri, myAppFolder);
            uri_image_final = Uri.parse(filePath);

            blog_image_iv.setImageURI(uri_image_final);
            String uri = uri_image_final.toString();

        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            stringUri = imageURI.toString();

            filePath = SiliCompressor.with(getApplicationContext()).compress(stringUri, myAppFolder);
            uri_image_final = Uri.parse(filePath);

            blog_image_iv.setImageURI(uri_image_final);
        }



    }
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewPostActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= com.example.kashif.coachauthapp2.Utility.checkPermission(AddNewPostActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result) {
                        cameraIntent();
                    }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result) {
                        galleryIntent();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }


    private void galleryIntent(){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, GALLERY_REQUEST);


    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageURI = Uri.fromFile(new File(Environment.getExternalStorageDirectory()  + File.separator
                + getString(R.string.app_name),
                String.valueOf(System.currentTimeMillis() + ".jpg")));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    // method to create the app folder
    public void createAppFolder(){

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

            myAppFolder = new File(Environment.getExternalStorageDirectory() + File.separator
                    + getString(R.string.app_name));
            myAppFolder.mkdirs();
        } else {
            /* save the folder in internal memory of phone */

            myAppFolder = new File("/data/data/" + getPackageName()
                    + File.separator + getString(R.string.app_name));
            myAppFolder.mkdirs();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home : {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // method to show progress dialog while signing in
    public void showProgressDialog() {
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // method to show progress dialog
    public void hideProgressDialog() {
        progressDialog.cancel();
    }
}
