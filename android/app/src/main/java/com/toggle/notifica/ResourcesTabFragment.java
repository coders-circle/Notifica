package com.toggle.notifica;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResourcesTabFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_subject_resources,container,false);
        return v;
    }
}
