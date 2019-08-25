package com.nhigia.playerforandroidtv.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhigia.playerforandroidtv.Constant;
import com.nhigia.playerforandroidtv.R;
import com.nhigia.playerforandroidtv.adapter.PlayListAdapter;
import com.nhigia.playerforandroidtv.handle.OnVideoClickListener;
import com.nhigia.playerforandroidtv.model.VideoObject;
import com.nhigia.playerforandroidtv.service.TurnOffReceiver;
import com.nhigia.playerforandroidtv.service.TurnOnReceiver;
import com.nhigia.playerforandroidtv.util.TimerUtil;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.util.ArrayList;
import java.util.Calendar;

public class PlayerListVideoActivity extends AppCompatActivity {

    private String[] mArrayItem = {"Hẹn giờ tắt", "Hẹn giờ bật", "Hẹn giờ phát một video nào đó", "Chỉnh sửa danh sách phát"};
    private ArrayList<VideoObject> mArrayVideo;

    private UniversalVideoView mVideoPlayer;
    private UniversalMediaController mController;
    private FrameLayout mContainer;
    private BottomSheetBehavior mBottomSheet;
    private LinearLayout mBottomSheetLayout;
    private RecyclerView mRecyclerPlayList;
    private PlayListAdapter mPlayListAdapter;

    private int mCurrentPostion = 0;

    private String TYPE_PLAY = Constant.PLAY_NEXT;

    public int getmCurrentPostion() {
        return mCurrentPostion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_video);

        initIntentData();
        initView();
        initUI();
        initEvent();
    }

    private void initIntentData() {
        if (getIntent().getStringExtra(Constant.PLAY_TYPE) != null) {
            TYPE_PLAY = getIntent().getStringExtra(Constant.PLAY_TYPE);
        }
    }

    private void initUI() {
        TextView mError = new TextView(this);
        mError.setText("エラーが発生しました");
        mError.setTextColor(Color.YELLOW);

        ProgressBar mLoading = new ProgressBar(this);

        mController.setOnErrorView(mError);
        mController.setOnLoadingView(mLoading);

        if (TYPE_PLAY.equals(Constant.PLAY_NEXT)) {
            mRecyclerPlayList.setHasFixedSize(true);
            mRecyclerPlayList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mPlayListAdapter = new PlayListAdapter(mArrayVideo, this);
            mRecyclerPlayList.setAdapter(mPlayListAdapter);
        } else {
            mBottomSheetLayout.setVisibility(View.GONE);
        }
    }

    private void playVideo(int position) {
        if (TYPE_PLAY.equals(Constant.PLAY_NEXT)) {
            mPlayListAdapter.notifyDataSetChanged();
            mController.setTitle(mArrayVideo.get(position).getmVideoName());
            mVideoPlayer.setVideoPath(mArrayVideo.get(position).getmVideoPath());
            mVideoPlayer.start();
        }
    }

    private void playVideoRepeat() {
        if (TYPE_PLAY.equals(Constant.PLAY_REPEAT)) {
            VideoObject videoObject = TimerUtil.getVideoTimer(getApplicationContext());

            mController.setTitle(videoObject.getmVideoName());
            mVideoPlayer.setVideoPath(videoObject.getmVideoPath());
            mVideoPlayer.start();
        }
    }

    private void initEvent() {
        if (TYPE_PLAY.equals(Constant.PLAY_NEXT)) {
            initArrayVideo();
            mPlayListAdapter.setListener(new OnVideoClickListener() {
                @Override
                public void onVideoClick(int position) {
                    playVideo(position);
                    mCurrentPostion = position;
                }
            });
        }
        mVideoPlayer.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
            @Override
            public void onScaleChange(boolean isFullscreen) {
                if (isFullscreen) {
                    ViewGroup.LayoutParams layoutParams = mContainer.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mContainer.setLayoutParams(layoutParams);
                } else {
                    ViewGroup.LayoutParams layoutParams = mContainer.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mContainer.setLayoutParams(layoutParams);

                    ViewGroup.LayoutParams layoutParams1 = mVideoPlayer.getLayoutParams();
                    mController.setLayoutParams(layoutParams1);
                }
            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) {
            }

            @Override
            public void onStart(MediaPlayer mediaPlayer) {
            }

            @Override
            public void onBufferingStart(MediaPlayer mediaPlayer) {
            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {
            }
        });
        mVideoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (TYPE_PLAY.equals(Constant.PLAY_NEXT)) {
                    mCurrentPostion++;
                    if (mCurrentPostion == mArrayVideo.size()) {
                        mCurrentPostion = 0;
                        playVideo(mCurrentPostion);
                    } else {
                        playVideo(mCurrentPostion);
                    }
                } else {
                    playVideoRepeat();
                }
            }
        });

    }

    private void requestWriteSettingsPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        PackageManager packageManager = getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Can not show permission request dialog", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        if (TYPE_PLAY.equals(Constant.PLAY_NEXT)) {
            initArrayVideo();
        }
        super.onResume();
    }

    private void initArrayVideo() {
        FirebaseDatabase.getInstance().getReference().child("Video").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mArrayVideo.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mArrayVideo.add(snapshot.getValue(VideoObject.class));
                        mPlayListAdapter.notifyItemInserted(mArrayVideo.size() - 1);
                    }
                    playVideo(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void initView() {
        mVideoPlayer = findViewById(R.id.mVideoPlayer);
        mController = findViewById(R.id.mController);
        mContainer = findViewById(R.id.mContainer);
        mBottomSheetLayout = findViewById(R.id.mBottomSheetLayout);
        mRecyclerPlayList = findViewById(R.id.mRecyclerPlayList);

        mVideoPlayer.setMediaController(mController);
        mBottomSheet = BottomSheetBehavior.from(mBottomSheetLayout);

        mArrayVideo = new ArrayList<>();
    }

    public void SetOff() {
        requestWriteSettingsPermission();
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, min);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), TurnOffReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), alarmIntent);
                } else {
                    alarmManager
                            .set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), alarmIntent);
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Hẹn giờ tắt");
        timePickerDialog.show();
    }

    public void SetOn() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, min);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), TurnOnReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), alarmIntent);
                } else {
                    alarmManager
                            .set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), alarmIntent);
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Hẹn giờ bật");
        timePickerDialog.show();
    }

    public void MoreAction(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Tùy Chọn Cài Đặt")
                .setItems(mArrayItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                SetOff();
                                break;
                            case 1:
                                SetOn();
                                break;
                            case 2:
                                startActivity(new Intent(getApplicationContext(), SetupPlayerVideoRepeatActivity.class));
                                break;
                            case 3:
                                startActivity(new Intent(getApplicationContext(), EditPlayListActivity.class));
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    @Override
    protected void onPause() {
        if (mVideoPlayer.canPause()) {
            mVideoPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onRestart() {
        mVideoPlayer.resume();
        super.onRestart();
    }
}
