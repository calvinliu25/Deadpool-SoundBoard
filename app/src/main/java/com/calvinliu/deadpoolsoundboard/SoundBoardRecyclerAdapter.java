package com.calvinliu.deadpoolsoundboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SoundBoardRecyclerAdapter extends RecyclerView.Adapter<SoundBoardRecyclerAdapter.SoundBoardViewHolder> {

    private ArrayList<SoundObject> soundObjects;

    public SoundBoardRecyclerAdapter(ArrayList<SoundObject> soundObjects){

        this.soundObjects = soundObjects;
    }

    @NonNull
    @Override
    public SoundBoardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sound_item, null);
        return new SoundBoardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundBoardViewHolder soundBoardViewHolder, int i) {

        final SoundObject object = soundObjects.get(i);
        final Integer soundID = object.getItemId();

        soundBoardViewHolder.itemTextView.setText(object.getItemName());

        soundBoardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventHandlerClass.startMediaPlayer(v, soundID);
            }
        });

        soundBoardViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {

                EventHandlerClass.popupmanager(v, object);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return soundObjects.size();
    }

    public class SoundBoardViewHolder extends RecyclerView.ViewHolder{

        TextView itemTextView;

        public SoundBoardViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTextView = (TextView) itemView.findViewById(R.id.textViewItem);
        }
    }
}