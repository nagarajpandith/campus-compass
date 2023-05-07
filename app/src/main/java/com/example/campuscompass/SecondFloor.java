package com.example.campuscompass;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SecondFloor extends Fragment {
    View v;
    String []places = {"LH301", "LH302", "BTL10", "BTL07", "BT HOD Cabin", "LH210", "LH211", "LH212", "Dept of Physical Education", "BT Staffroom", "Biokinetics Lab", "Instrumentation and Project Lab"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Location LH301 = new Location("LH211", new ArrayList<>(), getImageResourceId(getContext(), "LH211.jpg"), 2, false, 0f, null, null, null, null, null);

        v = inflater.inflate(R.layout.fragment_second_floor, container, false);
        return v;
    }

    public static int getImageResourceId(Context context, String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }
}