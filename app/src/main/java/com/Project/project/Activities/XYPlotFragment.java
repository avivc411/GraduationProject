package com.Project.project.Activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.Project.project.R;


/**
 * Use the {@link XYPlotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class XYPlotFragment extends Fragment {
    public XYPlotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment XYPlotFragment.
     */
    public static XYPlotFragment newInstance() {
        XYPlotFragment fragment = new XYPlotFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_x_y_plot, container, false);
        fl.findViewById(R.id.plot);
        fl.findViewById(R.id.noDataText);
        fl.findViewById(R.id.leftArrowLayout);
        fl.findViewById(R.id.rightArrowLayout);
        return fl;
    }
}
