package com.nhigia.playerforandroidtv.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.nhigia.playerforandroidtv.model.VideoObject;

public class TimerUtil {
    public static void setVideoTimer(VideoObject video, Context context){
        SharedPreferences.Editor preferences = context.getSharedPreferences("Video",Context.MODE_PRIVATE).edit();
        preferences.putString("VIDEO_ID",video.getmVideoID());
        preferences.putString("VIDEO_NAME",video.getmVideoName());
        preferences.putString("VIDEO_PATH",video.getmVideoPath());
        preferences.apply();
    }

    public static VideoObject getVideoTimer(Context context){
        SharedPreferences preferences = context.getSharedPreferences("Video",Context.MODE_PRIVATE);
        VideoObject videoObject = new VideoObject();
        videoObject.setmVideoID(preferences.getString("VIDEO_ID",""));
        videoObject.setmVideoName(preferences.getString("VIDEO_NAME",""));
        videoObject.setmVideoPath(preferences.getString("VIDEO_PATH",""));
        return videoObject;
    }
}
