package com.example.plantapermanente.empleados;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantapermanente.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class empleados_tabbed extends Fragment {
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.fragment_empleados_tabbed, container, false);
    SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this.getContext(), getChildFragmentManager());
    ViewPager viewPager = view.findViewById(R.id.view_pager);
    viewPager.setAdapter(sectionsPagerAdapter);
    TabLayout tabs = view.findViewById(R.id.tabs);
    tabs.setupWithViewPager(viewPager);
    return view;
    }

}
