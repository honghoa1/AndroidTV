package com.nhigia.playerforandroidtv.model;

public class VideoObject {
    private String mVideoID,mVideoPath,mVideoName;
    private boolean isChecked;

    public VideoObject(String mVideoID, String mVideoPath, String mVideoName) {
        this.mVideoID = mVideoID;
        this.mVideoPath = mVideoPath;
        this.mVideoName = mVideoName;
    }

    public VideoObject() {
    }

    public String getmVideoID() {
        return mVideoID;
    }

    public void setmVideoID(String mVideoID) {
        this.mVideoID = mVideoID;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getmVideoName() {
        return mVideoName;
    }

    public void setmVideoName(String mVideoName) {
        this.mVideoName = mVideoName;
    }

    public String getmVideoPath() {
        return mVideoPath;
    }

    public void setmVideoPath(String mVideoPath) {
        this.mVideoPath = mVideoPath;
    }
}
