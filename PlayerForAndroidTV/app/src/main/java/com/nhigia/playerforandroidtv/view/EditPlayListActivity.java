package com.nhigia.playerforandroidtv.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.nhigia.playerforandroidtv.R;
import com.nhigia.playerforandroidtv.adapter.PlayListEditorAdapter;
import com.nhigia.playerforandroidtv.model.VideoObject;
import com.nhigia.playerforandroidtv.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;

public class EditPlayListActivity extends AppCompatActivity {

    private LinkedList<VideoObject> mArrayVideo;
    private RecyclerView mRecyclerPlayList;
    private PlayListEditorAdapter mAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_play_list);

        initView();
        initUI();
        initEvent();
    }

    private void initEvent() {
        FirebaseDatabase.getInstance().getReference().child("Video").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    mArrayVideo.add(dataSnapshot.getValue(VideoObject.class));
                    mAdapter.notifyItemInserted(mArrayVideo.size()-1);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void initUI() {
        mRecyclerPlayList.setHasFixedSize(true);
        mRecyclerPlayList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new PlayListEditorAdapter(mArrayVideo,this);
        mRecyclerPlayList.setAdapter(mAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
    }

    private void initView() {
        mRecyclerPlayList = findViewById(R.id.mRecyclerPlayList);
        toolbar = findViewById(R.id.toolbar);

        mArrayVideo = new LinkedList<>();
    }

    public void AddVideo(MenuItem item){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),123);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlist,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {

        if (requestCode==123 && resultCode==RESULT_OK && data!=null){

            final EditText mNameInput = new EditText(this);

            new AlertDialog.Builder(this)
                    .setTitle("Nhập tên video")
                    .setView(mNameInput)
                    .setCancelable(false)
                    .setNegativeButton("Tải lên", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            uploadVideo(mNameInput.getText().toString(),data.getData());
                        }
                    })
                    .setPositiveButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadVideo(final String mName, Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang tải lên video");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String mPath = FileUtil.getPath(getApplicationContext(),data);
        final long Key = System.currentTimeMillis();
        final long total = FileUtil.getFolderSize(new File(mPath));

        InputStream mInputStream = null;
        try {
            mInputStream = new FileInputStream(mPath);
            FirebaseStorage.getInstance().getReference().child("Video").child(System.currentTimeMillis()+"").putStream(mInputStream)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseDatabase.getInstance().getReference().child("Video").child(Key+"").setValue(new VideoObject(Key+"",taskSnapshot.getDownloadUrl().toString(),mName));
                            progressDialog.cancel();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("zzzzzzzzzzzzzz",e.toString());
                            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage(FileUtil.formatFileSize(taskSnapshot.getBytesTransferred())+"/"+FileUtil.formatFileSize(total)+"          "+((int) (taskSnapshot.getBytesTransferred()*100/total)+"%"));
                        }
                    });

        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Không thể tải lên tập tin này", Toast.LENGTH_SHORT).show();
            progressDialog.cancel();
            Log.d("zzzzzzzzzzzzzz",e.toString());
        }
    }
}
