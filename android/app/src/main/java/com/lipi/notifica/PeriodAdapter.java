package com.lipi.notifica;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Period;
import com.lipi.notifica.database.Subject;
import com.lipi.notifica.database.Teacher;
import com.lipi.notifica.database.User;

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
        shortNameBackground.setColor(returnColor(subject._id));
    }

    public static int returnColor(long id){
        int rand = ((int) id)%3;
        switch (rand){
            case 0:
                return Color.parseColor("#268b83");
            case 1:
                return Color.parseColor("#e7a403");
            default:
                return Color.parseColor("#e53935");
        }

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
