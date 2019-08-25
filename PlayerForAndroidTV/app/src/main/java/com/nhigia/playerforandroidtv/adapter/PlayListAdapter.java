package com.nhigia.playerforandroidtv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nhigia.playerforandroidtv.R;
import com.nhigia.playerforandroidtv.handle.OnVideoClickListener;
import com.nhigia.playerforandroidtv.model.VideoObject;
import com.nhigia.playerforandroidtv.view.PlayerListVideoActivity;

import java.util.ArrayList;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    private ArrayList<VideoObject> mArrayVideo;
    private Context mContext;
    private OnVideoClickListener listener;

    private int TYPE_VIDEO = 101;
    private int TYPE_VIDEO_PLAYING = 102;

    public PlayListAdapter(ArrayList<VideoObject> mArrayVideo, Context mContext) {
        this.mArrayVideo = mArrayVideo;
        this.mContext = mContext;
    }

    public void setListener(OnVideoClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (((PlayerListVideoActivity)mContext).getmCurrentPostion() == position){
            return TYPE_VIDEO_PLAYING;
        }else {
            return TYPE_VIDEO;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_VIDEO){
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_playlist,null));
        }else {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_playlist_playing,null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mArrayVideo.get(position).getmVideoPath())
                .into(holder.mIcon);
        holder.mName.setText(mArrayVideo.get(position).getmVideoName());
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onVideoClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayVideo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mIcon;
        TextView mName;
        LinearLayout mContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIcon = itemView.findViewById(R.id.mIcon);
            mName = itemView.findViewById(R.id.mName);
            mContainer = itemView.findViewById(R.id.mContainer);
        }
    }
}
