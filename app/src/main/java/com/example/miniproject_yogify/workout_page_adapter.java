package com.example.miniproject_yogify;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class workout_page_adapter extends RecyclerView.Adapter<workout_page_adapter.portfoliopage_viewholder> {


    ArrayList<workout_data> workoutData;
    private Context mContext;
    private portfoliopage_viewholder.onClickRecyclerListen mOnClickRecyclerListen;
    private static DecimalFormat df2 = new DecimalFormat("#.##");


    public workout_page_adapter(Context mContext, ArrayList<workout_data> workoutData, portfoliopage_viewholder.onClickRecyclerListen OnClickRecyclerListen) {
        this.workoutData = workoutData;
        this.mContext = mContext;
        this.mOnClickRecyclerListen=OnClickRecyclerListen;
    }

    @NonNull
    @Override
    public portfoliopage_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.workout_boxes,parent,false);
        return new portfoliopage_viewholder(view,mOnClickRecyclerListen);
    }

    @Override
    public void onBindViewHolder(@NonNull portfoliopage_viewholder holder, int position) {
        holder.name.setText(workoutData.get(position).getName().toUpperCase(Locale.ROOT));
        holder.time.setText(Double.toString(workoutData.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return workoutData.size();
    }

    public static class portfoliopage_viewholder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name,time;
        public onClickRecyclerListen mListener;
        public portfoliopage_viewholder(@NonNull View itemView, onClickRecyclerListen listener) {
            super(itemView);
            mListener=listener;
            name=itemView.findViewById(R.id.txt_workoutname);
            time = itemView.findViewById(R.id.txt_workouttime);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClickRecycler(getAdapterPosition(),v);
        }

        public static interface onClickRecyclerListen {
            void onClickRecycler(int position,View v);

        }

    }

}
