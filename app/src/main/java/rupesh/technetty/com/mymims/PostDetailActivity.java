package rupesh.technetty.com.mymims;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    //content views
    TextView mTitleTv, mDetailsTv;
    ImageView mImageView;
    Bitmap bitmap;
    // action views
    Button mSaveButton, mShareButton, mWallButton;
    //consstent variable
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide(); // hide the title bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Post Details");
        //sset back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
//initialization...
        mTitleTv = findViewById(R.id.titleTv);
        mDetailsTv = findViewById(R.id.descriptionTv);
        mImageView = findViewById(R.id.imageView);
        mSaveButton = findViewById(R.id.saveBtn);
        mShareButton = findViewById(R.id.shareBtn);
        mWallButton = findViewById(R.id.wallBtn);

        //get data from intent
        // byte[] bytes=getIntent().getByteArrayExtra("image");
        // Bitmap bmp=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        String title = getIntent().getStringExtra("title");
        String image = getIntent().getStringExtra("image");
        String desc = getIntent().getStringExtra("description");
        //set title image and description
        mTitleTv.setText(title);
        mDetailsTv.setText(desc);
        // mImageView.setImageBitmap(bmp);
        Picasso.get().load(image).into(mImageView);

        //get image from imageview as bitmap
        bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

        //save button click listener
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check Android OS is >= 6.0 Marshmallow(M=23) for permition to saving image in storege
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        String[] permition = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //SHOW POPUP MESSSAGE, sending for requet permition
                        requestPermissions(permition, WRITE_EXTERNAL_STORAGE_CODE);
                    } else {
                        //PERMITION ALLREEADY GRANTED save image
                        saveImage();
                    }
                } else {
                    //System Android OS iss < marsmallow ,so don't need permition
                    saveImage();
                }

            }
        });
        //share button click listener
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });
        //wallpaper button click listener
        mWallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImgWallPager();

            }
        });

    }

    //save image fun by rupeh kumar
    public void saveImage() {
        //get image from imageview as bitmap
        bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        //locate external storage path
        File path = Environment.getExternalStorageDirectory();
        //create folder "MyMims" in storage
        File dir = new File(path + "/MyMims");
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
//image name
        String imageName = timeStamp + ".PNG";
        File file = new File(dir, imageName);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "Image saved to\n" + dir, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Image not saved\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    //sharing image
    private void shareImage() {
        try {
            //get image from imageview as bitmap
            bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
            String s = mTitleTv.getText().toString() + "\n" + mDetailsTv.getText().toString();
            File file = new File(getExternalCacheDir(), "myimgsample.png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            file.setReadable(true, false);
            //creat an intent sshare content
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, s);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share Via"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "not shared\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //sset wallpaper
    private void setImgWallPager() {
        //get image from imageview as bitmap
        bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setBitmap(bitmap);
            Toast.makeText(this, "WallPaper Set..", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "not set..\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //using to check permition result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE: {
                //if requet code iss cancelled the ressult array are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //user allow at the time of saving image,now save the image
                    saveImage();
                } else {
                    //user not allow permition at the time of saving image
                    Toast.makeText(this, "enable permition to save image", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    //on backe presss
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
