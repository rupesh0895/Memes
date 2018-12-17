package rupesh.technetty.com.mymims;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.ByteArrayOutputStream;

public class PostaListActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
RecyclerView mRecyclerView;
FirebaseDatabase mFirebaseDatabase;
DatabaseReference mRef;
LinearLayoutManager mLayoutManager; //for sorting
SharedPreferences mSharedPreferences; //for saving ssort setingss
     //load data into recycleview on start
 @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        progressDialog.setIndeterminate(true);
        //load data in ViewHolder class
        FirebaseRecyclerAdapter<Model,ViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Model, ViewHolder>(
                        Model.class,
                        R.layout.row,
                        ViewHolder.class,
                        mRef
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {
                        viewHolder.setDetails(getApplicationContext(),model.getTitle() ,model.getImage() , model.getDescription());
                    progressDialog.dismiss();
                    }
                    //this used to hold viewa on ViewHolder second setting

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder= super.onCreateViewHolder(parent,viewType );
                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView mTitleTv=view.findViewById(R.id.rTitleTv);
                                TextView mDescTv=view.findViewById(R.id.rDescriptionTv);
                                ImageView mImageView=view.findViewById(R.id.rImageView);

//                                //get data from Views static
//                                String mTitle=mTitleTv.getText().toString();
//                                String mDesc=mDescTv.getText().toString();

            // start get image into Bitmap whatever image from ImageView..........option1
//                                Drawable mDrawable=mImageView.getDrawable();
//                                if (mDrawable==null){
//                                    Toast.makeText(view.getContext(),"Wait Image is loading" , Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                                Bitmap mBitmap=((BitmapDrawable)mDrawable).getBitmap();
//                                ByteArrayOutputStream stream=new ByteArrayOutputStream();
//                                //compress Bitmap image into bytes stream
//                                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                                byte[] sbytes=stream.toByteArray();
            //end get image into Bitmap whatever image from ImageView..........option1 end
//get image option 2
                                //get data from Views Dynamic from firebase
                                String sbytes=getItem(position).getImage();
                                String mTitle=getItem(position).getTitle();
                                String mDesc=getItem(position).getDescription();

                                if (sbytes.getBytes()==null){
                                    Toast.makeText(view.getContext(),"Wait Image is loading" , Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //pass thiss detail to the PostDetailActivity activity
                                Intent intent=new Intent(view.getContext(),PostDetailActivity.class);

                                //put all data to the intent
                                intent.putExtra("image",sbytes );
                                intent.putExtra("title",mTitle );
                                intent.putExtra("description", mDesc);
                                if (sbytes.getBytes()!=null){
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                //TODO LONG ITEM CLICKED

                            }
                        });

                        return viewHolder;
                    }
                };
        //sset adaptor to recycleview
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_posta_list);
        progressDialog=new ProgressDialog(PostaListActivity.this);
        //set ActionBar
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Post List");
       mSharedPreferences=getSharedPreferences("Sortsettings",MODE_PRIVATE);
       String mSorting= mSharedPreferences.getString("Sort", "Newest");

       //is sselected newest will be default
        if (mSorting.equals("Newest")){
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means newet first
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        } else if(mSorting.equals("Oldest")){
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means oldest
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }
        //RecycleView
        mRecyclerView=findViewById(R.id.recycleView);
        // fix ssize of length
        mRecyclerView.setHasFixedSize(true);
        //set layout as a linearlayout
        //****************this line uen if you don't want to sorted data list as a default list**********************************/
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //****************this line uen if you want to sorted data list as a default list**********************************/
        mRecyclerView.setLayoutManager(mLayoutManager);
        //initilise this app to firebase
        FirebaseApp.initializeApp(this);
        //send Query to firebase
        mFirebaseDatabase =FirebaseDatabase.getInstance();
        //get databae reference on firebase
        mRef=mFirebaseDatabase.getReference("Data");
//        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        getSupportActionBar().show();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        getSupportActionBar().hide();
//                        break;
//                    default: break;
//                }
//                return false;
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu; this add item to the action bar if it present
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //call firebaseSearch(String s) method
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //call firebaseSearch(String s) method
                firebaseSearch(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    //


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        //handel when other action bar item clicked
        if (id==R.id.action_sort){
            //TODO
            showSortDialog();

            return true;
        }
        if (id==R.id.action_add){
            //TODO
           startActivity(new Intent(PostaListActivity.this,AddPostActivity.class));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//
    private void showSortDialog(){
     //options
        String[] sortOption={"Newest","Oldest"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Sort by")
                .setIcon(R.drawable.ic_action_sort)
                .setItems(sortOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //the 'which' argument countain the index poition of the elected item
                        // '0' means ""Newet" and '1' mean "oldet"
                        if (which == 0){
                        //Sorting Newest
                            //edit out sshared preference
                            SharedPreferences.Editor editor=mSharedPreferences.edit();
                            editor.putString("Sort","Newest" ); //Where 'Sort' is key & 'Newest' is value
                           editor.apply();
                           recreate(); //Retart acticity to take effect
                    }else if(which ==1){
                            //Sorting Oldest
                            //edit out sshared preference
                            SharedPreferences.Editor editor=mSharedPreferences.edit();
                            editor.putString("Sort","Oldest" ); //Where 'Sort' is key & 'Oldest' is value
                            editor.apply();
                            recreate(); //Retart acticity to take effect

                        }
                    }
                });
        builder.show();
    }
    //search Data
    private void firebaseSearch(String searchText){
     // convert searched string in to lowercasse
        String query=searchText.toLowerCase();
        //search data by its title hear title  name come from firebae databae->Data->title
       // Query fireBaseSearchQuery =mRef.orderByChild("title").startAt(searchText).endAt(searchText +"\uf8ff");
        Query fireBaseSearchQuery =mRef.orderByChild("search").startAt(query).endAt(query +"\uf8ff");

        //load data according to search in ViewHolder class
        FirebaseRecyclerAdapter<Model,ViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Model, ViewHolder>(
                        Model.class,
                        R.layout.row,
                        ViewHolder.class,
                        fireBaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {
                        viewHolder.setDetails(getApplicationContext(),model.getTitle() ,model.getImage() , model.getDescription());

                    }
                };
        //sset adaptor to recycleview
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


}
