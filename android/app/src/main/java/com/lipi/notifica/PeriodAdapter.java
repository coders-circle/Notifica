package com.lipi.notifica;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fhx on 1/1/16.
 */
public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.PeriodViewHolder>{
    List<Period> mPeriods;

    public PeriodAdapter(List<Period> periods){
        mPeriods = periods;
    }

    @Override
    public int getItemCount() {
        return mPeriods.size();
    }

    @Override
    public PeriodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PeriodViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.layout_period, parent, false));
    }

    @Override
    public void onBindViewHolder(PeriodViewHolder holder, int position) {
        Period period = mPeriods.get(position);
        holder.subject.setText(period.subject);
        holder.remarks.setText(period.remarks);
    }

    public class PeriodViewHolder extends RecyclerView.ViewHolder{
        protected TextView subject;
        protected TextView remarks;
        public PeriodViewHolder(View v){
            super(v);
            subject = (TextView)v.findViewById(R.id.subject);
            remarks = (TextView)v.findViewById(R.id.remarks);
        }
    }
}
