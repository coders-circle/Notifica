package com.lipi.notifica;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Period;
import com.lipi.notifica.database.Subject;
import com.lipi.notifica.database.Teacher;
import com.lipi.notifica.database.User;

public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.PeriodViewHolder>{
    private List<Period> mPeriods;
    private List<Integer> mBreaks = new ArrayList<>();
    private Context mContext;

    public PeriodAdapter(Context context, List<Period> periods){
        mPeriods = periods;
        mContext = context;

        if (mPeriods.size() > 1) {
            int cnt = 0;
            Period lastPeriod = mPeriods.get(0);
            for (int i=1; i<mPeriods.size(); ++i) {
                Period next = mPeriods.get(i);
                if (next.start_time > lastPeriod.end_time) {
                    mBreaks.add(i + cnt);
                    cnt++;
                }
                lastPeriod = next;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPeriods.size() + mBreaks.size();
    }

    @Override
    public PeriodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PeriodViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.layout_period, parent, false));
    }

    @Override
    public void onBindViewHolder(PeriodViewHolder holder, int position) {
        // If break then show "Break"
        if (mBreaks.contains(position)) {
            holder.subject.setText("Break");
            holder.remarks.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.teachers.setVisibility(View.GONE);
            holder.subShortName.setVisibility(View.GONE);
            holder.subject.setGravity(Gravity.CENTER);
            return;
        }

        // Else find the period and show that
        int pPosition = position;
        for (Integer b: mBreaks) {
            if (b < position)
                pPosition--;
            else if (b > position)
                break;
        }

        Period period = mPeriods.get(pPosition);

        DbHelper helper = new DbHelper(mContext);
        Subject subject = Subject.get(Subject.class, helper, period.subject);
        List<Teacher> teachers = period.getTeachers(helper);
        String teacher_str = "";
        for(int i = 0; i < teachers.size(); i++){
            if(teachers.get(i).user != -1) {
                User usr = User.get(User.class, helper, teachers.get(i).user);
                teacher_str += usr.first_name.length() > 0? usr.first_name + " " + usr.last_name : "";
            }
            else{
                teacher_str += teachers.get(i).username;
            }
            if (i != teachers.size() - 1) {
                teacher_str += ", ";
            }
        }
        String subShortName = subject.short_name;
        if(subShortName.length() == 0){
            String[] subWords = subject.name.split(" ");
            for (String subWord : subWords) {
                subShortName += subWord.toUpperCase().charAt(0);
            }
        }

        holder.subShortName.setText(subShortName);
        holder.subShortName.setBackgroundResource(R.drawable.border_circle);
        GradientDrawable shortNameBackground = (GradientDrawable) holder.subShortName.getBackground();

        String timeText = period.getStartTime() + " - " + period.getEndTime();
        holder.time.setText(timeText);
        holder.subject.setText(subject.name);
        holder.teachers.setText(teacher_str);
        if(teacher_str.length() == 0){
            holder.teachers.setVisibility(View.GONE);
        }
        else{
            holder.teachers.setVisibility(View.VISIBLE);
        }
        holder.remarks.setText(period.remarks);
        if(period.remarks.length() == 0){
            holder.remarks.setVisibility(View.GONE);
        }
        else{
            holder.remarks.setVisibility(View.VISIBLE);
        }

        shortNameBackground.setColor(
                Color.parseColor(subject.color)
        );
    }

    public class PeriodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView subShortName;
        protected TextView time;
        protected TextView subject;
        protected TextView teachers;
        protected TextView remarks;

        public PeriodViewHolder(View v){
            super(v);
            v.setOnClickListener(this);
            subShortName = (TextView)v.findViewById(R.id.sub_shortname);
            time = (TextView)v.findViewById(R.id.time);
            subject = (TextView)v.findViewById(R.id.subject);
            teachers = (TextView)v.findViewById(R.id.teachers);
            remarks = (TextView)v.findViewById(R.id.remarks);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
