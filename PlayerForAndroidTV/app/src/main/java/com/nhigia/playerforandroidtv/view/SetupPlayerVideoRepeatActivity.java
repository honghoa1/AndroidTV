package com.nhigia.playerforandroidtv.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.nhigia.playerforandroidtv.R;
import com.nhigia.playerforandroidtv.adapter.PlayListCheckedAdapter;
import com.nhigia.playerforandroidtv.model.VideoObject;
import com.nhigia.playerforandroidtv.service.TimeOnRepeatReceiver;
import com.nhigia.playerforandroidtv.service.TurnOnReceiver;
import com.nhigia.playerforandroidtv.util.TimerUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class SetupPlayerVideoRepeatActivity extends AppCompatActivity {

    private RecyclerView recyclerRepeatVideo;
    private PlayListCheckedAdapter mPlayListCheckedAdapter;
    private ArrayList<VideoObject> mArrayVideo;
    private Toolbar toolbar;

    private Calendar mCalendarTimeOn,mCalendarTimeOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_player_video_repeat);

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
                    mPlayListCheckedAdapter.notifyItemInserted(mArrayVideo.size()-1);
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        recyclerRepeatVideo.setHasFixedSize(true);
        recyclerRepeatVideo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mPlayListCheckedAdapter = new PlayListCheckedAdapter(mArrayVideo,getApplicationContext());
        recyclerRepeatVideo.setAdapter(mPlayListCheckedAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        recyclerRepeatVideo = findViewById(R.id.mRecyclerPlayList);
        toolbar = findViewById(R.id.toolbar);

        mArrayVideo = new ArrayList<>();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checked,menu);
        return true;
    }

    public void AddVideo(MenuItem item){
        if (mPlayListCheckedAdapter.getVideoChecked()!=null){
            setTimeOn();
        }
    }
    private void setTimeOn(){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                mCalendarTimeOn = Calendar.getInstance();
                mCalendarTimeOn.set(Calendar.HOUR,hour);
                mCalendarTimeOn.set(Calendar.MINUTE,min);
                setTimeOff();
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
        timePickerDialog.setTitle("Hẹn thời gian bật");
        timePickerDialog.show();
    }
    private void setTimeOff(){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                mCalendarTimeOff = Calendar.getInstance();
                mCalendarTimeOff.set(Calendar.HOUR,hour);
                mCalendarTimeOff.set(Calendar.MINUTE,min);

                // Set Time On
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), TimeOnRepeatReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                TimerUtil.setVideoTimer(mPlayListCheckedAdapter.getVideoChecked(),getApplicationContext());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendarTimeOn.getTimeInMillis(), alarmIntent);
                } else {
                    alarmManager
                            .set(AlarmManager.RTC_WAKEUP, mCalendarTimeOn.getTimeInMillis(), alarmIntent);
                }

                // Set Time Off

                AlarmManager alarmManager1 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent1 = new Intent(getApplicationContext(), TurnOnReceiver.class);
                PendingIntent alarmIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager1.setExact(AlarmManager.RTC_WAKEUP, mCalendarTimeOff.getTimeInMillis(), alarmIntent1);
                } else {
                    alarmManager1
                            .set(AlarmManager.RTC_WAKEUP, mCalendarTimeOff.getTimeInMillis(), alarmIntent1);
                }

                Toast.makeText(getApplicationContext(), "Hẹn giờ thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
        timePickerDialog.setTitle("Hẹn thời gian tắt");
        timePickerDialog.show();
    }
}
