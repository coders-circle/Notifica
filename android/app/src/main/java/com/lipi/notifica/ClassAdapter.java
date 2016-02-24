package com.lipi.notifica;

import android.app.ProgressDialog;
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
import com.lipi.notifica.database.Elective;
import com.lipi.notifica.database.PClass;
import com.lipi.notifica.database.Period;
import com.lipi.notifica.database.Profile;
import com.lipi.notifica.database.Routine;
import com.lipi.notifica.database.Subject;
import com.lipi.notifica.database.Teacher;
import com.lipi.notifica.database.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Subject> mSubjects;
    private List<Teacher> mTeachers;
    private HashMap<String, List<Elective>> mElectives;
    private List<String> mElectiveGroups;
    private PClass mClass;
    private DbHelper mDbHelper;

    public ClassAdapter(Context context, PClass pClass) {
        mContext = context;
        mClass = pClass;

        mDbHelper = new DbHelper(context);
        refreshData();
    }

    public void refreshData() {
        // Get subjects, electives and teachers list

        mSubjects = new ArrayList<>();
        mTeachers = new ArrayList<>();
        mElectives = new HashMap<>();
        mElectiveGroups = new ArrayList<>();

        List<Long> sids = new ArrayList<>();
        List<Long> tids = new ArrayList<>();

        List<Routine> routines = mClass.getRoutines(mDbHelper);
        for (Routine r: routines) {
            List<Period> periods = r.getPeriods(mDbHelper);
            for (Period p: periods) {

                if (!sids.contains(p.subject)) {
                    sids.add(p.subject);
                    Subject s = p.getSubject(mDbHelper);

                    Elective elective = s.getElective(mDbHelper);
                    if (elective == null)
                        mSubjects.add(s);
                    else if (elective.p_class == mClass._id) {
                        if (mElectives.get(elective.p_group) == null) {
                            mElectives.put(elective.p_group, new ArrayList<Elective>());
                            mElectiveGroups.add(elective.p_group);
                        }

                        mElectives.get(elective.p_group).add(elective);
                    }
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
        int electivesSize = 0;
        for (String group: mElectiveGroups) {
            List<Elective> list = mElectives.get(group);
            electivesSize += list.size()+1;
        }

        return mSubjects.size() + 1
                + mTeachers.size() + 1
                + electivesSize;
    }

    @Override
    public int getItemViewType(int position) {
        int origin = mSubjects.size() + 1 + mTeachers.size()+1;
        for (String group: mElectiveGroups) {
            if (position == origin)
                return 0;
            else if (position < origin)
                break;

            origin += mElectives.get(group).size()+1;
        }

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

        // Subject
        if (position == 0) {
            HeaderViewHolder holder = (HeaderViewHolder)viewHolder;
            holder.header.setText("Subjects");
        }
        else if (position-1 < mSubjects.size()) {
            Subject subject = mSubjects.get(position - 1);

            ViewHolder holder = (ViewHolder) viewHolder;
            Utilities.fillProfileView(holder.view,
                    Color.parseColor(subject.color),
                    null, subject.name, null, null, subject.getShortName());
            holder.view.setSelected(false);
            holder.view.setOnClickListener(null);
        }

        // Teacher
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

            ViewHolder holder = (ViewHolder) viewHolder;
            Utilities.fillProfileView(holder.view,
                    Utilities.returnColor(teacher._id),
                    avatar, teacher.getUsername(mDbHelper), null, null, null);
            holder.view.setSelected(false);
            holder.view.setOnClickListener(null);
        }
        else {
            int origin = mSubjects.size() + 1 + mTeachers.size()+1;
            for (String group: mElectiveGroups) {
                List<Elective> electives = mElectives.get(group);

                if (origin == position) {
                    HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
                    String electiveHeader = "Elective " + group;
                    holder.header.setText(electiveHeader);
                }
                else if (position - origin-1 < electives.size()) {
                    final Elective elective = electives.get(position-origin-1);
                    Subject subject = elective.getSubject(mDbHelper);

                    ViewHolder holder = (ViewHolder) viewHolder;

                    if (!elective.selected)
                        Utilities.fillProfileView(holder.view,
                                Color.parseColor(subject.color),
                                null, subject.name, null, null, subject.getShortName());
                    else
                        Utilities.fillProfileView(holder.view,
                                Color.parseColor(subject.color),
                                BitmapFactory.decodeResource(mContext.getResources(),
                                        R.drawable.ic_check),
                                subject.name, null, null, null);


                    // Select new elective
                    if (!elective.selected)
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog dialog = new ProgressDialog(mContext);
                                dialog.setMessage("Selecting new elective");
                                dialog.show();

                                elective.select(mContext, new Elective.SelectCallback() {
                                    @Override
                                    public void onSelectionComplete() {
                                        refreshData();
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                }

                origin += electives.size() + 1;
            }
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
