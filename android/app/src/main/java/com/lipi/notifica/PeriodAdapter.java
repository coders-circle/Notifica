package com.lipi.notifica;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Period;
import com.lipi.notifica.database.Subject;

/**
 * Created by fhx on 1/1/16.
 */
public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.PeriodViewHolder>{
    private List<Period> mPeriods;
    private Context mContext;

    public PeriodAdapter(Context context, List<Period> periods){
        mPeriods = periods;
        mContext = context;
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
        Subject subject = Subject.get(Subject.class, new DbHelper(mContext), period.subject);

        holder.subject.setText(subject.name);
        holder.remarks.setText(period.remarks);
        holder.time.setText(period.getStartTime() + " - " + period.getEndTime());
    }

    public class PeriodViewHolder extends RecyclerView.ViewHolder{
        protected TextView subject;
        protected TextView remarks;
        protected TextView time;
        public PeriodViewHolder(View v){
            super(v);
            subject = (TextView)v.findViewById(R.id.subject);
            remarks = (TextView)v.findViewById(R.id.remarks);
            time = (TextView)v.findViewById(R.id.time);
        }
    }
}
