package com.nhigia.playerforandroidtv.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhigia.playerforandroidtv.R;
import com.nhigia.playerforandroidtv.model.VideoObject;

import java.util.LinkedList;

public class PlayListEditorAdapter extends RecyclerView.Adapter<PlayListEditorAdapter.ViewHolder> {

    private LinkedList<VideoObject> mArrayVideo;
    private Context mContext;

    public PlayListEditorAdapter(LinkedList<VideoObject> mArrayVideo, Context mContext) {
        this.mArrayVideo = mArrayVideo;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_playlist_editor,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mName.setText(mArrayVideo.get(position).getmVideoName());
        Glide.with(mContext).asBitmap().load(mArrayVideo.get(position).getmVideoPath()).into(holder.mIcon);
        holder.mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Xóa Video Này Ra Khỏi PlayList ?")
                        .setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child("Video").child(mArrayVideo.get(holder.getAdapterPosition()).getmVideoID()).setValue(null, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError==null){
                                            Toast.makeText(mContext, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                            mArrayVideo.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                        }else {
                                            Toast.makeText(mContext, "Xóa thất bại ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setPositiveButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayVideo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mIcon,mClear;
        TextView mName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIcon = itemView.findViewById(R.id.mIcon);
            mName = itemView.findViewById(R.id.mName);
            mClear = itemView.findViewById(R.id.mClear);
        }
    }
}
