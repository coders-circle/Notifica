package com.lipi.notifica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.PClass;
import com.lipi.notifica.database.Period;
import com.lipi.notifica.database.Profile;
import com.lipi.notifica.database.Routine;
import com.lipi.notifica.database.Subject;
import com.lipi.notifica.database.Teacher;
import com.lipi.notifica.database.User;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Subject> mSubjects;
    private List<Teacher> mTeachers;
    private PClass mClass;
    private DbHelper mDbHelper;

    public ClassAdapter(Context context, PClass pClass) {
        mContext = context;
        mClass = pClass;

        mDbHelper = new DbHelper(context);

        // Get subjects and teachers list

        mSubjects = new ArrayList<>();
        mTeachers = new ArrayList<>();

        List<Long> sids = new ArrayList<>();
        List<Long> tids = new ArrayList<>();

        List<Routine> routines = mClass.getRoutines(mDbHelper);
        for (Routine r: routines) {
            List<Period> periods = r.getPeriods(mDbHelper);
            for (Period p: periods) {

                if (!sids.contains(p.subject)) {
                    sids.add(p.subject);
                    mSubjects.add(p.getSubject(mDbHelper));
                }

                List<Teacher> teachers = p.getTeachers(mDbHelper);
                for (Teacher t: teachers)
                    if (!tids.contains(t._id)) {
                        tids.add(t._id);
                        mTeachers.add(t);
                    }
            }
        }

    }

    @Override
    public int getItemCount() {
        return mSubjects.size() + 1 + mTeachers.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == mSubjects.size()+1)
            return 0;
        else
            return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return  new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_item_header, parent, false
            ));
        else
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_profile_item, parent, false
            ));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position == 0) {
            HeaderViewHolder holder = (HeaderViewHolder)viewHolder;
            holder.header.setText("Subjects");
        }
        else if (position-1 < mSubjects.size()) {
            Subject subject = mSubjects.get(position - 1);

            Utilities.fillProfileView(((ViewHolder) viewHolder).view,
                    Color.parseColor(subject.color),
                    null, subject.name, null, null, subject.getShortName());
        }

        else if (position-1-mSubjects.size() == 0) {
            HeaderViewHolder holder = (HeaderViewHolder)viewHolder;
            holder.header.setText("Teachers");
        }
        else if (position-1-mSubjects.size()-1 < mTeachers.size()) {
            Teacher teacher = mTeachers.get(position - 1 - mSubjects.size() - 1);
            Bitmap avatar = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.ic_default_avatar);

            User user = teacher.getUser(mDbHelper);
            if (user != null) {
                Profile profile = user.getProfile(mDbHelper);
                if (profile != null)
                    avatar = profile.getAvatar();
            }

            Utilities.fillProfileView(((ViewHolder) viewHolder).view,
                    Utilities.returnColor(teacher._id),
                    avatar, teacher.getUsername(mDbHelper), null, null, null);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        protected TextView header;
        public HeaderViewHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView.findViewById(R.id.header);
        }
    }
}
