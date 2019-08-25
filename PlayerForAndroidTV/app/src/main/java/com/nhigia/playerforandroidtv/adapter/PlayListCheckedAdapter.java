package com.nhigia.playerforandroidtv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nhigia.playerforandroidtv.R;
import com.nhigia.playerforandroidtv.model.VideoObject;

import java.util.ArrayList;

public class PlayListCheckedAdapter extends RecyclerView.Adapter<PlayListCheckedAdapter.ViewHolder> {

    private ArrayList<VideoObject> mArrayVideo;
    private Context mContext;

    public PlayListCheckedAdapter(ArrayList<VideoObject> mArrayVideo, Context mContext) {
        this.mArrayVideo = mArrayVideo;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PlayListCheckedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayListCheckedAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_playlist_checked,null));
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayListCheckedAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mArrayVideo.get(position).getmVideoPath())
                .into(holder.mIcon);
        holder.mName.setText(mArrayVideo.get(position).getmVideoName());
        holder.mRadioButton.setChecked(mArrayVideo.get(position).isChecked());
        holder.mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mArrayVideo.get(position).setChecked(b);
                    notifyItemChanged(position);
                    setOffAllChecked(position);
                }
            }
        });
    }

    private void setOffAllChecked(int position){
        for (int i=0;i<mArrayVideo.size();i++){
            if (i != position && mArrayVideo.get(i).isChecked()){
                mArrayVideo.get(i).setChecked(false);
                notifyItemChanged(i);
            }
        }
    }

    public VideoObject getVideoChecked(){
        for (int i=0; i<mArrayVideo.size();i++){
            if (mArrayVideo.get(i).isChecked()){
                return mArrayVideo.get(i);
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mArrayVideo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mIcon;
        TextView mName;
        LinearLayout mContainer;
        RadioButton mRadioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIcon = itemView.findViewById(R.id.mIcon);
            mName = itemView.findViewById(R.id.mName);
            mContainer = itemView.findViewById(R.id.mContainer);
            mRadioButton = itemView.findViewById(R.id.mRadioButton);
        }
    }
}